package com.avoscloud.leanchatlib.event;

/**
 * 刷新聊天详情列表的事件
 */
public class RefreshChatDetailEvent {
    public int position=0;
    public RefreshChatDetailEvent(int p){
        position = p;
    }
}
