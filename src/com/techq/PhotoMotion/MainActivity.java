package com.techq.PhotoMotion;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.techq.PhotoMotion.data.Global;
import com.techq.PhotoMotion.data.Preferences;

public class MainActivity extends Activity {

    private Button startBtn;
    private Button stopBtn;
    private Button settingsBtn;
    private Button previewBtn;

    private Intent previewActivity;
    private Intent settingsActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        startBtn = (Button) findViewById(R.id.btnStart);
        stopBtn = (Button) findViewById(R.id.btnStop);
        settingsBtn = (Button) findViewById(R.id.btnSettings);
        previewBtn = (Button) findViewById(R.id.btnPreview);

        if (Global.IsServiceRunning(this)) {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            settingsBtn.setEnabled(false);
            previewBtn.setEnabled(false);
        } else {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            settingsBtn.setEnabled(true);
            previewBtn.setEnabled(true);
        }

        Global.Startup(this);

        if(Preferences.AUTOSTART_SERVICE && !Global.IsServiceRunning(this)) {
            startService(Global.motionService);
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            settingsBtn.setEnabled(false);
            previewBtn.setEnabled(false);
        }
    }

    public void onDestroy() {
        Global.Close(this);
        super.onDestroy();
    }

    public void onClickBtnStart(View view) {
        if (!Global.IsServiceRunning(view.getContext())) {
            startService(Global.motionService);
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            settingsBtn.setEnabled(false);
            previewBtn.setEnabled(false);
        }
    }

    public void onClickBtnStop(View view) {
        if (Global.IsServiceRunning(view.getContext())) {
            stopService(Global.motionService);
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            settingsBtn.setEnabled(true);
            previewBtn.setEnabled(true);
        }
    }

    public void onClickBtnSettings(View view) {
        if(settingsActivity == null) {
            settingsActivity = new Intent(view.getContext(), SettingsActivity.class);
        }
        startActivity(settingsActivity);
    }

    public void onClickBtnPreview(View view) {
        if(previewActivity == null) {
            previewActivity = new Intent(view.getContext(), PreviewActivity.class);
        }
        startActivity(previewActivity);
    }
}
