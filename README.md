# AndroidLogger
This is a lightweight Logger to file for Android. It's based on VoNaLogger.

# Recommended usage

- Add this to your build.gradle file of the project.
```
buildscript {
    repositories {
        jcenter()
    }
}
```

- Add this dependency.
```
dependencies {
    compile 'com.github.danylovolokh:android-logger:1.0.2'
}
```

- Initalize AndroidLogger in your Application class before using it.

```
        File logsDirectory = AndroidLogger.getDefaultLogFilesDirectory(this);
        int logFileMaxSizeBytes = 2 * 1024 * 1024; // 2Mb
        try {
            AndroidLogger.initialize(
                    this,
                    logsDirectory,
                    "Log_File_Name",
                    logFileMaxSizeBytes,
                    false
                    );
        } catch (IOException e) {
            // Some error happened - most likely there is no free space on the system
        }
```

To log text to file use AndroidLogger like this.
```
        AndroidLogger.v("TAG", "Verbose Message");
        AndroidLogger.d("TAG", "Debug Message");
        AndroidLogger.i("TAG", "Info Message");
        AndroidLogger.w("TAG", "Warn Message");
        AndroidLogger.e("TAG", "Error Message");
```
NOTE: this will not log the message to LogCat!
If you want to log to both LogCat and File you need to create some kind of LoggerWrapper, like this.
```
public abstract class LoggerWrapper {
    
    public static int v(final String TAG, final String message) {
        Log.v(TAG, message);
        return AndroidLogger.v(TAG, message);
    }

    public static int d(final String TAG, final String message) {
        Log.d(TAG, message);
        return AndroidLogger.d(TAG, message);
    }

    public static int inf(final String TAG, final String message) {
        Log.i(TAG, message);
        return AndroidLogger.i(TAG, message);
    }

    public static int w(final String TAG, final String message) {
        Log.w(TAG, message);
        return AndroidLogger.w(TAG, message);
    }

    public static int err(final String TAG, final String message) {
        Log.e(TAG, message);
        return AndroidLogger.e(TAG, message);
    }
}
```
- How to get logs. I recommend to use this method:
```
    // after calling this method AndroidLoger stops receiving new logs so you will need to reinitialize it.
    AndroidLogger.processPendingLogsStopAndGetLogFiles(new AndroidLogger.GetFilesCallback() {
        @Override
        public void onFiles(File[] logFiles) {
            // get everything you need from these files
            try {
                AndroidLogger.reinitAndroidLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
```
There are also other options to get logs from file.
Please see the source code.

# How it works.
Here is a decription from VoNaLogger. Because basically AndroidLogger is jsut a wrapper.
The logs (any parameters passed to the logger) are stored in Log Entries. Log Entries are reused because the main goal of this library is to create the smallest amount of objects during writing to file.

![logging_animation_converted](https://cloud.githubusercontent.com/assets/2686355/22549863/1b5c202c-e956-11e6-9b07-b500391a06b8.gif)

# License

Copyright 2017 Danylo Volokh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


