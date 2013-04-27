package com.techq.PhotoMotion;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;
import com.techq.PhotoMotion.data.Global;
import com.techq.PhotoMotion.data.Preferences;

public class PreviewActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private final long startTime = System.currentTimeMillis();
    private long lastFrameTime = startTime;

    private int curWidth = Preferences.PREVIEW_WIDTH;
    private int curHeight = Preferences.PREVIEW_HEIGHT;
    private int picThreshold = Preferences.PICTURE_THRESHOLD_PERCENT;
    private int pixThreshold = Preferences.PIXEL_THRESHOLD;

    private SeekBar previewBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.preview);

        previewBar = (SeekBar) findViewById(R.id.previewBar);

        SeekBar thresholdBar = (SeekBar) findViewById(R.id.thresholdBar);
        thresholdBar.setMax(100);
        thresholdBar.setProgress(picThreshold);
        thresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                picThreshold = i;
                Global.detector.setPictureThreshold(picThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        SeekBar pixelThresholdBar = (SeekBar) findViewById(R.id.pixelThresholdBar);
        pixelThresholdBar.setMax(255);
        pixelThresholdBar.setProgress(pixThreshold);
        pixelThresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pixThreshold = i;
                Global.detector.setPixelThreshold(pixThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.previewFrame);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        changeSize(width, height);
        Global.cameraHelper.updatePreviewSize(curWidth, curHeight);
        Global.cameraHelper.startCamera(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Global.detector.UpdateSettings(curWidth, curHeight, pixThreshold, picThreshold);
        if (Global.cameraHelper.createPreviewDisplay(holder)) {
            Global.cameraHelper.startCamera(this);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Global.cameraHelper.createPreviewDisplay(null);
        Global.cameraHelper.stopCamera();
        if (Preferences.PICTURE_THRESHOLD_PERCENT != picThreshold || Preferences.PIXEL_THRESHOLD != pixThreshold) {
            Preferences.PICTURE_THRESHOLD_PERCENT = picThreshold;
            Preferences.PIXEL_THRESHOLD = pixThreshold;
            if (Global.SavePreferences()) {
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPreviewFrame(byte[] dataFrame, Camera paramCamera) {
        if (dataFrame == null) {
            return;
        }

        long nowTime = System.currentTimeMillis();
        if (nowTime < (lastFrameTime + Preferences.PREVIEW_DELAY)) {
            return;
        }
        lastFrameTime = nowTime;

        int value = Global.detector.CompareAndResult(dataFrame);
        previewBar.setProgress(value);
    }

    private void changeSize(int w, int h) {
        curWidth = w;
        curHeight = h;
        previewBar.setMax(curWidth * curHeight);
        Global.detector.setSize(curWidth, curHeight);
    }
}
