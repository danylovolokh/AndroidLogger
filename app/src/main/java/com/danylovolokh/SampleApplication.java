package com.danylovolokh;

import android.app.Application;

import com.danylovolokh.androidlogger.AndroidLogger;

import java.io.File;
import java.io.IOException;

/**
 * Created by danylo.volokh on 1/31/17.
 */

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        File folder = AndroidLogger.getDefaultLogFilesDirectory(this);

        try {
            // 2 Kb
            int logFileMaxSizeBytes = 2 * 1024;
            AndroidLogger.initialize(
                    this,
                    folder,
                    "Test_log_file2",
                    logFileMaxSizeBytes,
                    true
            );
        } catch (IOException e) {
            e.printStackTrace();
//            Something went wrong and AndroidLogger cannot be used.
//            Handle the situation depending on the exception message

        }
    }
}
