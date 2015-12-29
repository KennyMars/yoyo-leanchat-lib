package com.avoscloud.leanchatlib.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avoscloud.leanchatlib.Config;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.utils.PathUtils;

import java.io.File;
import java.io.IOException;

public class RecordButton extends Button {
    public static int BACK_RECORDING = R.drawable.chat_voice_bg_pressed;
    public static int BACK_IDLE = R.drawable.chat_voice_bg;
    public static final int SLIDE_UP_TO_CANCEL = 0;
    public static final int RELEASE_TO_CANCEL = 1;
    private static final int MIN_INTERVAL_TIME = 1000; // 录音最短时间
    private static final int MAX_INTERVAL_TIME = 60000; // 录音最长时间
    private static boolean sendFlag = false;
    private static int[] recordImageIds = {R.drawable.chat_icon_voice0,
            R.drawable.chat_icon_voice1, R.drawable.chat_icon_voice2,
            R.drawable.chat_icon_voice3, R.drawable.chat_icon_voice4,
            R.drawable.chat_icon_voice5};
    private TextView textView;
    private String outputPath = null;
    private RecordEventListener recordEventListener;
    private long startTime;
    private Dialog recordIndicator;
    private View view;
    private MediaRecorder mRecorder;
    private ObtainDecibelThread thread;
    private Handler volumeHandler;
    private ImageView imageView;
    private int status;
    private OnDismissListener onDismiss = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSavePath(String path) {
        outputPath = path;
    }

    public void setRecordEventListener(RecordEventListener listener) {
        recordEventListener = listener;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
        setBackgroundResource(BACK_IDLE);
    }
    private void aotuFinish(){
        if ( !sendFlag ){
            if (status == RELEASE_TO_CANCEL) {
                cancelRecord();
            } else {
                finishRecord();
            }
            sendFlag = true;
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (outputPath == null)
            return false;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startRecord();
                break;
            case MotionEvent.ACTION_UP:
                aotuFinish();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    status = RELEASE_TO_CANCEL;
                } else {
                    status = SLIDE_UP_TO_CANCEL;
                }
                setTextViewByStatus();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelRecord();
                break;
        }
        return true;
    }

    public int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    private void setTextViewByStatus() {
        if (status == RELEASE_TO_CANCEL) {
            textView.setTextColor(getColor(R.color.chat_record_btn_red));
            textView.setText(R.string.chat_record_button_releaseToCancel);
        } else if (status == SLIDE_UP_TO_CANCEL) {
            textView.setTextColor(getColor(R.color.chat_common_white));
            textView.setText(R.string.chat_record_button_slideUpToCancel);
        }
    }

    private void startRecord() {
        sendFlag = false;
        setSavePath(PathUtils.getRecordPathByCurrentTime());
        initRecordDialog();
        startTime = System.currentTimeMillis();
        setBackgroundResource(BACK_RECORDING);
        startRecording();
        recordIndicator.show();
    }

    private void initRecordDialog() {
        if (null == recordIndicator) {
            recordIndicator = new Dialog(getContext(),
                    R.style.chat_record_button_toast_dialog_style);

            view = inflate(getContext(), R.layout.chat_record_layout, null);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
            recordIndicator.setContentView(view, new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            recordIndicator.setOnDismissListener(onDismiss);

            LayoutParams lp = recordIndicator.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
        }
    }

    private void removeFile() {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void finishRecord() {
        stopRecording();
        recordIndicator.dismiss();
        setBackgroundResource(BACK_IDLE);

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(getContext(), getContext().getString(R.string.chat_record_button_pleaseSayMore), Toast.LENGTH_SHORT).show();
            removeFile();
            return;
        }

        int sec = Math.round(intervalTime * 1.0f / 1000);
        if (recordEventListener != null) {
            recordEventListener.onFinishedRecord(outputPath, sec);
        }
    }

    private void cancelRecord() {
        stopRecording();
        setBackgroundResource(BACK_IDLE);
        recordIndicator.dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.chat_cancelRecord),
                Toast.LENGTH_SHORT).show();
        removeFile();
    }

    private void startRecording() {
        try {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(outputPath);
                mRecorder.prepare();
            } else {
                mRecorder.reset();
                mRecorder.setOutputFile(outputPath);
            }
            mRecorder.start();
            thread = new ObtainDecibelThread();
            thread.start();
            recordEventListener.onStartRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (RuntimeException e) {
                removeFile();
            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public interface RecordEventListener {
        public void onFinishedRecord(String audioPath, int secs);

        void onStartRecord();
    }

    private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mRecorder == null || !running) {
                    break;
                }else if (System.currentTimeMillis() - startTime >= MAX_INTERVAL_TIME) {
                    // 如果超过最长录音时间
                    volumeHandler.sendEmptyMessage(999);
                    break;
                }
                int x = mRecorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    int index = (f - 18) / 5;
                    if (index < 0) index = 0;
                    if (index > 5) index = 5;
                    volumeHandler.sendEmptyMessage(index);
                }
            }
        }

    }

    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch ( msg.what ){
                case 999:
                    aotuFinish();
                    break;
                default:
                    imageView.setImageResource(recordImageIds[msg.what]);
            }
            //imageView.setImageResource(recordImageIds[5]);
        }
    }

    public void setBigVoiceButton(final boolean is) {
        BACK_RECORDING = is ? R.drawable.ic_run_send_voice_on : R.drawable.chat_voice_bg_pressed;
        BACK_IDLE = is ? R.drawable.ic_run_send_voice : R.drawable.chat_voice_bg;
        setBackgroundResource(BACK_IDLE);
        setText(is?"":getContext().getString(R.string.chat_bottom_record_layout_pressToRecord));
    }
}
