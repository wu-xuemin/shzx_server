package com.eservice.api.service.common;

import com.alibaba.fastjson.JSONObject;
import com.eservice.api.model.picked_students_info.PickedStudentsBusView;
import com.eservice.api.model.picked_students_info.PickedStudentsInfo;
import com.eservice.api.service.util.Util;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service
public class ExcelService {
    private final static Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public <T> String exportRecord(List<T> recordData, String path, String fileName, Class<T> clazz) {
        logger.info("正在导出excel....");
        fileName += ".xls";
        try {
            //放excel表格需要存放的地址
            File dir = new File(path);
            dir.setWritable(true, false);//获取Linux文件权限,
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    logger.info("目录创建成功!");
                } else {
                    logger.info("目录创建失败!");
                }
            } else {
                logger.info(path + "路径存在！");
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            String name = clazz.getName();
            HSSFSheet sheet = workbook.createSheet(name.substring(name.lastIndexOf(".") + 1));
            FileOutputStream out = new FileOutputStream(path + fileName);
            //新增数据行，并且设置单元格数据
            insertDataInSheet(sheet, recordData, clazz);
            workbook.write(out);
            out.close();
            logger.info("excel导出成功！");
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("excel文件创建失败!");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("写入文件失败!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("导出文件失败!");
        }
        return "error.xls";
    }

    private <T> void insertDataInSheet(HSSFSheet sheet, List<T> recordData, Class<T> clazz) {
        //在表中存放查询到的数据放入对应的列
        int rowNum = 0;
        //headers表示excel表中第一行的表头
        HSSFRow row3 = sheet.createRow(rowNum++);
        //在excel表中添加表头
        Field[] fields = clazz.getDeclaredFields();
        String[][] map = new String[2][fields.length];
        for (int i = 0; i < fields.length; i++) {
            HSSFCell cell = row3.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(fields[i].getName());
            map[0][i] = fields[i].getName();
            map[1][i] = fields[i].getGenericType().getTypeName();
            cell.setCellValue(text);
        }
        int index = 1;
        for (T record : recordData) {
            HSSFRow row = sheet.createRow(rowNum++);
            try {
                Map object = Util.objectToMap(record);
                for (int i = 0; i < fields.length; i++) {
                    try {
                        if (map[0][i].equalsIgnoreCase("id")) {
                            row.createCell(i).setCellValue(index++);
                            continue;
                        }
                        switch (map[1][i]) {
                            case "java.lang.Integer":
                            case "java.lang.Byte":
                                row.createCell(i).setCellValue(object.get(map[0][i]).toString());
                                break;
                            case "java.util.Date":
                                row.createCell(i).setCellValue(sdf.format(object.get(map[0][i])));
                                break;
                            default:
                                row.createCell(i).setCellValue(object.get(map[0][i]).toString());
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info(e.getMessage());
                        row.createCell(i).setCellValue("");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                row.createCell(0).setCellValue(e.getMessage());
            }
        }
    }

    public String exportRecord(List recordData, String path, String fileName) {
        logger.info("正在导出excel....");
        fileName += ".xls";
        try {
            //放excel表格需要存放的地址
            File dir = new File(path);
            dir.setWritable(true, false);//获取Linux文件权限,
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    logger.info("目录创建成功!");
                } else {
                    logger.info("目录创建失败!");
                }
            } else {
                logger.info(path + "路径存在！");
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("InvitationName");
            FileOutputStream out = new FileOutputStream(path + fileName);
            //新增数据行，并且设置单元格数据
            insertDataInSheet(sheet, recordData);
            workbook.write(out);
            out.close();
            logger.info("excel导出成功！");
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("excel文件创建失败!");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("写入文件失败!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("导出文件失败!");
        }
        return "error.xls";
    }

    private void insertDataInSheet(HSSFSheet sheet, List userTimes) {
        String[] excelHeaders = {"id","time", "busNum", "station", "class", "name", "status", "flag"};
        //headers表示excel表中第一行的表头
        HSSFRow row3 = sheet.createRow(0);
        //在excel表中添加表头
        for (int i = 0; i < excelHeaders.length; i++) {
            HSSFCell cell = row3.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(excelHeaders[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        int rowNum = 1;
        if (userTimes == null || userTimes.size() <= 0) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("本次导出，未查询到乘车记录。");
        } else {
            for (Object object : userTimes) {
                PickedStudentsBusView pickedStudentsInfo = (PickedStudentsBusView) object;
                HSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(sdf.format(pickedStudentsInfo.getBoardTime()));
                row.createCell(2).setCellValue(pickedStudentsInfo.getBusNumber());
                row.createCell(3).setCellValue(pickedStudentsInfo.getStudentStationsAfternoon());
                //invitationName.getInvitationCreateTime()==null?"":sdf.format(invitationName.getInvitationCreateTime())
                row.createCell(4).setCellValue(pickedStudentsInfo.getClassName());
                row.createCell(5).setCellValue(pickedStudentsInfo.getStudentName());
                row.createCell(6).setCellValue(pickedStudentsInfo.getModeName());
                row.createCell(7).setCellValue(pickedStudentsInfo.getCheckMode() == 1 ? "刷脸" : "手动");
                rowNum++;
            }
        }
    }

    public <T> ArrayList<T> importRecord(MultipartFile multipartFile, Class<T> clazz) {
        ArrayList<T> dataJsonList = new ArrayList<>();
        ArrayList<String> fieldNames = new ArrayList<>();
        //获取导入字段名称
        for (Field field : clazz.getDeclaredFields()) {
            fieldNames.add(field.getName());
        }
        List<String[]> excel = getExcelData(multipartFile);
        if (excel != null && excel.size() > 0) {
            Map<String, Integer> fieldIndex = new HashMap<>();
            for (String fieldName : fieldNames) {
                String[] row = excel.get(0);
                for (int i = 0; i < row.length; i++) {
                    if (row[i].trim().equalsIgnoreCase(fieldName)) {
                        fieldIndex.put(fieldName, i);
                        break;
                    }
                }
            }

            for (int i = 1; i < excel.size(); i++) {
                String[] rowData = excel.get(i);
                StringBuffer dataJson = new StringBuffer("{");       //json格式以{开始
                for (Map.Entry<String, Integer> entry : fieldIndex.entrySet()) {
                    dataJson.append("\"" + entry.getKey() + "\":\"" + rowData[entry.getValue()] + "\",");
                }
                //去除最后一个，json格式以}结束
                dataJsonList.add(JSONObject.parseObject(dataJson.substring(0, dataJson.lastIndexOf(",")) + "}", clazz));
            }
        }
        return dataJsonList;
    }

    private List<String[]> getExcelData(MultipartFile file) {
        if (checkFile(file)) {
            //获得Workbook工作薄对象
            Workbook workbook = getWorkBook(file);
            //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
            List<String[]> list = new ArrayList<>();
            if (workbook != null) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(0);
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //有效列的个数
                int cellCount = 0;
                Row row = sheet.getRow(firstRowNum);
                if (row != null) {
                    //获得第一行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得第一行的结束列
                    int lastCellNum = row.getLastCellNum();
                    String[] cells = new String[lastCellNum];
                    //循环第一行，只统计连续不为空的标题，遇到空值就终止
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell).trim();
                        if (cells[cellNum] != null && !"".equalsIgnoreCase(cells[cellNum])) {
                            cellCount++;
                        } else {
                            break;
                        }
                    }
                    list.add(cells);
                }
                //循环除了第一行的所有行
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //统计每行空值的个数
                    int nullCount = 0;
                    //存储每行的数据
                    String[] cells = new String[cellCount];
                    //循环当前行
                    if (firstCellNum < 0) {
                        continue;
                    }
                    for (int cellNum = firstCellNum; cellNum < cellCount; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell).trim();
                        if (cells[cellNum].equalsIgnoreCase("")) {
                            nullCount++;
                        }
                    }
                    //如果当前行的空值列多余或者等与有效列的个数，则视为从当前行开始之后，没数据
                    if (nullCount >= cellCount) {
                        break;
                    } else {
                        list.add(cells);
                    }
                }
            }
            return list;
        }
        return null;
    }

    /**
     * 检查文件
     *
     * @param file
     */
    private boolean checkFile(MultipartFile file) {
        //判断文件是否存在
        if (null == file) {
            logger.error("文件不存在！");
            return false;
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
            logger.error(fileName + "不是excel文件");
            return false;
        }
        return true;
    }

    private Workbook getWorkBook(MultipartFile file) {
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        //获得文件名
        try {
            String fileName = file.getOriginalFilename();
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith("xls")) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                //2007 及2007以上
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return workbook;
    }

    private String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = stringDateProcess(cell);
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    /**
     * 时间格式处理
     *
     * @return
     * @author Liu Xin Nan
     * @data 2017年11月27日
     */
    private String stringDateProcess(Cell cell) {
        String result = new String();
        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
            SimpleDateFormat sdf = null;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            }
            Date date = cell.getDateCellValue();
            result = sdf.format(date);
        } else if (cell.getCellStyle().getDataFormat() == 58) {
            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            double value = cell.getNumericCellValue();
            Date date = DateUtil.getJavaDate(value);
            result = sdf.format(date);
        } else {
            double value = cell.getNumericCellValue();
            CellStyle style = cell.getCellStyle();
            DecimalFormat format = new DecimalFormat();
            String temp = style.getDataFormatString();
            // 单元格设置成常规
            if (temp.equals("General")) {
                format.applyPattern("#");
            }
            result = format.format(value);
        }
        return result;
    }

}
