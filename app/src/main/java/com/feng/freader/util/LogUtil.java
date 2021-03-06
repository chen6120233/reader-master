package com.feng.freader.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public class LogUtil {
    public static  final  boolean DEBUG = true;
    private static LogUtil sLogUtil;

    private LogUtil() {
    }

    public static LogUtil getInstance() {
        if (sLogUtil == null) {
            synchronized (LogUtil.class) {
                if (sLogUtil == null) {
                    sLogUtil = new LogUtil();
                }
            }
        }
        return sLogUtil;
    }


    public static void v(String msg){
        if(mLogEnable){
            android.util.Log.v(TAG, buildMessage(msg));
        }
    }
    public static void v(int msg){
        if(mLogEnable){
            android.util.Log.v(TAG, buildMessage(msg+""));
        }
    }
   public static void c(String msg){
        if(mLogEnable){
            int max_str_length = 2001 - TAG.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.i(TAG, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.i(TAG, msg);

        }
    }




    private static final String TAG = "LogUtils";

    /**
     * Whether output log
     */
    private static boolean mLogEnable = true;
    /**
     * Whether write log to file
     */
    private static boolean mWriteLogEnable;
    /**
     * The log dir parent path.
     */
    private static File mWriteLogDir = null;
    /**
     * The log dir.
     */
    private static final String DEFAULT_LOG_FILE_PATH = "log";
    /**
     * The format for date.
     */
    private static SimpleDateFormat mDateFormat = null;
    /**
     * The FileOutputStream for write log.
     */
    private static FileOutputStream mFileOutputStream = null;

    public static void init(Context context, boolean debug, boolean writeFile) {
        mLogEnable = debug;
        mWriteLogEnable = writeFile;
        if (mWriteLogEnable) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mWriteLogDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
            } else {
                mWriteLogDir = new File(context.getCacheDir(), context.getPackageName());
            }
            mWriteLogDir = new File(mWriteLogDir, DEFAULT_LOG_FILE_PATH);
        }
    }

    public static void setLogEnable(boolean logEnable) {
        mLogEnable = logEnable;
    }

    public static void setWriteFile(boolean writeFile) {
        mWriteLogEnable = writeFile;
    }

    public static void setWriteLogDir(String dirPath) {
        mWriteLogDir = new File(dirPath);
    }

    public static void d(String msg) {
        if (mLogEnable) {
            android.util.Log.d(TAG, buildMessage(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (mLogEnable) {
            android.util.Log.d(tag, buildMessage(msg));
        }
    }

    public static void i(String msg) {
        if (mLogEnable) {
            android.util.Log.i(TAG, buildMessage(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (mLogEnable) {
            android.util.Log.i(tag, buildMessage(msg));
        }
    }

    public static void w(String msg) {
        if (mLogEnable) {
            android.util.Log.w(TAG, buildMessage(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (mLogEnable) {
            android.util.Log.w(tag, buildMessage(msg));
        }
    }

    public static void e(String msg) {
        if (mLogEnable) {
            android.util.Log.e(TAG, buildMessage(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (mLogEnable) {
            android.util.Log.e(tag, buildMessage(msg));
        }
    }

    public static void e(Exception ex) {
        if (mLogEnable && null != ex) {
            android.util.Log.e(TAG, buildMessage(ex.toString()), ex);
        }
    }

    public static void e(String msg, Exception ex) {
        if (mLogEnable && null != ex) {
            android.util.Log.e(TAG, buildMessage(msg + ex.toString()), ex);
        }
    }

    public static void e(String tag, String msg, Exception ex) {
        if (mLogEnable && null != ex) {
            android.util.Log.e(tag, buildMessage(msg + ex.toString()), ex);
        }
    }

    private static String buildMessage(String msg) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        StringBuilder text = new StringBuilder();
        text.append(caller.getFileName()
                .replace(".java", ""))
                .append(".")
                .append(caller.getMethodName())
                .append("[")
                .append(caller.getLineNumber())
                .append("]:")
                .append(msg);
        if (mWriteLogEnable) {
            writeLog2File(text.toString());
        }
        return text.toString();
    }

    private static void writeLog2File(String text) {
        try {
            if (null == mFileOutputStream) {
                mDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS", Locale.getDefault());
                File file = new File(mWriteLogDir, "log_" + mDateFormat.format(new Date()) + ".txt");
                if (!file.exists()) {
                    File parentFile = file.getParentFile();
                    if (null != parentFile && !parentFile.exists() && !parentFile.mkdirs()) {
                        // has parent dir, but make failed.
                        android.util.Log.e(TAG, "mkdirs is " + false);
                        return;
                    }

                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            // has not file, but create failed.
                            android.util.Log.e(TAG, "createNewFile is " + false);
                            return;
                        }
                    }
                }
                mFileOutputStream = new FileOutputStream(file, true);
            }
            String log = mDateFormat.format(new Date()) + ":(" + TAG + ")" + " >> " + text + "\n";
            mFileOutputStream.write(log.getBytes());
            mFileOutputStream.flush();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Write error: " + e.toString());
        }
    }
}
