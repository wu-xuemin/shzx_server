package com.eservice.api.service.park;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.model.user.User;
import com.eservice.api.service.impl.BusBaseInfoServiceImpl;
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

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class SyncBusMomService {
    private final static Logger logger = LoggerFactory.getLogger(SyncBusMomService.class);

    @Value("${park_base_url}")
    private String PARK_BASE_URL;

    @Value("${busmom_repo_id}")
    private Integer BUSMOM_REPO_ID;

    @Value("${driver_repo_id}")
    private Integer DRIVER_REPO_ID;

    @Value("${user_img_dir}")
    private String USER_IMG_DIR;

    @Autowired
    private RestTemplate restTemplate;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean SYNCING = false;

    /**
     * BusMom/Driver列表
     */
    private ArrayList<FaceUserInfo> staffList = new ArrayList<>();
    /**
     * 每个小时获取一次busmom信息
     */
    @Scheduled(initialDelay = 15000,fixedRate = 1000 * 60 * 10)
    public void fetchManagerScheduled() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity httpEntity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/fetchFace", httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == ResponseCode.OK) {
                String body = responseEntity.getBody();
                if (body != null) {
                    processManagerResponse(body);
                } else {
                    fetchManagerScheduled();
                }
            }
        } catch (HttpClientErrorException exception) {
            logger.error("Fetch manager error.");
        }
    }

    private void processManagerResponse(String body) {
        FeatureResponseData responseModel = JSONObject.parseObject(body, FeatureResponseData.class);
        if (responseModel != null && responseModel.getCode() == ResponseCode.OK) {
            ArrayList<FaceUserInfo> tmpList = responseModel.getData();
            if (tmpList != null && tmpList.size() > 0) {
                ArrayList<FaceUserInfo> resultList = new ArrayList<>();
                for (int i = 0; i < tmpList.size(); i++) {
                    if(!tmpList.get(i).getCardNum().toUpperCase().startsWith("G") && !tmpList.get(i).getCardNum().toUpperCase().startsWith("Z")) {
                        resultList.add(tmpList.get(i));
                    }
                }
                staffList = resultList;
                logger.info("Manager number: " + staffList.size());
            }
        }
    }

    public String syncManagerPicToFacePlatform(List<User> platformBusMomList) {
        String result = "";
        SYNCING = true;
        ArrayList<User> needSyncBusMomList = new ArrayList<>();
        ArrayList<User> syncSuccessList = new ArrayList<>();
        ArrayList<User> syncFailList = new ArrayList<>();
        ArrayList<User> picNotExistList = new ArrayList<>();
        List<FaceUserInfo> facePlatformBusMomList = getManagerList();
        for (int i = 0; i < platformBusMomList.size(); i++) {
            boolean exist = false;
            for (int j = 0; j < facePlatformBusMomList.size() && !exist; j++) {
                if(facePlatformBusMomList.get(j).getCardNum().equals(platformBusMomList.get(i).getPhone())) {
                    exist = true;
                }
            }
            if(!exist) {
                needSyncBusMomList.add(platformBusMomList.get(i));
            }
        }
        if(needSyncBusMomList.size() == 0) {
            result = "管理员照片已同步";
        } else {
            for (int i = 0; i < needSyncBusMomList.size(); i++) {
                File dir = new File(USER_IMG_DIR);
                if(!dir.exists()) {
                    dir.mkdirs();
                }
                // e.g. 18019008369_busmom36_孙美燕.jpg
                File picFile = new File(USER_IMG_DIR + needSyncBusMomList.get(i).getHeadImage());
                if(!picFile.exists()) {
                    picNotExistList.add(needSyncBusMomList.get(i));
                } else {
                    String picBase64Data = Util.getBase64ImgStr(picFile.getPath());
                    try {
                        if(addManagerInFace(picBase64Data, needSyncBusMomList.get(i))) {
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
        fetchManagerScheduled();
        return result;
    }

    public boolean addManagerInFace(String base64Data, User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        FaceDTO faceDTO = new FaceDTO();
        faceDTO.setBase64(base64Data);
        faceDTO.setCardNum(user.getPhone());
        faceDTO.setName(user.getName());
        HttpEntity httpEntity = new HttpEntity<>(JSON.toJSONString(faceDTO), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/add/manager", httpEntity, String.class);
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


    public boolean updateManagerInFace(String base64Data, User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        FaceDTO faceDTO = new FaceDTO();
        faceDTO.setBase64(base64Data);
        faceDTO.setCardNum(user.getPhone());
        faceDTO.setName(user.getName());
        HttpEntity httpEntity = new HttpEntity<>(JSON.toJSONString(faceDTO), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(PARK_BASE_URL + "/face/update/manager", httpEntity, String.class);
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

    public ArrayList<FaceUserInfo> getManagerList() {
        return staffList;
    }
}
