package com.techq.PhotoMotion.data;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class AddToLogTask extends AsyncTask<String, Integer, Integer> {

    protected Integer doInBackground(String... data) {
        for (String logText : data) {
            if (!logText.isEmpty()) {
                AppendToLog(logText);
            }
        }
        return 1;
    }

    private void AppendToLog(String text) {
        File logFile = new File(Preferences.LOG_FILE_PATH);
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    new AddToLogTask().execute("Can't create log file");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(Preferences.NOW_DATE_STRING());
            buf.append("_");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
