package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.EmotionHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wli on 15/9/17.
 *
 * @deprecated
 */
public class ChatItemTextYoyoXiaomiHolder extends ChatItemHolder {

    private static final String SCHEMA = "yoyo";
    protected TextView contentView, btn;

    public ChatItemTextYoyoXiaomiHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_text_layout_yoyoxiaomi, null));
            contentView = (TextView) itemView.findViewById(R.id.chat_left_text_tv_content);
            btn = (TextView) itemView.findViewById(R.id.chat_btn);
        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_right_text_layout, null));
            contentView = (TextView) itemView.findViewById(R.id.chat_right_text_tv_content);
        }
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        AVIMMessage message = (AVIMMessage) o;
        try {
            JSONObject json = new JSONObject(message.getContent());
            JSONObject data = json.optJSONObject("attr").optJSONObject("data");
            contentView.setText(EmotionHelper.replace(ChatManager.getContext(), getText(data)));
            setBtn(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getText(JSONObject data) {
        return data.optString("content");
    }

    private void setBtn(JSONObject data) {
        String text = null;
        if (data.optJSONObject("btn") != null)
            text = data.optJSONObject("btn").optString("text");
        final String url = data.optString("url");
        if (!TextUtils.isEmpty(text)) {
            btn.setVisibility(View.VISIBLE);
            btn.setText(text);
            conventLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(makeSchema(url));
                        intent.setData(content_url);
                        btn.getContext().startActivity(intent);
                    }
                }
            });
        } else {
            btn.setVisibility(View.GONE);
            conventLayout.setOnClickListener(null);
        }
    }

    private String makeSchema(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            url = url.replace("http", SCHEMA);
        }
        return url;
    }
}
