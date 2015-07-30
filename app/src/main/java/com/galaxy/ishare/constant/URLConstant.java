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
    public static final String UPDATE_USER_INFO = SERVER_IP + "/user/Update_User_C";

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
    // 删除发布的卡
    public static final String DELETE_SHARE_CARD = SERVER_IP + "/card/Delete_Card_C";
    //修改发布的卡信息
    public static final String EDIT_SHARE_CARD = SERVER_IP + "/card/Update_Card_C";


    // 根据折扣从大到小得到卡的列表
    public static final String GET_DISCOUNT_CARD_LIST = SERVER_IP + "/card/Query_Card_Sort_Discount_C";

    // 根据距离得到卡的列表
    public static final String GET_DISTANCE_CARD_LIST = SERVER_IP + "/card/Query_Card_Sort_Distance_C";

    //chat
    public static final String SEND_CHAT_MSG = SERVER_IP + "/chat/Add_Chat_C";

    public static final String GET_CHAT_DATA = SERVER_IP + "/chat/Query_Chat_C";

    // 获取qiniu token
    public static final String QIUNIU_TOKEN = SERVER_IP + "/qiniu/Get_Token_C";

    public static final String UPDATE_USER = SERVER_IP + "/user/Update_User_C";

    public static final String QUERY_USER = SERVER_IP + "/user/Query_User_C";

    //用户状态相关
    public static final String STATE_CARD = SERVER_IP + "/record/Record_C/get";

    public static final String BORROW_CARD = SERVER_IP + "/record/Record_C/add";

    //Order
    public static final String GET_ORDER_LIST = SERVER_IP + "/record/Get_Record_List_C";

    public static final String GET_ORDER = SERVER_IP + "/record/Get_Record_C";

    public static final String ADD_ORDER = SERVER_IP + "/record/Record_C/add";

    public static final String GET_CARD_RECORD = SERVER_IP + "/record/Get_Card_Record_C";
    public static final String CARD_RECORD_IS_EXIST = SERVER_IP + "/record/Get_Card_Record_C";

    public static final String GET_REQUEST_RECORD = SERVER_IP + "/record/Get_Request_Record_C";
    public static final String REQUEST_RECORD_IS_EXIST = SERVER_IP + "/record/Get_Request_Record_C";

    // 用户获取附近的借卡
    public static final String REQUEST_CARD_GET = SERVER_IP + "/request/Get_Request_Card_C";

    // 用户评论相关
    // 得到用户评论
    public static final String ADD_CARD_COMMENT = SERVER_IP + "/comment/Add_Comment_C";
    public static final String GET_CARD_COMMENTS = SERVER_IP + "/comment/Get_Comment_C";

    public static final String UPDATE_ORDER_STATE = SERVER_IP + "/record/Record_C/update";

    // 用户发布请求
    public static final String PUBLISH_CARD_REQUEST = SERVER_IP + "/request/Add_Request_Card_C";

    //card
    //我分享的卡
    public static final String GET_I_SHARE_CARD = SERVER_IP + "/card/Query_Card_I_Share";

    //card
    //我在找的卡
    public static final String GET_I_REQUEST_CARD = SERVER_IP + "/request/Get_My_Request_C";
    public static final String DELETE_I_REQUEST_CARD = SERVER_IP + "/request/Delete_Request_Card_C";
    public static final String EDIT_I_REQUEST_CARD = SERVER_IP + "/request/Update_Request_Card_C";

    /*收藏
     */
    public static final String GET_I_COLLECT_CARD = SERVER_IP + "/collection/Get_Collection_C";
    public static final String ADD_COLLECTION = SERVER_IP + "/Add_Collection_C";
    public static final String REMOVE_COLLOECTION = SERVER_IP + "/Delete_Colletion_C";

    // 个人认证
    public static final String UPDATE_CREDIT = SERVER_IP + "/user/Update_Credit_C";

    //客服
    public static final String SEND_CUSTOMER_SERVICE = SERVER_IP + "/service/Add_Service_Chat_C";

    public static final String GET_CUSTOMER_SERVICE = SERVER_IP + "/service/Get_Service_Chat_C";

    // Location
    public static final String ADD_LOCATION = SERVER_IP + "/location/Add_Location_C";
    public static final String DELETE_LOCATION = SERVER_IP + "/location/Delete_Location_C";
    public static final String GET_LOCATION = SERVER_IP + "/location/Get_Location_C";

    // search
    public static final String SEARCH = SERVER_IP + "/card/Search_Card_C";

}
