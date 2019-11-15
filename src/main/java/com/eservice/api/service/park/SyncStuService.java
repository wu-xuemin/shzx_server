package com.eservice.api.service.park;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.park.model.FaceDTO;
import com.eservice.api.service.park.model.FaceUserInfo;
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
     * 员工列表
     */
    private ArrayList<FaceUserInfo> staffList = new ArrayList<>();

    private ThreadPoolTaskExecutor mExecutor;

    private static boolean SYNCING = false;

    /**
     * 每小时获取一次员工信息
     */
    @Scheduled(initialDelay = 10000, fixedRate = 1000 * 60 * 10)
    public void fetchStaffScheduled() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity httpEntity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/fetchFace", httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == ResponseCode.OK) {
                String body = responseEntity.getBody();
                if (body != null) {
                    processStaffResponse(body);
                } else {
                    fetchStaffScheduled();
                }
            }
        } catch (HttpClientErrorException exception) {
            logger.error("Fetch student error.");
        }
    }


    private void processStaffResponse(String body) {
        FeatureResponseData responseModel = JSONObject.parseObject(body, FeatureResponseData.class);
        if (responseModel != null && responseModel.getCode() == ResponseCode.OK) {
            ArrayList<FaceUserInfo> tmpList = responseModel.getData();
            if (tmpList != null && tmpList.size() > 0) {
                ArrayList<FaceUserInfo> resultList = new ArrayList<>();
                for (int i = 0; i < tmpList.size(); i++) {
                    if(tmpList.get(i).getCardNum().toUpperCase().startsWith("G") || tmpList.get(i).getCardNum().toUpperCase().startsWith("Z")) {
                        resultList.add(tmpList.get(i));
                    }
                }
                staffList = resultList;
                logger.info("Student number: " + staffList.size());
            }
        }
    }

    public String syncStuPicToFacePlatform(List<Student> platformStuList) {
        String result = "";
        SYNCING = true;
        ArrayList<Student> needSyncStuList = new ArrayList<>();
        ArrayList<Student> syncSuccessList = new ArrayList<>();
        ArrayList<Student> syncFailList = new ArrayList<>();
        ArrayList<Student> picNotExistList = new ArrayList<>();
        List<FaceUserInfo> facePlatformStuList = getStudentList();
        logger.info("platformStuList size: " + platformStuList.size());
        for (int i = 0; i < platformStuList.size(); i++) {
            boolean exist = false;
            for (int j = 0; j < facePlatformStuList.size() && !exist; j++) {
                if(facePlatformStuList.get(j).getCardNum().equals(platformStuList.get(i).getStudentNumber())) {
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
                File picFile = new File(STUDENT_IMG_DIR + needSyncStuList.get(i).getHeadImg());
                if(!picFile.exists()) {
                    picNotExistList.add(needSyncStuList.get(i));
                } else {
                    String picBase64Data = Util.getBase64ImgStr(picFile.getPath());
                    try {
                        //同步人脸
                        if(addStudentInFace(picBase64Data, needSyncStuList.get(i))) {
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

    public boolean addStudentInFace(String base64Data, Student stu) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        FaceDTO faceDTO = new FaceDTO();
        faceDTO.setBase64(base64Data);
        faceDTO.setCardNum(stu.getStudentNumber());
        faceDTO.setName(stu.getName());
        HttpEntity httpEntity = new HttpEntity<>(JSON.toJSONString(faceDTO), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/add/student", httpEntity, String.class);
        if (responseEntity.getStatusCodeValue() == ResponseCode.OK) {
            String body = responseEntity.getBody();
            if (body != null) {
                Result responseModel = JSONObject.parseObject(body, Result.class);
                if(responseModel.getCode() == ResponseCode.OK) {
                    return true;
                } else {
                    logger.error("Add student face in face platform : " + responseModel.getMessage());
                }
            }
        }
        return false;
    }

    public boolean updateStudentInFace(String base64Data, Student stu) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        FaceDTO faceDTO = new FaceDTO();
        faceDTO.setBase64(base64Data);
        faceDTO.setCardNum(stu.getStudentNumber());
        faceDTO.setName(stu.getName());
        HttpEntity httpEntity = new HttpEntity<>(JSON.toJSONString(faceDTO), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/update/student", httpEntity, String.class);
        if (responseEntity.getStatusCodeValue() == ResponseCode.OK) {
            String body = responseEntity.getBody();
            if (body != null) {
                Result responseModel = JSONObject.parseObject(body, Result.class);
                if(responseModel.getCode() == ResponseCode.OK) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<FaceUserInfo> getStudentList() {
        return staffList;
    }
}
