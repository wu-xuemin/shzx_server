package com.eservice.api.service.park;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.park.model.Condition;
import com.eservice.api.service.park.model.RepoIdBean;
import com.eservice.api.service.park.model.ResponseModel;
import com.eservice.api.service.park.model.WinVisitorRecord;
import com.eservice.api.service.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
public class SyncStuService {
    private final static Logger logger = LoggerFactory.getLogger(SyncStuService.class);

    @Value("${park_base_url}")
    private String PARK_BASE_URL;

    @Value("${student_repo_id}")
    private Integer STUDENT_REPO_ID;

    @Value("${student_img_dir}")
    private String STUDENT_IMG_DIR;

    @Autowired
    private RestTemplate restTemplate;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Token
     */
    private String token;
    /**
     * 员工列表
     */
    private ArrayList<WinVisitorRecord> staffList = new ArrayList<>();

    @Autowired
    private TokenService tokenService;

    private ThreadPoolTaskExecutor mExecutor;

    private static boolean SYNCING = false;

    /**
     * 每分钟获取一次员工信息
     */
    @Scheduled(initialDelay = 5000, fixedRate = 1000 * 60)
    public void fetchStaffScheduled() {
        token = tokenService.getToken();
        if (token != null) {
            HashMap<String, Object> postParameters = new HashMap<>();
            postParameters.put("start", 0);
            postParameters.put("limit", 0);
            //Condition
            Condition condition = new Condition();
            //"1"返回用户信息
            condition.setType(1);
            RepoIdBean idBean = new RepoIdBean();
            List<Integer> repoIdList = new ArrayList<>();
            repoIdList.add(STUDENT_REPO_ID);
            idBean.setIn(repoIdList);
            condition.setRepo_id(idBean);
            postParameters.put("condition", condition);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            headers.add(HttpHeaders.AUTHORIZATION, token);
            String jsonString = JSON.toJSONString(postParameters);
            jsonString = jsonString.replace("in", "$in");
            jsonString = jsonString.replace("gte", "$gte");
            HttpEntity httpEntity = new HttpEntity<>(jsonString, headers);
            try {
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/v1/framework/face/search", httpEntity, String.class);
                if (responseEntity.getStatusCodeValue() == ResponseCode.OK) {
                    String body = responseEntity.getBody();
                    if (body != null) {
                        processStaffResponse(body);
                    } else {
                        fetchStaffScheduled();
                    }
                }
            } catch (HttpClientErrorException exception) {
                if (exception.getStatusCode().value() == ResponseCode.TOKEN_INVALID) {
                    token = tokenService.getToken();
                    if (token != null) {
                        fetchStaffScheduled();
                    }
                }
            }
        }
    }


    private void processStaffResponse(String body) {
        ResponseModel responseModel = JSONObject.parseObject(body, ResponseModel.class);
        if (responseModel != null && responseModel.getResults() != null) {
            ArrayList<WinVisitorRecord> tmpList = (ArrayList<WinVisitorRecord>)JSONArray.parseArray(responseModel.getResults(), WinVisitorRecord.class);
            if (tmpList != null && tmpList.size() > 0) {
                if (mExecutor == null) {
                    initExecutor();
                }
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        HashSet<String> hashSet = new HashSet<>();
                        ArrayList<WinVisitorRecord> qualityPassedList =  new ArrayList<>();
                        for (WinVisitorRecord item: tmpList) {
                            if(!item.isSuccess()) {
                                logger.error("照片质量未通过学生：学号:{}, 姓名:{}", item.getMeta().getExternal_id(),item.getMeta().getName());
                                continue;
                            } else {
                                qualityPassedList.add(item);
                            }
                            if(item.getMeta() == null) {
                                logger.error("信息错误学生：ID:{}, URL:{}", item.getPerson_id_str(),item.getImage_uri());
                            } else {
                                if(item.getMeta().getExternal_id() == null || item.getMeta().getExternal_id().equals("")) {
                                    logger.error("学号为空学生：{} {}", item.getMeta().getName(),item.getMeta().getExternal_id());
                                } else if(!hashSet.contains(item.getMeta().getExternal_id())) {
                                    hashSet.add(item.getMeta().getExternal_id());
                                } else {
                                    logger.error("学号重复的学生：{} {}", item.getMeta().getName(),item.getMeta().getExternal_id());
                                }
                            }
                        }
                        if(!SYNCING) {
                            logger.info("The number of student：{} ==> {}", staffList.size(), qualityPassedList.size());
                            staffList = qualityPassedList;
                        }
                    }
                });
            }
        }
    }

    private void initExecutor() {
        mExecutor = new ThreadPoolTaskExecutor();
        mExecutor.setCorePoolSize(2);
        mExecutor.setMaxPoolSize(5);
        mExecutor.setThreadNamePrefix("YTTPS-");
        mExecutor.initialize();
    }

    public String syncStuPicToFacePlatform(List<Student> platformStuList) {
        String result = "";
        SYNCING = true;
        ArrayList<Student> needSyncStuList = new ArrayList<>();
        ArrayList<Student> syncSuccessList = new ArrayList<>();
        ArrayList<Student> syncFailList = new ArrayList<>();
        ArrayList<Student> picNotExistList = new ArrayList<>();
        List<WinVisitorRecord> facePlatformStuList = getStudentList();
        for (int i = 0; i < platformStuList.size(); i++) {
            boolean exist = false;
            for (int j = 0; j < facePlatformStuList.size() && !exist; j++) {
                if(facePlatformStuList.get(j).getMeta().getExternal_id().equals(platformStuList.get(i).getStudentNumber())) {
                    exist = true;
                }
            }
            if(!exist) {
                needSyncStuList.add(platformStuList.get(i));
            }
        }
        if(needSyncStuList.size() == 0) {
            result = "学生照片已同步";
        } else {
            for (int i = 0; i < needSyncStuList.size(); i++) {
                File dir = new File(STUDENT_IMG_DIR);
                if(!dir.exists()) {
                    dir.mkdirs();
                }
                File picFile = new File(STUDENT_IMG_DIR + needSyncStuList.get(i).getStudentNumber() + "_" + needSyncStuList.get(i).getName() + ".png");
                if(!picFile.exists()) {
                    picNotExistList.add(needSyncStuList.get(i));
                } else {
                    String picBase64Data = Util.getBase64ImgStr(picFile.getPath());
                    try {
                        if(uploadStuPic(picBase64Data, needSyncStuList.get(i))) {
                            syncSuccessList.add(needSyncStuList.get(i));
                        } else {
                            syncFailList.add(needSyncStuList.get(i));
                        }
                    } catch (Exception e) {
                        syncFailList.add(needSyncStuList.get(i));
                    }

                }
            }

            result += "================ 同步成功学生 ===============";
            result += "\n";
            result += "总人数：" + syncSuccessList.size();
            result += "\n";
            result += "\n";

            result += "================ 同步失败学生 ===============";
            result += "\n";
            result += "总人数：" + syncFailList.size();
            result += "\n";
            for (Student stu: syncFailList) {
                result += stu.getStudentNumber() + "，";
            }
            result += "\n";

            result += "================ 照片缺少学生 ===============";
            result += "\n";
            result += "总人数：" + picNotExistList.size();
            result += "\n";
            for (Student stu: picNotExistList) {
                result += stu.getStudentNumber() + "，";
            }
        }

        SYNCING = false;
        //update student
        fetchStaffScheduled();
        return result;
    }

    public boolean uploadStuPic(String base64Data, Student stu) {
        token = tokenService.getToken();
        if (token != null) {
            HashMap<String, Object> postParameters = new HashMap<>();
            postParameters.put("repo_id", STUDENT_REPO_ID);
            postParameters.put("image_content_base64", base64Data);
            postParameters.put("image_type", 3);
            WinVisitorRecord.MetaBean metaBean = new WinVisitorRecord.MetaBean();
            metaBean.setExternal_id(stu.getStudentNumber());
            metaBean.setName(stu.getName());
            postParameters.put("meta", metaBean);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            headers.add(HttpHeaders.AUTHORIZATION, token);
            String jsonString = JSON.toJSONString(postParameters);
            HttpEntity httpEntity = new HttpEntity<>(jsonString, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/v1/framework/face", httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == ResponseCode.OK) {
                String body = responseEntity.getBody();
                if (body != null) {
                    ResponseModel responseModel = JSONObject.parseObject(body, ResponseModel.class);
                    if(responseModel.getRtn() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<WinVisitorRecord> getStudentList() {
        return staffList;
    }
}
