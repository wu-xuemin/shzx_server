package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.BusLineService;
import com.eservice.api.service.impl.BusBaseInfoServiceImpl;
import com.eservice.api.service.impl.BusLineServiceImpl;
import com.eservice.api.service.impl.StudentServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@RestController
@RequestMapping("/bus/line")
@Api(description = "校车线路信息")
public class BusLineController {
    @Resource
    private BusLineServiceImpl busLineService;
    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;
    @Resource
    private StudentServiceImpl studentService;

    private final Logger logger = LoggerFactory.getLogger(BusLineController.class);

    @PostMapping("/add")
    public Result add(BusLine busLine) {
        busLineService.save(busLine);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        busLineService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(BusLine busLine) {
        busLineService.update(busLine);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BusLine busLine = busLineService.findById(id);
        return ResultGenerator.genSuccessResult(busLine);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<BusLine> list = busLineService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 根据巴士妈妈账号来获得巴士妈妈所在的巴士, 允许多条线都是同个bus妈妈，比如早午班
     * @param busMomAccount
     * @return
     */
    @ApiOperation("根据巴士妈妈账号和班次来获得巴士线路等信息,不同班次允许同个巴士妈妈")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busMomAccount", value = "巴士妈妈账号，具有唯一性",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“早班”、“午班”两种，晚班不支持",required = true)})
    @PostMapping("/getBusLineInfoByBusMomAccountAndBusMode")
    public Result getBusLineInfoByBusMomAccountAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                        @RequestParam String busMomAccount,
                                        @RequestParam String busMode ) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoByBusMomAccountAndBusMode(busMomAccount,busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校区来获得该校区的所有巴士线路相关信息")
    @PostMapping("/getBusLineInfoBySchoolPartition")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "schoolPartition", value = "校区，目前只有“浦东”、“浦西” ",required = true)})
    public Result getBusLineInfoBySchoolPartition(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                          @RequestParam String schoolPartition) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoBySchoolPartition(schoolPartition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号/早午班 来获得该校车/早午班的所有学生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = " 早班 午班,不填则不限制")
    })
    @PostMapping("/getStudents")
    public Result getStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                              @RequestParam String busNumber,
                              String busMode ) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = busLineService.getStudents(busNumber,busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
    @PostMapping("/readFromExcel")
    public Result readFromExcel(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                              @RequestParam String fileName,
                              String busMode ) {
        BusLineInfo busLineInfo = new BusLineInfo();
        BusBaseInfo busBaseInfo = new BusBaseInfo();
        PageHelper.startPage(page, size);
        List<BusLineExcelHelper> list =   new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;

        BusLine busLine = new BusLine();
        Student student = new Student();

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            // 循环工作表Sheet
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                // 循环行Row
                for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow != null) {
                        busLineExcelHelper = new BusLineExcelHelper();
                        HSSFCell busNumber = hssfRow.getCell(0);
                        HSSFCell timeRemark = hssfRow.getCell(1);
                        HSSFCell busStationName = hssfRow.getCell(2);
                        HSSFCell studentNumber = hssfRow.getCell(3);
                        HSSFCell studentName = hssfRow.getCell(4);
                        HSSFCell studentPhones = hssfRow.getCell(5);
                        busLineExcelHelper.setBusNumber(  getValue(busNumber));
                        SimpleDateFormat sdf  = new SimpleDateFormat("hh:mm");
                        busLineExcelHelper.setTimeRemark(  sdf.format(timeRemark.getDateCellValue()));
                        busLineExcelHelper.setStationName(  getValue(busStationName));
                        busLineExcelHelper.setStudentNumber(  getValue(studentNumber));
                        busLineExcelHelper.setStudentName(  getValue(studentName));
                        busLineExcelHelper.setStudentPhones(  getValue(studentPhones));
                        list.add(busLineExcelHelper);
                        logger.info("=====" + busLineExcelHelper.toString());

                        //todo ,从目前的校车线路模板excel，来解析站点等 ，这样OK？
                        /**
                         * 保存校车编号到数据库，其他的校车数据，目前没有来源
                         */
                        busBaseInfo.setNumber(busLineExcelHelper.getBusNumber());
                        // 是否存在该车号
                        if(busBaseInfoService.findBy("number",busLineExcelHelper.getBusNumber()) != null){
                            busBaseInfoService.update(busBaseInfo);
                        } else {
                            busBaseInfoService.save(busBaseInfo);
                        }

                        /**
                         * 同个车号的站点，保存到对应的线路区间去
                         */

                        /**
                         * 学生信息保存
                         */
                        if(studentService.findBy("student_number",busLineExcelHelper.getStudentNumber()) != null) {
                            student.setStudentNumber(busLineExcelHelper.getStudentNumber());
                            student.setName(busLineExcelHelper.getStudentName());
                            student.setFamilyInfo(busLineExcelHelper.getStudentPhones());
                            //TODO ...
//                        student.setBusLineMorning(busLineExcelHelper.getBusNumber());
                            studentService.update(student);
                        } else {
                            // 新增
                            student.setStudentNumber(busLineExcelHelper.getStudentNumber());
                            student.setName(busLineExcelHelper.getStudentName());
                            student.setFamilyInfo(busLineExcelHelper.getStudentPhones());
                            //TODO ...
//                        student.setBusLineMorning(busLineExcelHelper.getBusNumber());
                            studentService.save(student);
                        }

                    }
                }
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//        } catch (BiffException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    @ApiOperation("根据校车编号/早午班 来获得该校车的线路信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = " 早班 午班")
    })
    @PostMapping("/getBusLineInfoByBusNumberAndBusMode")
    public Result getBusLineInfoByBusNumberAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                      @RequestParam String busNumber,
                                                      @RequestParam String busMode ) {

        BusLine busLine = busLineService.getBusLineInfoByBusNumberAndBusMode(busNumber,busMode);
        return ResultGenerator.genSuccessResult(busLine);
    }
}
