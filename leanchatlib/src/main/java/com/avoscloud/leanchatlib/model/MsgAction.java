package com.avoscloud.leanchatlib.model;

/**
 * 消息对应Msg.action
 */
public enum MsgAction {
    enmojtype(1),// sdk有一个自定义的enmojtype =1 了
    /**
     * 小蜜
     */
    system(2),
    /**
     * 跑步
     */
    runner(3) ,
    /**
     * 正在输入
     */
    typing(4),
    /**
     * 匹配
     */
    partner(5),
    /**
     * 聊天
     */
    msg(6),
    /**
     * 离开约跑
     */
    dispart(7);
    private int value = 0;

    private MsgAction(int value) {
        this.value = value;
    }

    public static MsgAction valueOf(int value) {
        switch (value) {
            case 1:
                return enmojtype;
            case 2:
                return system;
            case 3:
                return runner;
            case 4:
                return typing;
            case 5:
                return partner;
            case 6:
                return msg;
            default:
                return msg;
        }
    }

    public int value() {
        return this.value;
    }
}
