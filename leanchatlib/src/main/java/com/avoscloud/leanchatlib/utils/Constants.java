package com.avoscloud.leanchatlib.utils;

/**
 * Created by wli on 15/8/23.
 * 用来存放各种 static final 值
 */
public class Constants {

    public static final String OBJECT_ID = "objectId";
    public static final int PAGE_SIZE = 10;
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";

    //TODO 还不知道这俩货是干嘛的
    public static final int ORDER_UPDATED_AT = 1;
    public static final int ORDER_DISTANCE = 0;

    private static final String LEANMESSAGE_CONSTANTS_PREFIX = "com.avoscloud.leanchatlib.";

    /**
     * 是否现实顶栏的数字统计（跑步中需要）
     */
    public static final String TOPBAR_COUNT = getPrefixConstant("topbar_count");
    public static final String TOPBAR_COUNT_TYPE = getPrefixConstant("topbar_count_type");
    public static final String TOPBAR_COUNT_LEFT = getPrefixConstant("topbar_count_left");
    public static final String TOPBAR_COUNT_RIGHT = getPrefixConstant("topbar_count_right");

    public static final String FROM = getPrefixConstant("from");
    public static final String MEMBER_NAME = getPrefixConstant("member_name");
    public static final String MEMBER_IMID = getPrefixConstant("member_imid");
    public static final String MEMBER_USERID = getPrefixConstant("member_userid");
    public static final String CONVERSATION_ID = getPrefixConstant("conversation_id");
    public static final String ACTIVITY_TITLE = getPrefixConstant("activity_title");


    //Notification
    public static final String NOTOFICATION_TAG = getPrefixConstant("notification_tag");
    public static final String NOTIFICATION_SINGLE_CHAT = Constants.getPrefixConstant("notification_single_chat");
    public static final String NOTIFICATION_GROUP_CHAT = Constants.getPrefixConstant("notification_group_chat");
    public static final String NOTIFICATION_SYSTEM = Constants.getPrefixConstant("notification_system_chat");



    public static String getPrefixConstant(String str) {
        return LEANMESSAGE_CONSTANTS_PREFIX + str;
    }
}
