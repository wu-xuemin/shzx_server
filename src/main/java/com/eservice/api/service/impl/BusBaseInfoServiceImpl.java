package com.eservice.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.BusBaseInfoMapper;
import com.eservice.api.model.bus_base_info.BusBaseFullInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.model.user.User;
import com.eservice.api.service.BusBaseInfoService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@Service
@Transactional
public class BusBaseInfoServiceImpl extends AbstractService<BusBaseInfo> implements BusBaseInfoService {
    @Resource
    private BusBaseInfoMapper busBaseInfoMapper;

    @Resource
    private UserServiceImpl userService;

    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;

    private final Logger logger = LoggerFactory.getLogger(BusBaseInfoServiceImpl.class);

    public List<BusBaseFullInfo> getBusBaseInfo(String busNumber,String busLineName,String busDriverAccount,String busMomAccount,String busSupplierName, String schoolPartition,String keyWord){
        return busBaseInfoMapper.getBusBaseInfo(busNumber,busLineName,busDriverAccount,busMomAccount,busSupplierName,schoolPartition,keyWord);
    }
    public Result readFromExcel(@RequestParam String fileName ) {
        List<BusLineExcelHelper> list =   new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;
        BusBaseInfo busBaseInfo = null;

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("sheet1");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No sheet1 found");
            }
            // 循环行Row
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    busLineExcelHelper = new BusLineExcelHelper();
                    busBaseInfo = new BusBaseInfo();
                    HSSFCell busNumberCell = hssfRow.getCell(0);
                    HSSFCell busPlateNumberCell = hssfRow.getCell(8);
                    HSSFCell busPartitionCell = hssfRow.getCell(9);
                    HSSFCell busMomNameCell = hssfRow.getCell(10);
                    HSSFCell busMomPhoneCell = hssfRow.getCell(11);
                    HSSFCell busDriverNameCell = hssfRow.getCell(12);
                    HSSFCell busDriverPhoneCell = hssfRow.getCell(13);

                    busLineExcelHelper.setBusNumber(CommonService.getValue(busNumberCell));
                    busLineExcelHelper.setPlateNumber( CommonService.getValue(busPlateNumberCell));
                    busLineExcelHelper.setSchoolPartition(CommonService.getValue(busPartitionCell));
                    busLineExcelHelper.setBusMomName(CommonService.getValue(busMomNameCell));
                    busLineExcelHelper.setBusMomPhone(CommonService.getValue(busMomPhoneCell));
                    busLineExcelHelper.setBusDriverName(CommonService.getValue(busDriverNameCell));
                    busLineExcelHelper.setBusDriverPhone(CommonService.getValue(busDriverPhoneCell));
                    list.add(busLineExcelHelper);

                    /**
                     * 校车编号
                     */
                    busBaseInfo.setNumber(busLineExcelHelper.getBusNumber().split("\\.")[0]);
                    busBaseInfo.setPlateNumber(busLineExcelHelper.getPlateNumber());
                    busBaseInfo.setSchoolPartition(busLineExcelHelper.getSchoolPartition());
                    busBaseInfo.setValid(Constant.VALID_YES);

                    /**
                     * 如果有对应的user 巴士妈妈/司机则保存
                     */
                    User mom = userService.findBy("name", busLineExcelHelper.getBusMomName());
                    if (null != mom) {
                        busBaseInfo.setBusMom(mom.getId());
                    }
                    User driver = userService.findBy("name", busLineExcelHelper.getBusDriverName());
                    if (null != driver) {
                        busBaseInfo.setBusDriver(driver.getId());
                    }

                    /**
                     * 注意这里不能用findby(filedName ,...)
                     */
                    Class cl = Class.forName("com.eservice.api.model.bus_base_info.BusBaseInfo");
                    /**
                     * 成员名
                     */
                    Field fieldClassName = cl.getDeclaredField("number");
                    BusBaseInfo busBaseInfoExist = null;
                    busBaseInfoExist = busBaseInfoService.findBy(fieldClassName.getName(), busLineExcelHelper.getBusNumber().split("\\.")[0]);
                    /**
                     * 如果校车不存在，则增加
                     */
                    if ((null == busBaseInfoExist)) {
                        busBaseInfo.setCreateTime(new Date());
                        busBaseInfoService.save(busBaseInfo);
                        logger.info("add: =====" + rowNum + ":" + busBaseInfo.getNumber() + "/"
                                + busBaseInfo.getSchoolPartition() + "/"
                                + busBaseInfo.getBusMom());
                    } else {
                        /**
                         * 校车编号存在，则更新
                         */
                        busBaseInfo.setUpdateTime(new Date());
                        busBaseInfo.setId(busBaseInfoExist.getId());
                        busBaseInfoService.update(busBaseInfo);
                        logger.info("Update: =====" + rowNum + ":" + busBaseInfo.getNumber() + "/"
                                + busBaseInfo.getSchoolPartition() + "/"
                                + busBaseInfo.getBusMom() + "/" + busBaseInfo.getBusMom());
                    }
                }
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    public String getBusNumberByBusMomAccount(String busMomAccount){
        return busBaseInfoMapper.getBusNumberByBusMomAccount(busMomAccount);
    }
    public String getBusNumberByDriverAccount(String driverAccount){
        return busBaseInfoMapper.getBusNumberByDriverAccount(driverAccount);
    }

    public List<BusBaseInfo> listByNumber(){
        return busBaseInfoMapper.listByNumber();
    }

    public Integer getURLContentAndCreateBusBaseInfo(String urlStr){
        Integer addedBusBaseInfoSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject= JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                BusBaseInfo busBaseInfo = new BusBaseInfo();
                JSONObject jo = ja.getJSONObject(i);
                String schoolPartition = jo.getString("area");
                String number = jo.getString("id");
                String plateNumber = jo.getString("plate_number");
                String busMomName = jo.getString("bus_mom_name");

                busBaseInfo.setSchoolPartition(schoolPartition);
                busBaseInfo.setNumber(number);
                busBaseInfo.setPlateNumber(plateNumber);

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User busMomExist = null;
                busMomExist = userService.findBy(fieldUserAccount.getName(), busMomName);
                if(busMomExist != null) {
                    busBaseInfo.setBusMom(busMomExist.getId());
                } else {
                    logger.warn(" no busMom found by account: " + busMomName);
                }
                busBaseInfo.setCreateTime(new Date());
                busBaseInfo.setValid(Constant.VALID_YES);

                Class cl2 = Class.forName("com.eservice.api.model.bus_base_info.BusBaseInfo");
                Field field = cl2.getDeclaredField("number");
                BusBaseInfo busBaseInfoExist = busBaseInfoService.findBy(field.getName(), number);
                if(busBaseInfoExist == null) {
                    busBaseInfoService.save(busBaseInfo);
                    logger.info("added busBaseInfo: " + busBaseInfo.getNumber() + " 号线");
                    addedBusBaseInfoSum ++;
                } else {
                    logger.info(" already exist busBaseInfo: " +  busBaseInfo.getNumber());
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
            return 0;
        }
        return addedBusBaseInfoSum;
    }

    public Result updateBusBaseInfoDriverAndBusMom(String urlStr)
    {
        Integer updatedBusDriverSum = 0;
        Integer updatedBusMomSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String number = jo.getString("id");
                String busDriverName = jo.getString("driver_name");
                String busMomName = jo.getString("bus_mom_name");

                Class classBusBaseInfo = Class.forName("com.eservice.api.model.bus_base_info.BusBaseInfo");
                Field fieldNumber = classBusBaseInfo.getDeclaredField("number");
                BusBaseInfo busBaseInfo = busBaseInfoService.findBy(fieldNumber.getName(), number);
                if (busBaseInfo == null) {
                    logger.info("can not find busBaseInfo by bus number " + number);
                    continue;
                } else {
                    Class cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldUserAccount = cl.getDeclaredField("account");
                    User busDriverExist = null;
                    busDriverExist = userService.findBy(fieldUserAccount.getName(), busDriverName);
                    User busMomExist = userService.findBy(fieldUserAccount.getName(), busMomName);
                    if (busDriverExist != null) {
                        // driver字段为空时才更新，便于统计数量
                        if (busBaseInfo.getBusDriver() == null) {
                            busBaseInfo.setBusDriver(busDriverExist.getId());
                            busBaseInfoService.update(busBaseInfo);
                            logger.info("update busBaseInof.bus_driver: " + busDriverName + " for bus " + busBaseInfo.getNumber());
                            updatedBusDriverSum++;
                        } else {
                            logger.info("driver already exist in bus " + number);
                        }
                    } else {
                        logger.info(" can not find driver by: " + busDriverName);
                    }
                    if (busMomExist != null) {
                        if (busBaseInfo.getBusMom() == null) {
                            busBaseInfo.setBusMom(busMomExist.getId());
                            busBaseInfo.setUpdateTime(new Date());
                            busBaseInfoService.update(busBaseInfo);
                            logger.info("update busBaseInof.bus_mom: " + busMomName + " for bus " + busBaseInfo.getNumber());
                            updatedBusMomSum++;
                        } else {
                            logger.info("busMom already exist in bus " + number);
                        }
                    } else {
                        logger.info(" can not find busMom by: " + busMomName);
                    }
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
            return ResultGenerator.genFailResult(e.getMessage());
        }
        return  ResultGenerator.genSuccessResult("updatedBusDriverSum: " + updatedBusDriverSum + " updatedBusMomSum: " + updatedBusMomSum + "; \r\n" );
    }
}
