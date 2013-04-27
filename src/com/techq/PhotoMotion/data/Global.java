package com.techq.PhotoMotion.data;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.techq.PhotoMotion.MainService;

import java.io.*;
import java.util.List;
import java.util.Map;

public class Global {

    public static Intent motionService;
    private static SharedPreferences preferences;
    private static long lastSettingsFileModTime = 0;
    private static long lastUpdateFileModTime = 0;

    private static File settingsFile;
    private static File updateFile;

    public static Detector detector;
    public static CameraHelper cameraHelper;

    public static void Startup(Context context) {

        settingsFile = new File(Preferences.SETTINGS_FILE_PATH);
        updateFile = new File(Preferences.SETTINGS_UPDATE_FILE_PATH);

        if (!Global.CreateFolder(Preferences.UPLOAD_DIR_PATH)) {
            new AddToLogTask().execute("Can't create upload folder");
        }
        if (!Global.CreateFolder(Preferences.SETTINGS_DIR_PATH)) {
            new AddToLogTask().execute("Can't create settings folder");
        }

        if (preferences == null) {
            SetPreferences(context);
        }

        if (cameraHelper == null) {
            cameraHelper = new CameraHelper();
        }

        if (detector == null) {
            detector = new Detector(Preferences.PREVIEW_WIDTH, Preferences.PREVIEW_HEIGHT, Preferences.PIXEL_THRESHOLD, Preferences.PICTURE_THRESHOLD());
        }

        if (motionService == null) {
            motionService = new Intent(context, MainService.class);
        }


    }

    public static void Close(Context context) {
        if (!Preferences.WORK_IN_BACKGROUND && motionService != null) {
            context.stopService(motionService);
            detector = null;
            cameraHelper.destroyCamera();
        }
    }

    public static boolean UpdateProcess() {
        boolean result = false;
        if (updateFile.exists()) {
            if (lastUpdateFileModTime != 0 && lastUpdateFileModTime != updateFile.lastModified()) {
                result = true;
            }
            lastUpdateFileModTime = updateFile.lastModified();
        }
        if (result) {
            UpdateApplication();
        }
        return result;
    }

    public static boolean PreferencesProcess() {
        boolean result = false;
        if (settingsFile.exists()) {
            if (settingsFile.lastModified() != lastSettingsFileModTime && LoadPreferences()) {
                result = true;
            }
        }
        return result;
    }

    public static boolean IsServiceRunning(Context context) {
        boolean result = false;
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(Preferences.SERVICE_CLASS_NAME)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static void Reboot() {
        new AddToLogTask().execute("Reboot");
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            proc.waitFor();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean SavePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("PICTURE_DELAY", Preferences.PICTURE_DELAY);
        editor.putInt("PREVIEW_DELAY", Preferences.PREVIEW_DELAY);
        editor.putInt("PIXEL_THRESHOLD", Preferences.PIXEL_THRESHOLD);
        editor.putInt("PICTURE_THRESHOLD_PERCENT", Preferences.PICTURE_THRESHOLD_PERCENT);
        editor.putInt("PICTURE_COMPRESSION", Preferences.PICTURE_COMPRESSION);
        editor.putInt("PICTURE_WIDTH", Preferences.PICTURE_WIDTH);
        editor.putInt("PICTURE_HEIGHT", Preferences.PICTURE_HEIGHT);
        editor.putInt("REBOOT_DELAY", Preferences.REBOOT_DELAY);
        editor.putInt("SERVICE_DELAY", Preferences.SERVICE_DELAY);
        editor.putBoolean("REBOOT_NEED", Preferences.REBOOT_NEED);
        editor.putBoolean("APPLICATION_UPDATE", Preferences.APPLICATION_UPDATE);
        editor.putBoolean("AUTOSTART_SERVICE", Preferences.AUTOSTART_SERVICE);
        editor.putBoolean("AUTOSTART_DROPSYNC", Preferences.AUTOSTART_DROPSYNC);
        editor.putBoolean("WORK_IN_BACKGROUND", Preferences.WORK_IN_BACKGROUND);
        editor.commit();

        boolean result = false;
        if (SaveSharedPreferencesToFile(preferences)) {
            lastSettingsFileModTime = settingsFile.lastModified();
            new AddToLogTask().execute("Settings saved");
            result = true;
        }
        return result;
    }

    private static boolean LoadPreferences() {
        boolean result = false;

        if (LoadSharedPreferencesFromFile(preferences)) {
            lastSettingsFileModTime = settingsFile.lastModified();

            Preferences.AUTOSTART_SERVICE = preferences.getBoolean("AUTOSTART_SERVICE", Preferences.AUTOSTART_SERVICE);
            Preferences.AUTOSTART_DROPSYNC = preferences.getBoolean("AUTOSTART_DROPSYNC", Preferences.AUTOSTART_DROPSYNC);
            Preferences.PICTURE_DELAY = preferences.getInt("PICTURE_DELAY", Preferences.PICTURE_DELAY);
            Preferences.PREVIEW_DELAY = preferences.getInt("PREVIEW_DELAY", Preferences.PREVIEW_DELAY);
            Preferences.PIXEL_THRESHOLD = preferences.getInt("PIXEL_THRESHOLD", Preferences.PIXEL_THRESHOLD);
            Preferences.PICTURE_THRESHOLD_PERCENT = preferences.getInt("PICTURE_THRESHOLD_PERCENT", Preferences.PICTURE_THRESHOLD_PERCENT);
            Preferences.PICTURE_COMPRESSION = preferences.getInt("PICTURE_COMPRESSION", Preferences.PICTURE_COMPRESSION);
            Preferences.PICTURE_WIDTH = preferences.getInt("PICTURE_WIDTH", Preferences.PICTURE_WIDTH);
            Preferences.PICTURE_HEIGHT = preferences.getInt("PICTURE_HEIGHT", Preferences.PICTURE_HEIGHT);
            Preferences.REBOOT_DELAY = preferences.getInt("REBOOT_DELAY", Preferences.REBOOT_DELAY);
            Preferences.SERVICE_DELAY = preferences.getInt("SERVICE_DELAY", Preferences.SERVICE_DELAY);
            Preferences.REBOOT_NEED = preferences.getBoolean("REBOOT_NEED", Preferences.REBOOT_NEED);
            Preferences.APPLICATION_UPDATE = preferences.getBoolean("APPLICATION_UPDATE", Preferences.APPLICATION_UPDATE);
            Preferences.WORK_IN_BACKGROUND = preferences.getBoolean("WORK_IN_BACKGROUND", Preferences.WORK_IN_BACKGROUND);

            new AddToLogTask().execute("Settings loaded");
            result = true;
        }
        return result;
    }

    private static void SetPreferences(Context context) {
        preferences = context.getSharedPreferences(Preferences.PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (LoadPreferences()) {
            Toast.makeText(context, "Settings loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean CreateFolder(String folderPath) {
        boolean result;
        File upDir = new File(folderPath);
        try {
            result = (upDir.exists() || upDir.mkdirs());
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private static boolean SaveSharedPreferencesToFile(SharedPreferences preferences) {
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(settingsFile));
            output.writeObject(preferences.getAll());
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    private static boolean LoadSharedPreferencesFromFile(SharedPreferences preferences) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(settingsFile));
            SharedPreferences.Editor prefEdit = preferences.edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, (Boolean) v);
                else if (v instanceof Float)
                    prefEdit.putFloat(key, (Float) v);
                else if (v instanceof Integer)
                    prefEdit.putInt(key, (Integer) v);
                else if (v instanceof Long)
                    prefEdit.putLong(key, (Long) v);
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    private static void UpdateApplication() {
        try {
            String[] commands = {
                    "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + Preferences.SETTINGS_UPDATE_FILE_PATH,
                    "LD_LIBRARY_PATH=/vendor/lib:/system/lib am start -n \"" + Preferences.PACKAGE_NAME + "/" + Preferences.ACTIVITY_CLASS_NAME + "\""
            };
            new AddToLogTask().execute("Updating");
            RootExecute(commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RootExecute(String... commands) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String command : commands) {
                os.writeBytes(command + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
