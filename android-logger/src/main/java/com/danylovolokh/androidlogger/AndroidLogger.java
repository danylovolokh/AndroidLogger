package com.danylovolokh.androidlogger;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.IOException;

import com.volokh.danylo.vonalogger.VoNaLogger;

/**
 * This is a wrapper class above the VoNaLogger.
 * Basically it adds some corrections that are Android related.
 *
 * For example: we want every process to log into it's own files.
 * That's why log file names are modified and prefix is added to every process.
 * See {@link #initialize(Context, File, String, int, boolean)}
 *
 */
public class AndroidLogger {

    private static String mProcessName;

    private interface LogLevel {
        String VERBOSE = "verbose";
        String DEBUG = "debug";
        String INFO = "info";
        String WARN = "warn";
        String ERROR = "error";
    }

    private static VoNaLogger sVoNaLogger;


    public interface GetFilesCallback {
        void onFiles(File[] logFiles);
    }

    private AndroidLogger() {
    }

    /**
     * This method has to be called for every Android process if you want to get log files of every
     * process.
     *
     * This method stops logger and returns a list of files with logs in to {@link GetFilesCallback#onFiles(File[])}
     * After calling this method Android Logger has to be restarted by calling {@link #initialize(Context, String, String, int, boolean)}
     */
    public static void stopLoggingAndGetLogFiles(final GetFilesCallback filesCallback) {
        sVoNaLogger.stopLoggingAndGetLogFiles(new com.volokh.danylo.vonalogger.GetFilesCallback() {
            @Override
            public void onFilesReady(File[] logFiles) {
                filesCallback.onFiles(logFiles);
            }
        });
    }

    /**
     * This method has to be called for every Android process if you want to get log files of every
     * process.
     *
     * This method stops logger and returns a list of files.
     * After calling this method Android Logger has to be restarted
     * by calling {@link #initialize(Context, File, String, int, boolean)}
     */
    public static File[] stopLoggingAndGetLogFilesSync() {
        return sVoNaLogger.stopLoggingAndGetLogFilesSync();
    }

    /**
     * This method has to be called for every Android process if you want to get log files of every
     * process.
     *
     * This method stops to receive new logs.
     * Writing to file all the logs that were add until method was called.
     * Returns the array of files with logs.
     */
    public static File[] processPendingLogsStopAndGetLogFilesSync() {
        return sVoNaLogger.processPendingLogsStopAndGetLogFilesSync();
    }

    /**
     * This method has to be called for every Android process if you want to get log files of every
     * process.
     *
     * This method stops to receive new logs.
     * Writing to file all the logs that were add until method was called.
     * Returns the array of files with logs into {@link GetFilesCallback#onFiles(File[])}
     */
    public static void processPendingLogsStopAndGetLogFiles(final GetFilesCallback filesCallback) {
        sVoNaLogger.processPendingLogsStopAndGetLogFiles(new com.volokh.danylo.vonalogger.GetFilesCallback() {
            @Override
            public void onFilesReady(File[] logFiles) {
                filesCallback.onFiles(logFiles);
            }
        });
    }

    /**
     * This method has to be called for every Android process if you want to get log files of every
     * process.
     *
     * This method returns a snapshot of logging files without stopping the Logger.
     */
    public static File[] getLoggingFilesSnapShot() {
        return sVoNaLogger.getLoggingFilesSnapShotSync();
    }

    /**
     * This method re-initializes AndroidLogger after it was stopped by calling one of:
     * {@link #stopLoggingAndGetLogFiles(GetFilesCallback)}
     * {@link #stopLoggingAndGetLogFilesSync()}
     * {@link #processPendingLogsStopAndGetLogFiles(GetFilesCallback)}
     * {@link #processPendingLogsStopAndGetLogFilesSync()}
     *
     * After it's called it starts to receive new logs again.
     */
    public static void reinitAndroidLogger() throws IOException {
        sVoNaLogger.initVoNaLoggerAfterStopping();
    }

    public static int e(final String TAG, final String message) {
        return sVoNaLogger.writeLog(mProcessName, Thread.currentThread().getId(), LogLevel.ERROR, TAG, message);
    }

    public static int w(final String TAG, final String message) {
        return sVoNaLogger.writeLog(mProcessName, Thread.currentThread().getId(), LogLevel.WARN, TAG, message);
    }

    public static int d(final String TAG, final String message) {
        return sVoNaLogger.writeLog(mProcessName, Thread.currentThread().getId(), LogLevel.DEBUG, TAG, message);
    }

    public static int v(final String TAG, final String message) {
        return sVoNaLogger.writeLog(mProcessName, Thread.currentThread().getId(), LogLevel.VERBOSE, TAG, message);
    }

    public static int i(final String TAG, final String message) {
        return sVoNaLogger.writeLog(mProcessName, Thread.currentThread().getId(), LogLevel.INFO, TAG, message);
    }

    /**
     *
     * This method has to be called in the inheritor of the {@link android.app.Application} class.
     * In its {@link Application#onCreate()} method.
     *
     * Because it has to be called before you first call:
     *
     * {@link AndroidLogger#v(String, String)}
     * {@link AndroidLogger#d(String, String)}
     * {@link AndroidLogger#i(String, String)}
     * {@link AndroidLogger#w(String, String)}
     * {@link AndroidLogger#e(String, String)}
     *
     * @param context - this is a mandatory parameter. For every process the log files are slightly
     *                modified. The process name is added to the file name.
     * @param directory - this is the directory where the log files will be located.
     *                  It's better to set directory path on the internal storage because user
     *                  has to give a permission to write to external storage.
     * @param logFileName - The root of files name.
     * @param logFileMaxSizeBytes - The maximum amount of memory that is used on the system for the
     *                            log files.
     * @param showLogs - if this is "true" the logger will log into LogCat it's activity. It will
     *                 log its service data that should be used only for debug purposes and will affect
     *                 performance.
     * @throws IOException - throws exception if anything went wrong working with files.
     */
    public static void initialize(Context context,
                                  File directory,
                                  String logFileName,
                                  int logFileMaxSizeBytes,
                                  boolean showLogs
    ) throws IOException {

        mProcessName = getProcessNameByPID(context, android.os.Process.myPid());
        mProcessName = mProcessName.replace(":", "_");
        mProcessName = mProcessName.replace(".", "_");

        sVoNaLogger = new VoNaLogger
                .Builder()
                .setLoggerFilesDir(directory)
                .setLoggerFileName(logFileName + "_" + mProcessName)
                .setLogFileMaxSize(logFileMaxSizeBytes)
                .setShowLogs(showLogs)
                .build();

    }

    /**
     * Utils method to get default logger directory
     */
    public static File getDefaultLogFilesDirectory(Context context) {
        return context.getFilesDir();
    }

    private static String getProcessNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

}

