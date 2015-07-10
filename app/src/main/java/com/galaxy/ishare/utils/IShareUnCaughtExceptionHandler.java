package com.galaxy.ishare.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Created by liuxiaoran on 15/7/5.
 * 未使用
 */
public class IShareUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static String TAG = "IShareUnCaughtExceptionHandler";
    private static IShareUnCaughtExceptionHandler instance;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 使用Properties
    private Properties mDevicesCrashInfo = new Properties();

    /*
     是否开启日志输出，在DEBUG状态下开启，在Release状态下关闭
     */
    public static final boolean DEBUG = true;
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "stackTrace";
    // 错误报告文件的拓展名
    private static final String CRASH_REPORTER_EXTENSION = ".cr";

    public static IShareUnCaughtExceptionHandler getInstance() {

        if (instance == null) {
            instance = new IShareUnCaughtExceptionHandler();
        }
        return instance;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param
     */
    public void init(Context context) {
        this.mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误信息报告，
     *
     * @param ex
     * @return
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "sorry,程序出错了," + msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        // 保存错误报告文件
        String crashFileName = saveCrashInfoToFile(ex);
        // 发送错误报告到服务器
        sendCrashReportsToServer(mContext);
        return true;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     *
     * @param ctx
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));

            for (String fileName : sortedFiles) {
                File cr = new File(ctx.getFilesDir(), fileName);
                postReport(cr);
                cr.delete();// 删除已发送的报告
            }
        }
    }

    /**
     * 发送错误报告到服务器
     *
     * @param file
     */
    private void postReport(File file) {


    }

    /**
     * 获取错误报告文件名
     * 在/data/data/<package-name>/files下搜索以.cr结尾的文件
     *
     * @param ctx
     * @return
     */
    private String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }


    /**
     * 收集程序崩溃的设别信息
     *
     * @param ctx
     */
    public void collectCrashDeviceInfo(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                mDevicesCrashInfo.put(VERSION_NAME, packageInfo.versionName == null ? "not set" : packageInfo.versionName);
                mDevicesCrashInfo.put(VERSION_CODE, packageInfo.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDevicesCrashInfo.put(field.getName(), field.get(Build.class));
                if (DEBUG) {
                    Log.d(TAG, field.getName() + " : " + field.get(Build.class));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }

        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        mDevicesCrashInfo.put(STACK_TRACE, result);
        String fileName = "";
        try {
            long timestamp = System.currentTimeMillis();
            fileName = "crash-" + timestamp + CRASH_REPORTER_EXTENSION;
            /*
               文件保存在/data/data/<package-name>/files中，内部存储空间，通过context.getFileDir 能获得这个路径

             */
            FileOutputStream trace = mContext.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            mDevicesCrashInfo.store(trace, ""); // 将property保存在fileoutputstream 中
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file..."
                    + fileName, e);
        }
        return null;
    }

}
