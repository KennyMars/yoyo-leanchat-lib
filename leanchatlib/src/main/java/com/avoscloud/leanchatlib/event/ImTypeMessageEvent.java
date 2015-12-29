package com.avoscloud.leanchatlib.event;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

/**
 * Created by wli on 15/8/23.
 */
public class ImTypeMessageEvent {
//  public AVIMTypedMessage message;
  public AVIMMessage message;
  public AVIMConversation conversation;
}
