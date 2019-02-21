package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.BusLineMapper;
import com.eservice.api.model.bus_base_info.BusBaseFullInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.service.BusLineService;
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
import java.text.ParseException;
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
public class BusLineServiceImpl extends AbstractService<BusLine> implements BusLineService {
    @Resource
    private BusLineMapper busLineMapper;

    @Resource
    private UserServiceImpl userService;

    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;

    @Resource
    private BusLineServiceImpl busLineService;

    private final Logger logger = LoggerFactory.getLogger(BusLineServiceImpl.class);

    public  List<BusLineInfo> getBusLineInfoByBusMomAccount (String busMomAccount ){
        return busLineMapper.getBusLineInfoByBusMomAccount(busMomAccount);
    }
    public List<BusLineInfo> getBusLineInfoByBusDriverAccount(String busDriverAccount){
        return busLineMapper.getBusLineInfoByBusDriverAccount(busDriverAccount);
    }

    public List<BusLineInfo> getBusLineInfoBySchoolPartition(String schoolPartition){
        return busLineMapper.getBusLineInfoBySchoolPartition(schoolPartition);
    }

    public List<StudentInfo> getStudents(String busNumber,String busMode){
        return busLineMapper.getStudents(busNumber,busMode);
    }

    public BusLine getBusLineInfoByBusNumberAndBusMode( String busNumber,String busMode ){
        return busLineMapper.getBusLineInfoByBusNumberAndBusMode(busNumber,busMode);
    }
    public List<BusLine> getBusLinesByMode( String busMode ){
        return busLineMapper.getBusLinesByMode(busMode);
    }

    public List<BusLine> list(String queryKey) {
        return busLineMapper.list(queryKey);
    }

    public List<BusLine> getBusLineByBusNumber(String busNumber) {
        return busLineMapper.getBusLineByBusNumber(busNumber);
    }

    public Result readFromExcel(@RequestParam String fileName) {
        List<BusLineExcelHelper> list = new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;
        BusLine busLine = null;

        File file = new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("sheet1");
            String currentStations;

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No sheet1 found");
            }
            // 循环行Row
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    busLine = new BusLine();
                    HSSFCell busNumberCell = hssfRow.getCell(0);
                    HSSFCell stationTimeRemarkCell = hssfRow.getCell(14);
                    HSSFCell stationNameCell = hssfRow.getCell(2);
                    busLineExcelHelper = new BusLineExcelHelper();
                    busLineExcelHelper.setBusNumber(CommonService.getValue(busNumberCell));
                    busLineExcelHelper.setTimeRemark(CommonService.getValue(stationTimeRemarkCell));
                    ///
                    busLineExcelHelper.setTimeRemark(CommonService.getValue(stationTimeRemarkCell));
                    String busModeGot = CommonService.getBusModeByTime(stationTimeRemarkCell);
                    if (!busModeGot.equals(Constant.FAIL)) {
                        busLineExcelHelper.setMode(busModeGot);
                    }
                    busLineExcelHelper.setName(CommonService.getValue(busNumberCell).split("\\.")[0] + "号车_" + busLineExcelHelper.getMode());
                    busLineExcelHelper.setStationName(CommonService.getValue(stationNameCell));
                    list.add(busLineExcelHelper);

                    BusBaseInfo busBaseInfo = busBaseInfoService.findBy("number", busLineExcelHelper.getBusNumber().split("\\.")[0]);
                    if (busBaseInfo == null) {
                        logger.info("can not find bus by bus number " + busLineExcelHelper.getBusNumber());
                    } else {
                        busLine.setBusBaseInfo(busBaseInfo.getId());
                    }
                    busLine.setMode(busLineExcelHelper.getMode());
                    busLine.setName(busLineExcelHelper.getName());
                    busLine.setValid(1);

                    /**
                     * 逐个加入站点
                     * 根据线路名字查询线路
                     */
                    Class cl = Class.forName("com.eservice.api.model.bus_line.BusLine");
                    /**
                     * 成员名
                     */
                    Field fieldClassName = cl.getDeclaredField("name");
                    BusLine busLineExist = null;
                    busLineExist = busLineService.findBy(fieldClassName.getName(), busLine.getName());

                    /**
                     * 如果线路不存在，则增加线路
                     */
                    if (null == busLineExist) {
                        busLine.setCreateTime(new Date());
                        busLine.setStations(busLineExcelHelper.getStationName());
                        busLineService.save(busLine);
                        logger.info("add: =====" + rowNum + ":" + busLine.getName() + "/"
                                + busLine.getMode() + "/"
                                + busLine.getStations());
                    } else {
                        currentStations = busLineExist.getStations();
                        if (busLineExist.getStations().contains(busLineExcelHelper.getStationName())) {
                            /**
                             * 如果线路存在，而该站点也存在，则不做处理
                             */
//                            logger.info("站点 " +  busLineExcelHelper.getStationName() + "已存在，不重复加入");
                        } else {
                            /**
                             * 如果线路存在，而该站点不存在，则增加站点（目前用逗号隔离站点），并更新线路
                             */
                            logger.info("currentStations: " + currentStations);
                            logger.info("站点 " + busLineExcelHelper.getStationName() + ", 还不存在，现在加入");
                            currentStations = currentStations + "," + busLineExcelHelper.getStationName();
                            /**
                             * UPDATE 需要由ID, 否则更新无效
                             */
                            busLine.setId(busLineExist.getId());
                            busLine.setStations(currentStations);
                            busLine.setUpdateTime(new Date());
                            logger.info("new currentStations: " + currentStations);
                            busLineService.update(busLine);
                            logger.info("Updated: =====" + rowNum + "行:" + busLine.getName() + "/" + busLine.getMode() + "/" + busLine.getStations());
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
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

    public void cleanAndCreateAfternoonBusLine(String busLineSame) {

        BusLine busLineNoon = new BusLine();
        String stationsNoon = null;
        List<BusLine> busLineListMorning = busLineService.getBusLinesByMode(Constant.BUS_MODE_MORNING);
        List<BusLine> busLineListNoonOld = busLineService.getBusLinesByMode(Constant.BUS_MODE_AFTERNOON);
        for (BusLine bln: busLineListNoonOld) {
            busLineService.deleteById(bln.getId());
        }
        logger.info(busLineListNoonOld.size() + " old noonLines cleaned ");

        String[] stationsArrMorning;
        //改 busBaseInfo, mode,name,stations,
        List<BusLine> busLineListNoon = new ArrayList<>();
        for (BusLine blm: busLineListMorning) {

            List<String> stationListNoon = new ArrayList<>();
            busLineNoon.setBusBaseInfo(blm.getBusBaseInfo());
            busLineNoon.setMode(Constant.BUS_MODE_AFTERNOON);
            busLineNoon.setName(blm.getName().replace(Constant.BUS_MODE_MORNING,Constant.BUS_MODE_AFTERNOON));
            /**
             * 站点倒个序 （除了1,8,10,11,16,17,20,31,41,60,67,74,86,97）
             */
            if(!blm.getName().replace("号车_早班","").equals("1")
                    && !blm.getName().replace("号车_早班","").equals("8")
                    && !blm.getName().replace("号车_早班","").equals("10")
                    && !blm.getName().replace("号车_早班","").equals("11")
                    && !blm.getName().replace("号车_早班","").equals("16")
                    && !blm.getName().replace("号车_早班","").equals("17")
                    && !blm.getName().replace("号车_早班","").equals("20")
                    && !blm.getName().replace("号车_早班","").equals("31")
                    && !blm.getName().replace("号车_早班","").equals("41")
                    && !blm.getName().replace("号车_早班","").equals("60")
                    && !blm.getName().replace("号车_早班","").equals("67")
                    && !blm.getName().replace("号车_早班","").equals("74")
                    && !blm.getName().replace("号车_早班","").equals("86")
                    && !blm.getName().replace("号车_早班","").equals("97")) {
                stationsArrMorning = blm.getStations().split(",");
                for (int i = stationsArrMorning.length; i > 0; i--) {
                    stationListNoon.add(stationsArrMorning[i - 1]);
                }
                for (int i = 0; i < stationListNoon.size(); i++) {
                    if (i == 0) {
                        stationsNoon = stationListNoon.get(i);
                    } else {
                        stationsNoon = stationsNoon + "," + stationListNoon.get(i);
                    }
                }
            } else {
                stationsNoon = blm.getStations();
            }
            busLineNoon.setStations(stationsNoon);
            busLineNoon.setCreateTime(new Date());
            busLineNoon.setValid(1);
            busLineService.save(busLineNoon);
            busLineListNoon.add(busLineNoon);
            logger.info("add 午班： " + busLineNoon.getName() + "id:");
        }
//todo 问题：午班线路的ID都相同
    }

}
