package com.smart.mirrorer.util;

/**
 * Created by lzm on 16/3/26.
 */
public class Urls {

    public static boolean aMode = true;
//    private static final String URL_BASE_HOST = "https://api.mirrorer.com";
    private static final String URL_BASE_HOST = "https://dev.mirrorer.com";
//    private static final String URL_BASE_HOST = "http://112.74.67.189:9966";
//    private static final String URL_BASE_HOST = "http://112.74.67.189:8080";
//    private static final String URL_BASE_HOST = "http://112.74.67.189:9500";

    public static final String URL_REGISTER = URL_BASE_HOST+"/v1/user/register";
    public static final String URL_VOLID_CODE = URL_BASE_HOST+"/v1/user/code";
    public static final String URL_LOGIN = URL_BASE_HOST+"/v1/user/login";

    //获取首页信息
    public static final String URL_HOME_INDEX = URL_BASE_HOST+"/v1/home/index";
    public static final String URL_HOME_QUESTION = URL_BASE_HOST+"/v1/home/question";
    //删除问题数据
    public static final String URL_HOME_QUESTION_DELETE = URL_BASE_HOST+"/v1/question/remove";
    public static final String URL_LOGIN_OUT = URL_BASE_HOST+"/v1/user/logout";
    public static final String URL_HEART_CONNECT = URL_BASE_HOST+"/v1/user/active";

    //基本信息录入 行业列表
    public static final String URL_INDUSTRY_LIST = URL_BASE_HOST + "/v1/interest/category";
    //提问者端取消呼叫
//    public static final String URL_CANCEL_CALL = URL_BASE_HOST +"/v1/order/cancle";
    //上面的改成
    public static final String URL_CANCEL_CALL = URL_BASE_HOST +"/v1/question/cancle";
    //获取档案详情
    public static final String URL_RECORD_FILE = URL_BASE_HOST +"/v1/user/profile";
    //关注回答者
    public static final String URL_FOLLOW_TUTOR = URL_BASE_HOST +"/v1/user/follow/add";
    //取消关注回答者
    public static final String URL_CANCEL_FOLLOW_TUTOR = URL_BASE_HOST +"/v1/user/follow/remove";
    //已关注的回答者列表
    public static final String URL_FOLLOWED_TUTOR_LIST = URL_BASE_HOST +"/v1/user/follow/list";
    //我的问题，感兴趣的回答者
    public static final String URL_QUESTION_FOLLOW_TUTOR_LIST = URL_BASE_HOST +"/v1/question/inlist";
    //获取上传凭证
    public static final String URL_UPLOAD_TOKEN = URL_BASE_HOST + "/v1/upload/token";

//    public static final String URL_TUTOR_MATCH = URL_BASE_HOST + "/v1/guider/match";
    //上面的改成
    public static final String URL_TUTOR_MATCH = URL_BASE_HOST + "/v1/question/ask";

    public static final String URL_TUTOR_RECOMMOND = URL_BASE_HOST + "/v1/home/guider";
    public static final String URL_TUTOR_SWITCH = URL_BASE_HOST + "/v1/guider/switch";
    //回答者抢单
//    public static final String URL_TUTOR_ROB = URL_BASE_HOST + "/v1/guider/result";
    //上面的改成
    public static final String URL_TUTOR_ROB = URL_BASE_HOST + "/v1/question/result";

    //回答者录入
    public static final String URL_TUTOR_TYPE_IN = URL_BASE_HOST +"/v1/guider/base";
    //回答者关注感兴趣的单（问题）
    public static final String URL_TUTOR_FOLLOW_QUESTION = URL_BASE_HOST + "/v1/question/interest";

    //创建订单
    public static final String URL_CREATE_ORDER = URL_BASE_HOST + "/v1/order/create";
    //获取订单详情
    public static final String URL_ORDER_DETAILS = URL_BASE_HOST + "/v1/order/deatil";
    //意见反馈
    public static final String URL_FEED_BACK = URL_BASE_HOST + "/v1/user/feedback";
    //检查验证码，重置密码
    public static final String URL_PSW_RESET = URL_BASE_HOST + "/v1/user/password/reset";
    //获取钱包数据
    public static final String URL_MONEY_GET = URL_BASE_HOST + "/v1/user/wallet/balance";
    //获取评价标签
    public static final String URL_EVALUATE_TAGS = URL_BASE_HOST + "/v1/order/rated/tags";
    //提问者端评价回答者
    public static final String URL_EVALUATE_COMMIT = URL_BASE_HOST + "/v1/order/rated/guider";
    //自动更新
    public static final String URL_UPDATE = URL_BASE_HOST + "/v1/app/update";
    //获取通话动态key
    public static final String URL_CALL_TOKEN = URL_BASE_HOST + "/v1/call/token";
    //拒绝来电
    public static final String URL_CALL_REFUSE = URL_BASE_HOST + "/v1/call/refuse";
    //离开频道
    public static final String URL_CALL_LEFT = URL_BASE_HOST + "/v1/call/left";
    //加入频道
    public static final String URL_CALL_JOIN = URL_BASE_HOST + "/v1/call/join";
    //评价列表
    public static final String URL_GUIDER_RATED_LIST = URL_BASE_HOST + "/v1/guider/rated_list";
    //编辑个人信息
    public static final String URL_USER_EDIT = URL_BASE_HOST + "/v1/user/edit";
    //获取订单列表
    public static final String URL_ORDER_LIST= URL_BASE_HOST + "/v1/order/list";

    /**
     * #(r"/v1/guider/match", MatchGuiderHandler),     (r"/v1/question/ask", AskQuestionHandler),
     #(r"/v1/guider/result", MatchResultHandler),       (r"/v1/question/result", ResultQuestionHandler),
     #(r"/v1/order/cancle", CancleOrderHandler),      (r"/v1/question/cancle", CancleQuestionHandler),
     */
}
