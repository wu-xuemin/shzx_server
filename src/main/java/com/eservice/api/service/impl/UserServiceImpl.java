package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.UserMapper;
import com.eservice.api.model.banji.BanjiExcel;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.user.User;
import com.eservice.api.service.UserService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.CommonService;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@Service
@Transactional
public class UserServiceImpl extends AbstractService<User> implements UserService,UserDetailsService {
    @Resource
    private UserMapper userMapper;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public User selectByAccount(String account){
        return userMapper.selectByAccount(account);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.selectByAccount(s);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }

    public User requestLogin(String account, String password ) {
        return userMapper.requestLogin(account, password);
    }

    public List<User> selectUsers(String account, String name, Integer roleId, Integer valid) {
        return userMapper.selectUsers(account, name, roleId, valid);
    }

    public List<User> findAllBusMom() {
        return userMapper.findAllBusMom();
    }

    public Result parseChargeTeacherFromExcel(@RequestParam String fileName ) {
        List<BanjiExcel> list =   new ArrayList<BanjiExcel>();
        BanjiExcel banjiExcel = null;
        User bzr = null;

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
                    bzr = new User();
                    HSSFCell bzrNameCell = hssfRow.getCell(3);
                    HSSFCell bzrCodeCell = hssfRow.getCell(4);
                    banjiExcel.setChargeTeacherName(CommonService.getValue(bzrNameCell));
                    banjiExcel.setChargeTeacherCode(CommonService.getValue(bzrCodeCell));
                    //班主任的账号设为和姓名一样
                    bzr.setAccount(banjiExcel.getChargeTeacherName());
                    bzr.setName(banjiExcel.getChargeTeacherName());
                    bzr.setPassword("shzx");
                    bzr.setRoleId(4);
                    bzr.setCreateTime(new Date());
                    bzr.setValid(1);
                    list.add(banjiExcel);

                    Class cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldUserAccount = cl.getDeclaredField("account");//成员名
                    User userExist = null;
                    userExist = findBy(fieldUserAccount.getName(), banjiExcel.getChargeTeacherName());
                    /**
                     * 用户名称不存在，则增加,用户存在，则更新
                     */
                    if ((null == userExist)) {
                        bzr.setCreateTime(new Date());
                        save(bzr);
                        logger.info("charge teacher added: =====" + rowNum + ":" + bzr.getAccount());
                    } else {
                        update(bzr);
                        logger.info("charge teacher Updated: =====" + rowNum + ":" + bzr.getAccount());
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

    /**
     * 解析bus妈妈和司机的信息
     */
    public Result parseBusMomDriverFromExcel(@RequestParam String fileName ) {
        List<BusLineExcelHelper> list =   new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;
        User busMom = null;
        User busDriver = null;

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("Sheet1");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No sheet1 found");
            }
            // 循环行Row
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    busLineExcelHelper = new BusLineExcelHelper();
                    busMom = new User();
                    busDriver = new User();
                    HSSFCell busMomNameCell = hssfRow.getCell(10);
                    HSSFCell busMomPhoneCell = hssfRow.getCell(11);
                    HSSFCell busDriverNameCell = hssfRow.getCell(12);
                    HSSFCell busDriverPhoneCell = hssfRow.getCell(13);
                    //账号设为和姓名一样
                    busLineExcelHelper.setBusMomAccount(CommonService.getValue(busMomNameCell));
                    busLineExcelHelper.setBusMomName(CommonService.getValue(busMomNameCell));
                    busLineExcelHelper.setBusMomPhone(CommonService.getValue(busMomPhoneCell));
                    busLineExcelHelper.setBusDriverAccount(CommonService.getValue(busDriverNameCell));
                    busLineExcelHelper.setBusDriverName(CommonService.getValue(busDriverNameCell));
                    busLineExcelHelper.setBusDriverPhone(CommonService.getValue(busDriverPhoneCell));

                    busMom.setAccount(busLineExcelHelper.getBusMomAccount());
                    busMom.setName(busLineExcelHelper.getBusMomName());
                    busMom.setPhone(busLineExcelHelper.getBusMomPhone());
                    busMom.setPassword("shzx");
                    busMom.setRoleId(3);
                    busMom.setCreateTime(new Date());
                    busMom.setValid(1);

                    busDriver.setAccount(busLineExcelHelper.getBusDriverAccount());
                    busDriver.setName(busLineExcelHelper.getBusDriverName());
                    busDriver.setPhone(busLineExcelHelper.getBusDriverPhone());
                    busDriver.setPassword("shzx");
                    busDriver.setRoleId(5);
                    busDriver.setCreateTime(new Date());
                    busDriver.setValid(1);

                    list.add(busLineExcelHelper);

                    Class cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldUserAccount = cl.getDeclaredField("account");//成员名
                    User userMomExist = null;
                    userMomExist = findBy(fieldUserAccount.getName(), busLineExcelHelper.getBusMomAccount());
                    User userDriverExist = null;
                    userDriverExist = findBy(fieldUserAccount.getName(), busLineExcelHelper.getBusDriverAccount());
                    /**
                     * Mom,Driver 用户名称不存在，则增加,用户存在，则更新
                     */
                    if ((null == userMomExist)) {
                        busMom.setCreateTime(new Date());
                        save(busMom);
                        logger.info("Mom added: =====" + rowNum + ":" + busMom.getAccount());
                    } else {
                        update(busMom);
                        logger.info("Mom Updated: =====" + rowNum + ":" + busMom.getAccount());
                    }

                    if ((null == userDriverExist)) {
                        busDriver.setCreateTime(new Date());
                        save(busDriver);
                        logger.info("busDriver added: =====" + rowNum + ":" + busDriver.getAccount());
                    } else {
                        update(busDriver);
                        logger.info("busDriver Updated: =====" + rowNum + ":" + busDriver.getAccount());
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
