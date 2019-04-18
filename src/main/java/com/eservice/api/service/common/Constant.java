package com.eservice.api.service.common;

/**
 * Class Description:常量定义类
 *
 * @author Wilson Hu
 * @date 22/12/2017
 */
public class Constant {

    /**
     * 校车接送模式，分为“上学”，“放学”，“晚班”
     * 注意 Mapper.xml 里也有这些字符串
     */
    public static final String BUS_MODE_MORNING = "上学";
    public static final String BUS_MODE_AFTERNOON = "放学";
    public static final String BUS_MODE_NIGHT = "晚班";

    /**
     * 注意，transport_record的FLAG 是用来标识 TransportRecord的 发车记录的上学/放学/晚班
     */
    public static final String TRANSPORT_RECORD_FLAG_MORNING = "上学";
    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON_UP = "放学上车";
    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN = "放学下车";
    public static final String TRANSPORT_RECORD_FLAG_NIGHT = "晚班";

    /**
     * 注意，transport_record的status标志行程是否结束
     */
    public static final String TRANSPORT_RECORD_STATUS_RUNNING = "行程进行中";               //在添加记录时后端会设置
    public static final String TRANSPORT_RECORD_STATUS_DONE = "行程已结束";                  //需要APP来Update设置
    public static final String TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED = "晚班行程已选"; //在添加记录时后端会设置

    /**
     * 校车所处状态
     */
    public static final String BUS_STATUS_ZAOBAN_WAIT_START = "上学待发车";
    public static final String BUS_STATUS_ZAOBAN_RUNNING = "上学进行中";
    public static final String BUS_STATUS_ZAOBAN_DONE = "上学已结束";


    public static final String BUS_STATUS_WUBAN_WAIT_START = "放学待发车";
    //todo 相关的更新
    public static final String BUS_STATUS_WUBAN_ABOARDING = "放学上车中";
    public static final String BUS_STATUS_WUBAN_RUNNING = "放学进行中";
    public static final String BUS_STATUS_WUBAN_DONE = "放学已结束";

    public static final String BUS_STATUS_WANBAN_WAIT_START = "晚班待发车";
    public static final String BUS_STATUS_WANBAN_LINE_SELECTED = "晚班线路已选";
    public static final String BUS_STATUS_WANBAN_RUNNING = "晚班进行中";
    public static final String BUS_STATUS_WANBAN_DONE = "晚班已结束";

    public static final String BUS_STATUS_ERROR = "状态错误";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    public static final String URL_PATH_STYLE_RELATIVE = "relativePath";

    public static final String MSG_STATUS_UNREAD = "未读";
    public static final String MSG_STATUS_IS_READ = "已读";

    public static final String USER_DEFAULT_PASSWORD = "shzx";
    public static final Integer USER_ROLE_BUSMOM = 3;
    public static final Integer USER_ROLE_TEACHER = 4;
    public static final Integer USER_ROLE_DRIVER = 5;
    public static final Integer VALID_YES = 1;
    public static final Integer VALID_NO = 0;

    public static final String SHZX_URL_GET_BUS = "http://app.shs.cn/ydpt/ws/buse/buses?sign=865541ccd3e52ba8ad0d16052cc25903&sendTime=1551664022761";
    public static final String SHZX_URL_GET_STUDENT = "http://app.shs.cn/ydpt/ws/buse/students?sign=865541ccd3e52ba8ad0d16052cc25903&sendTime=1551664022761";
    public static final String SHZX_URL_GET_CLASS = "http://app.shs.cn/ydpt/ws/buse/classes?sign=865541ccd3e52ba8ad0d16052cc25903&sendTime=1551664022761";

}
