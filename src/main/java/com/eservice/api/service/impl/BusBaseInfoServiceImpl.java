package com.eservice.api.service.impl;

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
    @PostMapping("/readFromExcel")
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
                    busBaseInfo.setNumber(busLineExcelHelper.getBusNumber());
                    busBaseInfo.setPlateNumber(busLineExcelHelper.getPlateNumber());
                    busBaseInfo.setSchoolPartition(busLineExcelHelper.getSchoolPartition());

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
                    busBaseInfoExist = busBaseInfoService.findBy(fieldClassName.getName(), busLineExcelHelper.getBusNumber());
                    /**
                     * 如果校车不存在，则增加
                     */
                    if ((null == busBaseInfoExist)) {
                        busBaseInfoService.save(busBaseInfo);
                        logger.info("add: =====" + rowNum + ":" + busBaseInfo.getNumber() + "/"
                                + busBaseInfo.getSchoolPartition() + "/"
                                + busBaseInfo.getBusMom());
                    } else {
                        /**
                         * 校车编号存在，则更新
                         */
                        busBaseInfoService.update(busBaseInfo);
                        logger.info("Update: =====" + rowNum + ":" + busBaseInfo.getNumber() + "/"
                                + busBaseInfo.getSchoolPartition() + "/"
                                + busBaseInfo.getBusMom());
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


}
