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
    public static final String REGISTER = SERVER_IP + "/user/Register_C";

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
    public static final String GET_DISCOUNT_CARD_LIST = SERVER_IP + "/card/Query_Card_Sort_Discount_C";

    // 根据距离得到卡的列表
    public static final String GET_DISTANCE_CARD_LIST =SERVER_IP +"/card/Query_Card_Sort_Distance_C";

    //chat
    public static final String SEND_CHAT_MSG = SERVER_IP +"/chat/Add_Chat_C";

    public static final String GET_CHAT_DATA = SERVER_IP +"/chat/Query_Chat_C";

    // 获取qiniu token
    public static final String QIUNIU_TOKEN  = SERVER_IP +"/qiniu/Get_Token_C";

    public static final String UPDATE_USER = SERVER_IP + "/user/Update_User_C";

    public static final String QUERY_USER = SERVER_IP + "/user/Query_User_C";

    //用户状态相关
    public static final String STATE_CARD = SERVER_IP + "/record/Record_C/get";

    public static final String BORROW_CARD = SERVER_IP + "/record/Record_C/add";

    public static final String GET_ORDER = SERVER_IP + "/record/Record_C/get";

    public static final String ADD_ORDER = SERVER_IP + "/record/Record_C/add";

    // 用户获取附近的借卡
    public static final String REQUEST_CARD_GET = SERVER_IP + "/card/Request_Card_C/get";

    // 用户评论相关
    // 得到用户评论
    public static final String GET_CARD_COMMENTS = SERVER_IP + "/user/Comment_C/get";


    // 用户发布请求
    public static final String PUBLISH_CARD_REQUEST = SERVER_IP + "/card/Request_Card_C";

}
