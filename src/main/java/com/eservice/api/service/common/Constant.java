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

    /**
     * 注意，transport_record的FLAG 是用来标识 TransportRecord的 发车记录的早班/午班/晚班
     * 后端根据时间判断这3个，午班上车和晚班上车另外处理
     */
    public static final String TRANSPORT_RECORD_FLAG_MORNING = "早班";
    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON = "午班";
    public static final String TRANSPORT_RECORD_FLAG_NIGHT = "晚班";

    /**
     * 注意，transport_record的status标志行程是否结束
     */
    public static final String TRANSPORT_RECORD_STATUS_RUNNING = "行程进行中";
    public static final String TRANSPORT_RECORD_STATUS_DONE = "行程已结束";
    public static final String TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED = "晚班行程已选";

    /**
     * 校车所处状态
     */
    public static final String BUS_STATUS_ZAOBAN_WAIT_START = "早班待发车";
    public static final String BUS_STATUS_ZAOBAN_RUNNING = "早班进行中";
    public static final String BUS_STATUS_ZAOBAN_DONE = "早班已结束";


    public static final String BUS_STATUS_WUBAN_WAIT_START = "午班待发车";
    public static final String BUS_STATUS_WUBAN_RUNNING = "午班进行中";
    public static final String BUS_STATUS_WUBAN_DONE = "午班已结束";

    public static final String BUS_STATUS_WANBAN_WAIT_START = "晚班待发车";
    public static final String BUS_STATUS_WANBAN_LINE_SELECTED = "晚班线路已选";
    public static final String BUS_STATUS_WANBAN_RUNNING = "晚班进行中";
    public static final String BUS_STATUS_WANBAN_DONE = "晚班已结束";

    public static final String BUS_STATUS_ERROR = "状态错误";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

}
