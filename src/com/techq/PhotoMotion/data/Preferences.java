package com.techq.PhotoMotion.data;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;

public abstract class Preferences {
    public final static String PREFERENCES_NAME = "pmotion_preferences";
    public final static String PACKAGE_NAME = "com.techq.PhotoMotion";
    public final static String ACTIVITY_CLASS_NAME = "com.techq.PhotoMotion.MainActivity";
    public final static String SERVICE_CLASS_NAME = "com.techq.PhotoMotion.MainService";
    public static int SERVICE_DELAY = 300000; // 5min

    private final static String UPLOAD_FOLDER = "Detections";
    public final static String UPLOAD_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + UPLOAD_FOLDER + File.separator;

    private final static String SETTINGS_FOLDER = "DetectionsSettings";
    private final static String SETTINGS_FILENAME = "preferences";
    private final static String SETTINGS_UPDATE_FILENAME = "update.apk";
    public final static String SETTINGS_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + SETTINGS_FOLDER;
    public final static String SETTINGS_FILE_PATH = SETTINGS_DIR_PATH + File.separator + SETTINGS_FILENAME;
    public final static String SETTINGS_UPDATE_FILE_PATH = SETTINGS_DIR_PATH + File.separator + SETTINGS_UPDATE_FILENAME;
    public static boolean WORK_IN_BACKGROUND = true;
    public static boolean AUTOSTART_SERVICE = true;
    public static boolean AUTOSTART_DROPSYNC = true;
    public static boolean APPLICATION_UPDATE = true;

    //public final static String LOG_TAG = "PhotoMotion";
    public final static String LOG_FILE_PATH = SETTINGS_DIR_PATH + File.separator + "log.txt";

    private final static String DATE_MASK = "dd.MM.yyyy_kk.mm.ss";
    public static String NOW_DATE_STRING() {
        Date d = new Date();
        return DateFormat.format(Preferences.DATE_MASK, d.getTime()).toString();
    }

    public static int REBOOT_DELAY = 86400000; // 24h
    public static boolean REBOOT_NEED = true;

    public static int PICTURE_WIDTH = 640;
    public static int PICTURE_HEIGHT = 480;
    public static int PICTURE_COMPRESSION = 10;
    public static int PICTURE_DELAY = 5000;
    public static int PIXEL_THRESHOLD = 30;
    public static int PICTURE_THRESHOLD_PERCENT = 25;

    public static int PICTURE_THRESHOLD() {
        return (PREVIEW_SIZE() / 100 * PICTURE_THRESHOLD_PERCENT);
    }

    public static int PREVIEW_DELAY = 300;
    public static int PREVIEW_WIDTH = 320;
    public static int PREVIEW_HEIGHT = 240;

    public static int PREVIEW_SIZE() {
        return PREVIEW_WIDTH * PREVIEW_HEIGHT;
    }
}
