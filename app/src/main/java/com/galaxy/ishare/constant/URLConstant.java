package com.galaxy.ishare.constant;

/**
 * Created by liuxiaoran on 15/4/28.
 */
public class URLConstant {

    // 短信验证url
    public static final String GET_CHANGZHUO_SHORT_MESSAGE = "http://sms.chanzor.com:8001/sms.aspx?action=send&userid=&account=mengdongkeji&password=133268&mobile=tmpPhoneNumber&content=tmpContent&sendTime=";

    // 阿里服务器地址
    public static final String SERVER_IP = "http://123.57.229.77/index.php";

    // register 接口
    public static final String REGISTER = SERVER_IP + "/Register";

    // login
    public static final String LOGIN = SERVER_IP + "/Login";

    // 获得手机通信录双方都添加了对方--好友关系 的列表
    public static final String FRIEND_CONTACT = SERVER_IP + "/FriendContacts";

    //获得好友关系(但对方未装应用) 列表
    public static final String INVITE_CONTACT = SERVER_IP + "/InviteFriendContacts";


    public static final String UPLOAD_CONTACT= SERVER_IP +"/UpdateContact";
}
