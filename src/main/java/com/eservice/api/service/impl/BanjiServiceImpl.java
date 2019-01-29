package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.BanjiMapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiExcel;
import com.eservice.api.model.user.User;
import com.eservice.api.service.BanjiService;
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
import java.util.ArrayList;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@Service
@Transactional
public class BanjiServiceImpl extends AbstractService<Banji> implements BanjiService {
    @Resource
    private BanjiMapper banjiMapper;

    @Resource
    private BanjiServiceImpl banjiService;
    @Resource
    private UserServiceImpl userService;

    private final Logger logger = LoggerFactory.getLogger(BanjiServiceImpl.class);
    @PostMapping("/readFromExcel")
    public Result readFromExcel(@RequestParam String fileName ) {
        List<BanjiExcel> list =   new ArrayList<BanjiExcel>();
        BanjiExcel banjiExcel = null;
        Banji banji = null;

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("班级信息");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No 班级信息 sheet found");
            }
            // 循环行Row
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    banjiExcel = new BanjiExcel();
                    banji = new Banji();
                    HSSFCell nianjiCell = hssfRow.getCell(1);
                    HSSFCell banjiCell = hssfRow.getCell(2);
                    HSSFCell bzrNameCell = hssfRow.getCell(3);
                    HSSFCell bzrCodeCell = hssfRow.getCell(4);
                    banjiExcel.setGrade(CommonService.getValue(nianjiCell));
                    banjiExcel.setClassName(CommonService.getValue(banjiCell));
                    banjiExcel.setChargeTeacherName(CommonService.getValue(bzrNameCell));
                    banjiExcel.setChargeTeacherCode(CommonService.getValue(bzrCodeCell));
                    list.add(banjiExcel);

                    banji.setGrade(banjiExcel.getGrade());
                    banji.setClassName(banjiExcel.getClassName());
                    /**
                     * 如果有对应的user教师则保存
                     */
                    User bzr = userService.findBy("name", banjiExcel.getChargeTeacherName());
                    if (null != bzr) {
                        banji.setChargeTeacher(bzr.getId());
                    }
                    /**
                     * 班级目前不存在则增加
                     */
                    Class cl = Class.forName("com.eservice.api.model.banji.Banji");
                    Field fieldClassName = cl.getDeclaredField("className");//成员名
                    Field fieldGrade = cl.getDeclaredField("grade");
                    Banji banjiNew = banjiService.findBy(fieldClassName.getName(), banjiExcel.getClassName());
//                        Banji banji2 = banjiService.findBy(fieldGrade.getName(),banjiExcel.getGrade());
                    /**
                     * 班级名称不存在，或者存在于不同年级
                     * TODO : 此处逻辑待更新
                     */
                    if ((null == banjiNew)) {
                        banjiService.save(banji);

                        logger.info("add: =====" + rowNum + ":" + banjiExcel.getGrade() + "/"
                                + banjiExcel.getClassName() + "/"
                                + banjiExcel.getChargeTeacherName());
                    } else {
                        banjiService.update(banji);

                        logger.info("Update: =====" + rowNum + ":" + banjiExcel.getGrade() + "/"
                                + banjiExcel.getClassName() + "/"
                                + banjiExcel.getChargeTeacherName());
                    }

                }
            }

        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
