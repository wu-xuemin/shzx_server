package com.eservice.api.service.park;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.model.user.User;
import com.eservice.api.service.park.model.Condition;
import com.eservice.api.service.park.model.RepoIdBean;
import com.eservice.api.service.park.model.ResponseModel;
import com.eservice.api.service.park.model.WinVisitorRecord;
import com.eservice.api.service.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SyncBusMomService {
    private final static Logger logger = LoggerFactory.getLogger(SyncBusMomService.class);

    @Value("${park_base_url}")
    private String PARK_BASE_URL;

    @Value("${busmom_repo_id}")
    private Integer BUSMOM_REPO_ID;

    @Value("${user_img_dir}")
    private String USER_IMG_DIR;

    @Autowired
    private RestTemplate restTemplate;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean SYNCING = false;
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

    /**
     * 每分钟获取一次员工信息
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void fetchBusMomScheduled() {
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
            repoIdList.add(BUSMOM_REPO_ID);
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
                        processBusMomResponse(body);
                    } else {
                        fetchBusMomScheduled();
                    }
                }
            } catch (HttpClientErrorException exception) {
                if (exception.getStatusCode().value() == ResponseCode.TOKEN_INVALID) {
                    token = tokenService.getToken();
                    if (token != null) {
                        fetchBusMomScheduled();
                    }
                }
            }
        }
    }


    private void processBusMomResponse(String body) {
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
                                logger.error("照片质量未通过BusMom：手机号:{}, 姓名:{}", item.getMeta().getExternal_id(),item.getMeta().getName());
                                continue;
                            } else {
                                qualityPassedList.add(item);
                            }
                            if(item.getMeta() == null) {
                                logger.error("信息错误BusMom：ID:{}, URL:{}", item.getPerson_id_str(),item.getImage_uri());
                            } else {
                                if(item.getMeta().getExternal_id() == null || item.getMeta().getExternal_id().equals("")) {
                                    logger.error("手机号为空BusMom：{} {}", item.getMeta().getName(),item.getMeta().getExternal_id());
                                } else if(!hashSet.contains(item.getMeta().getExternal_id())) {
                                    hashSet.add(item.getMeta().getExternal_id());
                                } else {
                                    logger.error("手机号重复的BusMom：{} {}", item.getMeta().getName(),item.getMeta().getExternal_id());
                                }
                            }
                        }
                        if(!SYNCING) {
                            logger.info("The number of busMom：{} ==> {}", staffList.size(), qualityPassedList.size());
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

    public String syncBusMomPicToFacePlatform(List<User> platformBusMomList) {
        String result = "";
        SYNCING = true;
        ArrayList<User> needSyncBusMomList = new ArrayList<>();
        ArrayList<User> syncSuccessList = new ArrayList<>();
        ArrayList<User> syncFailList = new ArrayList<>();
        ArrayList<User> picNotExistList = new ArrayList<>();
        List<WinVisitorRecord> facePlatformBusMomList = getBusMonList();
        for (int i = 0; i < platformBusMomList.size(); i++) {
            boolean exist = false;
            for (int j = 0; j < facePlatformBusMomList.size() && !exist; j++) {
                if(facePlatformBusMomList.get(j).getMeta().getExternal_id().equals(platformBusMomList.get(i).getPhone())) {
                    exist = true;
                }
            }
            if(!exist) {
                needSyncBusMomList.add(platformBusMomList.get(i));
            }
        }
        if(needSyncBusMomList.size() == 0) {
            result = "BusMom照片已同步";
        } else {
            for (int i = 0; i < needSyncBusMomList.size(); i++) {
                File dir = new File(USER_IMG_DIR);
                if(!dir.exists()) {
                    dir.mkdirs();
                }
                File picFile = new File(USER_IMG_DIR + needSyncBusMomList.get(i).getPhone() + "_" + needSyncBusMomList.get(i).getName() + ".png");
                if(!picFile.exists()) {
                    picNotExistList.add(needSyncBusMomList.get(i));
                } else {
                    String picBase64Data = Util.getBase64ImgStr(picFile.getPath());
                    try {
                        if(uploadBusMomPic(picBase64Data, needSyncBusMomList.get(i))) {
                            syncSuccessList.add(needSyncBusMomList.get(i));
                        } else {
                            syncFailList.add(needSyncBusMomList.get(i));
                        }
                    } catch (Exception e) {
                        syncFailList.add(needSyncBusMomList.get(i));
                    }

                }
            }

            result += "================ 同步成功BusMom ===============";
            result += "\n";
            result += "总人数：" + syncSuccessList.size();
            result += "\n";
            result += "\n";

            result += "================ 同步失败BusMom ===============";
            result += "\n";
            result += "总人数：" + syncFailList.size();
            result += "\n";
            for (User busMon: syncFailList) {
                result += busMon.getName() + "，";
            }
            result += "\n";

            result += "================ 照片缺少BusMom ===============";
            result += "\n";
            result += "总人数：" + picNotExistList.size();
            result += "\n";
            for (User busMom: picNotExistList) {
                result += busMom.getName() + "，";
            }
        }

        SYNCING = false;
        //update student
        fetchBusMomScheduled();
        return result;
    }

    private boolean uploadBusMomPic(String base64Data, User busMom) {
        token = tokenService.getToken();
        if (token != null) {
            HashMap<String, Object> postParameters = new HashMap<>();
            postParameters.put("repo_id", BUSMOM_REPO_ID);
            postParameters.put("image_content_base64", base64Data);
            postParameters.put("image_type", 3);
            WinVisitorRecord.MetaBean metaBean = new WinVisitorRecord.MetaBean();
            metaBean.setExternal_id(busMom.getPhone());
            metaBean.setName(busMom.getName());
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

    public ArrayList<WinVisitorRecord> getBusMonList() {
        return staffList;
    }
}
