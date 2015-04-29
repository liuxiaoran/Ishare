package com.galaxy.ishare;

/**
 * Created by liuxiaoran on 15/4/28.
 */
public class URLConstant {

    // 短信验证url
    public static final String GET_CHANGZHUO_SHORT_MESSAGE = "http://sms.chanzor.com:8001/sms.aspx?action=send&userid=&account=mengdongkeji&password=133268&mobile=tmpPhoneNumber&content=tmpContent&sendTime=";

    // 阿里服务器地址
    public static final String SERVER_IP ="http://123.57.229.77/index.php";

    // register 接口
    public static final String REGISTER = SERVER_IP +"/Register";

    // login
    public static final String LOGIN = SERVER_IP +"/Login";
}
