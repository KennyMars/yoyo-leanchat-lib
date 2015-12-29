package com.avoscloud.leanchatlib.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.event.EmptyEvent;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.LogUtils;

/**
 * Created by wli on 15/9/18.
 * @deprecated
 */
public class AVChatActivity extends AVBaseActivity {

    protected ChatFragment chatFragment;
    protected AVIMConversation conversation;
    protected TextView mTextTitle;
    protected LeanchatUser mToUser;
    protected LinearLayout mTopbarCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
        mTextTitle = (TextView) findViewById(R.id.title);
        mTopbarCount = (LinearLayout) findViewById(R.id.top_count);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initByIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initByIntent(intent);
    }

    private void initByIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (null != extras) {
            String to = "";
            if (extras.containsKey(Constants.MEMBER_IMID)) {
                to = extras.getString(Constants.MEMBER_IMID);
                getConversation(to);
            } else if (extras.containsKey(Constants.CONVERSATION_ID)) {
                to = conversation.getCreator();
                String conversationId = extras.getString(Constants.CONVERSATION_ID);
                updateConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId));
            } else {
            }
            mToUser = AVUserCacheUtils.getCachedUser(to);
            if(null!=mToUser)
                mTextTitle.setText(mToUser.getUsername());
        }
    }

    protected void initActionBar(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (title != null) {
                actionBar.setTitle(title);
            }
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            LogUtils.i("action bar is null, so no title, please set an ActionBar style for activity");
        }
    }

    /**
     * 显示or隐藏输入框
     * @param isShow
     */
    public void shInput(boolean isShow){
        chatFragment.setVisibilityOfInputBottomBar(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示or隐藏顶部计数
     * @param isShow
     */
    public void shTopbarCount(boolean isShow,boolean isInside){
        mTopbarCount.setVisibility(isShow ? View.VISIBLE : View.GONE);
        ((TextView)mTopbarCount.findViewById(R.id.top_count_l_unit)).setText(isInside ? "步数" : "公里");
    }


    private TextView mTopbarCountLeft,mTopbarCountRight;

    public void setTopbarCount(String l,String r){
        if(mTopbarCountLeft==null)
            mTopbarCountLeft = ((TextView)mTopbarCount.findViewById(R.id.top_count_l_text));
        if(mTopbarCountRight==null)
            mTopbarCountRight = ((TextView)mTopbarCount.findViewById(R.id.top_count_r_text));
        if(!TextUtils.isEmpty(l))mTopbarCountLeft.setText(l);
        if(!TextUtils.isEmpty(r))mTopbarCountRight.setText(r);
    }

    public void onEvent(EmptyEvent emptyEvent) {
    }

    protected void updateConversation(AVIMConversation conversation) {
        if (null != conversation) {
            this.conversation = conversation;
            chatFragment.setConversation(conversation);
            chatFragment.showUserName(ConversationHelper.typeOfConversation(conversation) != ConversationType.Single);
            initActionBar(ConversationHelper.titleOfConversation(conversation));
        }
    }

    /**
     * 从服务器端获取 conversation<br/>
     * 为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
     * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
     */
    private void getConversation(final String memberId) {
        ChatManager.getInstance().fetchConversationWithUserId(memberId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation conversation, AVIMException e) {
                if (filterException(e)) {
                    ChatManager.getInstance().getRoomsTable().insertRoom(conversation.getConversationId());
                    updateConversation(conversation);
                }
            }
        });
    }
}