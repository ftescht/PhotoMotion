package com.techq.PhotoMotion.data;

import android.os.AsyncTask;

import java.io.FileOutputStream;

public final class SavePhotoTask extends AsyncTask<byte[], Integer, Integer> {

    protected Integer doInBackground(byte[]... data) {
        for (byte[] bitmap : data) {
            if (bitmap != null) {
                SaveImage(bitmap);
            }
        }
        return 1;
    }

    private void SaveImage(byte... bitmap) {
        String fullPath = Preferences.UPLOAD_DIR_PATH + Preferences.NOW_DATE_STRING() + ".jpg";
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(fullPath);
            os.write(bitmap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}