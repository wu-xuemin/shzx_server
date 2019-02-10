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

    public static final String TRANSPORT_RECORD_FLAG_MORNING_UP = "早上上车";
    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON_UP = "午班上车";
    public static final String TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN = "午班下车";
    public static final String TRANSPORT_RECORD_FLAG_NIGHT_UP = "晚班上车";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
  }
