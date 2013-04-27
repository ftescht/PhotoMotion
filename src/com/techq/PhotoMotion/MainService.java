package com.techq.PhotoMotion;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.IBinder;
import android.widget.Toast;
import com.techq.PhotoMotion.data.Global;
import com.techq.PhotoMotion.data.Preferences;
import com.techq.PhotoMotion.data.SavePhotoTask;

public class MainService extends Service implements Camera.PictureCallback, Camera.PreviewCallback {

    private final long startTime = System.currentTimeMillis();
    private long lastDetectTime = startTime + (Preferences.PICTURE_DELAY * 2);
    private long lastFrameTime = startTime;
    private long lastServiceTime = startTime;

    public void onCreate() {
        super.onCreate();
        UpdateCameraPreferences();
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    public void onDestroy() {
        super.onDestroy();
        Global.cameraHelper.stopCamera();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ServiceProcess(long nowTime) {
        if (nowTime < (lastServiceTime + Preferences.SERVICE_DELAY)) {
            return;
        }
        lastServiceTime = nowTime;

        if (Global.PreferencesProcess()) {
            Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show();
            UpdateCameraPreferences();
        }

        if (Preferences.APPLICATION_UPDATE) {
            Global.UpdateProcess();
        }

        if (Preferences.REBOOT_NEED && nowTime > (startTime + Preferences.REBOOT_DELAY)) {
            this.stopSelf();
            Global.Reboot();
        }
    }

    private void UpdateCameraPreferences() {
        Global.cameraHelper.stopCamera();
        Global.cameraHelper.updateCamera();
        Global.detector.UpdateSettings(Preferences.PREVIEW_WIDTH, Preferences.PREVIEW_HEIGHT, Preferences.PIXEL_THRESHOLD, Preferences.PICTURE_THRESHOLD());
        Global.cameraHelper.startCamera(this);
    }

    private void PhotoProcess(long nowTime, byte[] dataFrame) {
        if (nowTime < (lastDetectTime + Preferences.PICTURE_DELAY))
            return;

        if (Global.detector.Compare(dataFrame)) {
            lastDetectTime = nowTime;
            Global.cameraHelper.takePicture(this);
        }
    }

    public void onPictureTaken(byte[] dataPhoto, Camera paramCamera) {
        if (dataPhoto != null) {
            new SavePhotoTask().execute(dataPhoto);
        }
        Global.cameraHelper.startCamera(this);
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

        PhotoProcess(nowTime, dataFrame);
        ServiceProcess(nowTime);
    }
}
