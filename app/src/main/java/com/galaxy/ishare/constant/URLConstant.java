package com.galaxy.ishare.constant;

/**
 * Created by liuxiaoran on 15/4/28.
 */
public class URLConstant {

    // 短信验证url
    public static final String GET_CHANGZHUO_SHORT_MESSAGE = "http://sms.chanzor.com:8001/sms.aspx?action=send&userid=&account=mengdongkeji&password=133268&mobile=tmpPhoneNumber&content=tmpContent&sendTime=";

    // 阿里服务器地址
    public static final String SERVER_IP = "http://123.57.229.77/index.php";


    // login
    public static final String LOGIN = SERVER_IP + "/user/Login_C";

    // 更新用户信息
    public static final String UPDATE_USER_INFO =SERVER_IP+"/user/Update_User_C";

    //获取分页的地图商铺数据
    public static final String MAP_SHOP_PAGE = SERVER_IP + "/shop/Query_Shop_Sort_Composite_C/index";
    public static final String MAP_CARD_PAGE = SERVER_IP + "/card/Query_Card_Sort_Composite_C/index";

    // 获得手机通信录双方都添加了对方--好友关系 的列表
    public static final String FRIEND_CONTACT = SERVER_IP + "/FriendContacts";

    //获得好友关系(但对方未装应用) 列表
    public static final String INVITE_CONTACT = SERVER_IP + "/InviteFriendContacts";


    public static final String UPLOAD_CONTACT = SERVER_IP + "/UpdateContact";

    // 发布卡
    public static final String PUBLISH_SHARE_ITEM = SERVER_IP + "/card/Add_Card_C";


    // 根据折扣从大到小得到卡的列表
    public static final String  GET_DISCOUNT_CARD_LIST = SERVER_IP +"/card/Query_Card_Sort_Discount_C";

    // 根据距离得到卡的列表
    public static final String GET_DISTANCE_CARD_LIST =SERVER_IP +"/card/Query_Card_Sort_Distance_C";

    // 借卡，发送借卡请求
    public static final String BORROW_CARD = SERVER_IP + "/record/Record_C/add";


    // 获取qiniu token
    public static final String QIUNIU_TOKEN  = SERVER_IP +"/qiniu/Get_Token_C";
}
