package com.techq.PhotoMotion.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.techq.PhotoMotion.MainService;
import com.techq.PhotoMotion.data.AddToLogTask;
import com.techq.PhotoMotion.data.Global;
import com.techq.PhotoMotion.data.Preferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Preferences.AUTOSTART_SERVICE) {
            Global.Startup(context);
            new AddToLogTask().execute("Start PhotoMotion");
            Global.motionService = new Intent(context, MainService.class);
            context.startService(Global.motionService);

            if (Preferences.AUTOSTART_DROPSYNC) {
                new AddToLogTask().execute("Start Dropsync");
                Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.ttxapps.dropsync");
                context.startActivity(LaunchIntent);
            }
        }
    }
}
