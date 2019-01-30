package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.BusStationsMapper;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.service.BusStationsService;
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
* @date 2018/12/17.
*/
@Service
@Transactional
public class BusStationsServiceImpl extends AbstractService<BusStations> implements BusStationsService {
    @Resource
    private BusStationsMapper busStationsMapper;

    public BusStations getBusStation(String stationName){
        return busStationsMapper.getBusStation(stationName);
    }
    @Resource
    private BusStationsServiceImpl busStationsService;

    private final Logger logger = LoggerFactory.getLogger(BusStationsServiceImpl.class);
    @PostMapping("/readFromExcel")
    public Result readFromExcel(@RequestParam String fileName ) {
        List<BusLineExcelHelper> list =   new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;
        BusStations busStations = null;

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
                    busStations = new BusStations();
                    HSSFCell timeRemarkCell = hssfRow.getCell(1);
                    HSSFCell stationNameCell = hssfRow.getCell(2);
                    HSSFCell fareRateCell = hssfRow.getCell(6);

                    SimpleDateFormat sdf  = new SimpleDateFormat("hh:mm:ss");
                    busLineExcelHelper.setTimeRemark( sdf.format(timeRemarkCell.getDateCellValue()));
                    busLineExcelHelper.setStationName(CommonService.getValue(stationNameCell));
                    busLineExcelHelper.setFareRate(CommonService.getValue(fareRateCell));
                    list.add(busLineExcelHelper);

                    Time time = Time.valueOf(busLineExcelHelper.getTimeRemark());
                    busStations.setRemark(time);
                    busStations.setStationName(busLineExcelHelper.getStationName());
                    busStations.setFareRate(busLineExcelHelper.getFareRate());

                    /**
                     * 站点目前不存在则增加
                     * 注意这里不能用findby(filedName ,...)
                     */
                    Class cl = Class.forName("com.eservice.api.model.bus_stations.BusStations");
                    Field fieldClassName = cl.getDeclaredField("stationName");//成员名
                    BusStations busStationExist = null;
                    // todo,？
                    busStationExist = busStationsService.findBy(fieldClassName.getName(), busLineExcelHelper.getStationName());
                    /**
                     * 站点名称不存在，则增加
                     */
                    if ((null == busStationExist)) {
                        busStationsService.save(busStations);
                        logger.info("add: =====" + rowNum + ":" + busStations.getStationName() + "/"
                                + busStations.getRemark() + "/"
                                + busStations.getFareRate());
                    } else {
                        /**
                         * 班级名称存在，年级也相同，则更新
                         */
                        busStationsService.update(busStations);
                        logger.info("Update: =====" + rowNum + busStations.getStationName() + "/"
                                + busStations.getRemark() + "/"
                                + busStations.getFareRate());
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
