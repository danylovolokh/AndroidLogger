package com.danylovolokh;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.danylovolokh.androidlogger.AndroidLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by danylo.volokh on 1/31/17.
 */

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int STOP_LOGGER_AND_GET_LOGS = 0;
    private static final int PROCESS_PENDING_LOGS_STOP_LOGGER_AND_GET_LOGS = 1;

    private Handler mUiThreadHandler = new Handler();

    /**
     * Helper field
     */
    private String mLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        View sendLogs = findViewById(R.id.CTA);

        sendLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText logs = (EditText) findViewById(R.id.logs);
                AndroidLogger.v(TAG, logs.getText().toString());

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this);

        Menu menu = toolbar.getMenu();
        MenuItem item1 = menu.add(0, STOP_LOGGER_AND_GET_LOGS, Menu.NONE, "Stop Logger And Get Logs");
        item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        MenuItem item2 = menu.add(0, PROCESS_PENDING_LOGS_STOP_LOGGER_AND_GET_LOGS, Menu.NONE, "Process Pending logs. Stop Logger And Get Logs");
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    private void showFileContent(File file) {
        System.out.println(">> showFileContent, file " + file);
        System.out.println("showFileContent, file.length() " + file.length());

        BufferedReader inFile = null;

        int timeMillis = 1000;

        try {
            inFile = new BufferedReader(new FileReader(file));
            int index = 1;
            while((mLine = inFile.readLine()) != null)
            {
                final String line = mLine;
                System.out.println("> " + index++ + " mLine["+ mLine + "]");

                mUiThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, line, Toast.LENGTH_SHORT).show();
                    }
                }, index * timeMillis);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inFile!= null){
                    inFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("<< showFileContent, file " + file);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case STOP_LOGGER_AND_GET_LOGS:
                stopLoggerAndGetLogs();
                break;
            case PROCESS_PENDING_LOGS_STOP_LOGGER_AND_GET_LOGS:
                processPendingLogsStopLoggerAndGetLogs();
                break;
        }
        return false;
    }

    private void processPendingLogsStopLoggerAndGetLogs() {
        AndroidLogger.processPendingLogsStopAndGetLogFiles(new AndroidLogger.GetFilesCallback() {
            @Override
            public void onFiles(File[] logFiles) {
                System.out.println("onFilesReady, logFiles " + Arrays.toString(logFiles));

                for(File file: logFiles){
                    showFileContent(file);
                }

                try {
                    AndroidLogger.reinitAndroidLogger();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void stopLoggerAndGetLogs() {
        AndroidLogger.stopLoggingAndGetLogFiles(new AndroidLogger.GetFilesCallback() {
            @Override
            public void onFiles(File[] logFiles) {
                System.out.println("onFilesReady, logFiles " + Arrays.toString(logFiles));

                for(File file : logFiles){
                    showFileContent(file);
                }
                try {
                    AndroidLogger.reinitAndroidLogger();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
