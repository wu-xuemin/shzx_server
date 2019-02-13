package com.eservice.api.service.common;

/**
 * Class Description:常量定义类
 *
 * @author Wilson Hu
 * @date 22/12/2017
 */
public class Constant {

    /**
     * 校车接送模式，分为“早班”，“午班”，“晚班”
     * 注意 Mapper.xml 里也有这些字符串
     */
    public static final String BUS_MODE_MORNING = "早班";
    public static final String BUS_MODE_AFTERNOON = "午班";
    public static final String BUS_MODE_NIGHT = "晚班";

    public static final String TRANSPORT_RECORD_FLAG_MORNING = "早班发车";
//    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON_UP = "午班上车";
    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON = "午班发车";
    public static final String TRANSPORT_RECORD_FLAG_NIGHT = "晚班发车";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    public static final String TRANSPORT_STATUS_ZAOBAN_NOT_START = "早班未开始";
    public static final String TRANSPORT_STATUS_ZAOBAN_RUNNING = "早班进行中";
    public static final String TRANSPORT_STATUS_ZAOBAN_DONE = "早班已结束";

   // public static final String TRANSPORT_STATUS_WUBAN_NOT_START = "早班未开始"; //等同 早班已结束，
    public static final String TRANSPORT_STATUS_WUBAN_RUNNING = "午班进行中";
    public static final String TRANSPORT_STATUS_WUBAN_DONE = "午班已结束";

   // public static final String TRANSPORT_STATUS_WANGBAN_NOT_START = "晚班未开始"; //等同 午班已结束
    public static final String TRANSPORT_STATUS_WANGBAN_RUNNING = "晚班进行中";
    public static final String TRANSPORT_STATUS_WANGBAN_DONE = "晚班已结束";

    public static final String TRANSPORT_RECORD_STATUS_RUNNING = "行程进行中";
    public static final String TRANSPORT_RECORD_STATUS_DONE = "行程已结束";

}
