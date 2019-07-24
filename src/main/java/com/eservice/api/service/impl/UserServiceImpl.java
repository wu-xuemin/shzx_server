package com.eservice.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.UserMapper;
import com.eservice.api.model.banji.BanjiExcel;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.user.User;
import com.eservice.api.service.UserService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
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

    @Value("${user_img_dir}")
    private String USER_IMG_DIR;

    @Resource
    private UserServiceImpl userService;

    @Value("${url_style}")
    private String urlStyle;

    @Value("${user_img_url_prefix}")
    private String userImgUrlPrefix;

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

    public List<User> findAllDriver() {
        return userMapper.findAllDriver();
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
                    bzr.setPassword(Constant.USER_DEFAULT_PASSWORD);
                    bzr.setRoleId(Constant.USER_ROLE_TEACHER);
                    bzr.setCreateTime(new Date());
                    bzr.setValid(Constant.VALID_YES);
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
                        bzr.setId(userExist.getId());
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
     * 根据表格去更新老师的电话
     */
    public Result parseChargeTeacherPhoneFromExcel(@RequestParam String fileName ) {
        // 返回 表格中没有电话的老师的姓名
        List<String> listUserNameNoPhone =   new ArrayList<String>();
        BanjiExcel banjiExcel = null;
        User bzr = null;
        DecimalFormat format = new DecimalFormat("#");

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("老师信息");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No 老师信息 sheet found");
            }
            // 循环行Row
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    banjiExcel = new BanjiExcel();
                    bzr = new User();
                    HSSFCell bzrNameCell = hssfRow.getCell(9);
                    HSSFCell bzrPhoneCell = hssfRow.getCell(11);
                    banjiExcel.setChargeTeacherName(CommonService.getValue(bzrNameCell));
                    if(bzrPhoneCell != null) {
                        banjiExcel.setChargeTeacherPhone(format.format(bzrPhoneCell.getNumericCellValue()));
                    } else {
                        logger.info("the user no phone found " + banjiExcel.getChargeTeacherName());
                        listUserNameNoPhone.add(banjiExcel.getChargeTeacherName());
                    }
                    Class cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldUserAccount = cl.getDeclaredField("account");//成员名
                    User userExist = null;
                    userExist = findBy(fieldUserAccount.getName(), banjiExcel.getChargeTeacherName());
                    /**
                     * 用户名称不存在，则无视,用户存在，则更新
                     */
                    if ((null == userExist)) {
                        logger.info("charge teacher no found: =====" + rowNum + ":" + bzr.getAccount());
                    } else {
                        //只更新电话
                        userExist.setPhone(banjiExcel.getChargeTeacherPhone());
                        update(userExist);
                        logger.info("charge teacher Phone Updated: =====" + rowNum + ":" + bzr.getAccount());
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
        PageInfo pageInfo = new PageInfo(listUserNameNoPhone);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 解析bus妈妈和司机的信息
     * TODO: 去除各种空格
     */
    public Result parseBusMomDriverFromExcel(@RequestParam String fileName ) {
        List<BusLineExcelHelper> list =   new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;
        User busMom = null;
        User busDriver = null;
        DecimalFormat format = new DecimalFormat("#");

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
                    busLineExcelHelper.setBusMomPhone(format.format(busMomPhoneCell.getNumericCellValue()));
                    busLineExcelHelper.setBusDriverAccount(CommonService.getValue(busDriverNameCell));
                    busLineExcelHelper.setBusDriverName(CommonService.getValue(busDriverNameCell));
                    busLineExcelHelper.setBusDriverPhone(format.format(busDriverPhoneCell.getNumericCellValue()));

                    busMom.setAccount(busLineExcelHelper.getBusMomAccount());
                    busMom.setName(busLineExcelHelper.getBusMomName());
                    busMom.setPhone(busLineExcelHelper.getBusMomPhone());
                    busMom.setPassword(Constant.USER_DEFAULT_PASSWORD);
                    busMom.setRoleId(Constant.USER_ROLE_BUSMOM);
                    busMom.setCreateTime(new Date());
                    busMom.setValid(Constant.VALID_YES);

                    busDriver.setAccount(busLineExcelHelper.getBusDriverAccount());
                    busDriver.setName(busLineExcelHelper.getBusDriverName());
                    busDriver.setPhone(busLineExcelHelper.getBusDriverPhone());
                    busDriver.setPassword(Constant.USER_DEFAULT_PASSWORD);
                    busDriver.setRoleId(Constant.USER_ROLE_DRIVER);
                    busDriver.setCreateTime(new Date());
                    busDriver.setValid(Constant.VALID_YES);

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
                        logger.info("Mom added: =====" + rowNum + ":" + busMom.getAccount() + busMom.getPhone());
                    } else {
                        busMom.setId(userMomExist.getId());
                        update(busMom);
                        logger.info("Mom Updated: =====" + rowNum + ":" + busMom.getAccount() + busMom.getPhone());
                    }

                    if ((null == userDriverExist)) {
                        busDriver.setCreateTime(new Date());
                        save(busDriver);
                        logger.info("busDriver added: =====" + rowNum + ":" + busDriver.getAccount() + busDriver.getPhone());
                    } else {
                        busDriver.setId(userDriverExist.getId());
                        update(busDriver);
                        logger.info("busDriver Updated: =====" + rowNum + ":" + busDriver.getAccount() + busDriver.getPhone());
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
     * 返回User照片文件存在，但是在数据库中不存在的照片文件名
     *  busMom4_张三.jpg
     */
    public List<String> getAndInsertUserHeadImg() {
        File dir = new File(USER_IMG_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        List<String> list = new ArrayList<String>();
        File file = new File(USER_IMG_DIR);
        File[] tempList = file.listFiles();

        User user = null;
        Integer count = 0;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                /*
                 *根据姓名查user.  13800002222_李玲红.jpg   13011112222_郭银祥.jpg
                 */
                User userExist = null;
                Class cl = null;
                try {
                    cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldHeadImage = cl.getDeclaredField("name");
                    logger.info("to find user by name " + tempList[i].getName().split("_")[1].split("\\.")[0]);
                    userExist = userService.findBy(fieldHeadImage.getName(), tempList[i].getName().split("_")[1].split("\\.")[0]);
                } catch (ClassNotFoundException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }
                if(userExist != null) {
                    if(urlStyle.equals(Constant.URL_PATH_STYLE_RELATIVE)) {
                        userExist.setHeadImage( tempList[i].getName());
                    } else {
                        userExist.setHeadImage(userImgUrlPrefix  + tempList[i].getName());
                    }
                    userService.update(userExist);
                    logger.info("User：" + tempList[i].getName() + " 已更新head_image");
                } else {
                    count ++;
                    logger.warn("根据照片 " + tempList[i].getName() + "，找不到对应的用户, " + count);
                    list.add(tempList[i].getName());
                }
            }
        }
        return list;
    }

    public List<String> renameUserPic() {
        File dir = new File(USER_IMG_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        List<String> list = new ArrayList<String>();
        File file = new File(USER_IMG_DIR);
        File[] tempList = file.listFiles();

        User user = null;
        Integer count = 0;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                /*
                 * driver50_沈勇.jpg  改为--> 139xxxxyyyy_沈勇.jpg
                 */
                User userExist = null;
                Class cl = null;
                try {
                    cl = Class.forName("com.eservice.api.model.user.User");
                    Field fieldHeadImage = cl.getDeclaredField("name");
                    userExist = userService.findBy(fieldHeadImage.getName(), tempList[i].getName().split("_")[1].split("\\.")[0]);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if(userExist != null) {
                    String newName = userExist.getPhone()
                            + "_" + tempList[i].getName().split("_")[1];
                    CommonService.renameFile(USER_IMG_DIR,tempList[i].getName(),newName);
                    logger.info("User：" + tempList[i].getName() + " 已重命名： " + newName);
                } else {
                    count ++;
                    logger.warn("根据照片 " + tempList[i].getName() + "，找不到对应的用户, " + count);
                    list.add(tempList[i].getName());
                }

            }
        }
        return list;
    }

    public String getURLContentAndCreateBusMomAndDriver(String urlStr){
        Integer addedBusMomSum = 0;
        Integer addedBusDriverSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                User busMom = new User();
                User busDriver = new User();
                JSONObject jo = ja.getJSONObject(i);
                String busMomName = jo.getString("bus_mom_name");
                String busMomPhone = jo.getString("bus_mom_phone");
                String busdriverName = jo.getString("driver_name");
                String busdriverPhone = jo.getString("driver_phone");

                busMom.setAccount(busMomName);
                busMom.setName(busMomName);
                busMom.setRoleId(Constant.USER_ROLE_BUSMOM);
                busMom.setPassword(Constant.USER_DEFAULT_PASSWORD);
                busMom.setPhone(busMomPhone);
                busMom.setCreateTime(new Date());
                busMom.setValid(Constant.VALID_YES);

                busDriver.setAccount(busdriverName);
                busDriver.setName(busdriverName);
                busDriver.setRoleId(Constant.USER_ROLE_DRIVER);
                busDriver.setPassword(Constant.USER_DEFAULT_PASSWORD);
                busDriver.setPhone(busdriverPhone);
                busDriver.setCreateTime(new Date());
                busDriver.setValid(Constant.VALID_YES);

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User busMomExist = null;
                busMomExist = userService.findBy(fieldUserAccount.getName(), busMomName);
                if (busMomExist == null) {
                    userService.save(busMom);
                    logger.info("added busMom: " + busMom.getAccount());
                    addedBusMomSum++;
                } else {
                    logger.info(" already exist busMom: " + busMom.getAccount());
                }

                User busDriverExist = null;
                busDriverExist = userService.findBy(fieldUserAccount.getName(), busdriverName);
                if (busDriverExist == null) {
                    userService.save(busDriver);
                    logger.info("added driver: " + busdriverName);
                    addedBusDriverSum++;
                } else {
                    logger.info(" already exist driver: " + busdriverName);
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
        }
        return "addedBusMomSum: " + addedBusMomSum + "  ,addedBusDriverSum: " + addedBusDriverSum;
    }

    public String getURLContentAndCreateBZR(String urlStr){

        Integer addedBzrSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                User bzr = new User();
                JSONObject jo = ja.getJSONObject(i);
                String teacherName = jo.getString("teacher_name");
                String teacherPhone = jo.getString("teacher_phone");
                String teacherUnionId = jo.getString("tunionId");

                bzr.setCreateTime(new Date());
                //班主任的账号设为和姓名一样
                bzr.setAccount(teacherName);
                bzr.setName(teacherName);
                bzr.setPhone(teacherPhone);
                bzr.setPassword(Constant.USER_DEFAULT_PASSWORD);
                bzr.setRoleId(Constant.USER_ROLE_TEACHER);
                bzr.setCreateTime(new Date());
                bzr.setValid(Constant.VALID_YES);
                bzr.setSchoolStaffCode(teacherUnionId);

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User userExist = null;
                userExist = userService.findBy(fieldUserAccount.getName(), teacherName);
                if (userExist == null) {
                    userService.save(bzr);
                    logger.info("added bzr: " + bzr.getAccount());
                    addedBzrSum++;
                } else {
                    logger.info(" already exist account: " + bzr.getAccount() + ",name " + bzr.getName());
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
        }
        return "addedBzrSum " + addedBzrSum + " is added";
    }
}
