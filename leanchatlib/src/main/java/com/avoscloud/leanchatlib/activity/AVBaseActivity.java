package com.avoscloud.leanchatlib.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.event.EmptyEvent;
import com.avoscloud.leanchatlib.utils.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/7/24.
 */
public class AVBaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected ProgressDialog showSpinnerDialog() {
        //activity = modifyDialogContext(activity);
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setMessage(getString(R.string.chat_utils_hardLoading));
        if (!isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            LogUtils.logException(e);
            return false;
        } else {
            return true;
        }
    }

    //TODO
    public void startIntent(Intent intent) {
    }

    public void startAction(String action) {
    }

    public void startActivity(Class<?> cls, int requestCode) {
        startActivityForResult(new Intent(this, cls), requestCode);
    }

    public void onEvent(EmptyEvent emptyEvent) {
    }
}
