package com.eservice.api.service.park;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.service.park.model.Condition;
import com.eservice.api.service.park.model.RepoIdBean;
import com.eservice.api.service.park.model.ResponseModel;
import com.eservice.api.service.park.model.WinVisitorRecord;
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
                boolean changed = false;
                if (tmpList != null && tmpList.size() != 0) {
                    if (tmpList.size() != staffList.size()) {
                        changed = true;
                    } else {
                        if (!tmpList.equals(staffList)) {
                            changed = true;
                        }
                    }
                    if (changed) {
                        logger.info("The number of BusMom：{} ==> {}", staffList.size(), tmpList.size());
                    }
                    if (mExecutor == null) {
                        initExecutor();
                    }
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            HashSet<String> hashSet = new HashSet<>();
                            for (WinVisitorRecord item: tmpList) {
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
                        }
                    });
                    staffList = tmpList;
                }
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

    public ArrayList<WinVisitorRecord> getBusMonList() {
        return staffList;
    }
}
