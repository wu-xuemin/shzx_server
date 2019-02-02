package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.UserMapper;
import com.eservice.api.model.banji.BanjiExcel;
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
                    list.add(banjiExcel);

                    Class cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldUserAccount = cl.getDeclaredField("account");//成员名
                    User userExist = null;
                    userExist = findBy(fieldUserAccount.getName(), banjiExcel.getChargeTeacherName());
                    /**
                     * 用户名称不存在，则增加,用户存在，则更新
                     */
                    if ((null == userExist)) {
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
}
