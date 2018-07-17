package cn.edu.jssvc.xzh.rebuildclass.activity;

/**
 * Created by hsw on 2018/6/27.
 */

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import cn.edu.jssvc.xzh.rebuildclass.util.SpUtils;

public class BiSheApplication extends Application {

    private static BiSheApplication instance;
    private PendingIntent restartIntent;
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void uncaughtException(Thread thread, final Throwable ex) {
            String exMessage = "";
            try {
                JSONArray s = new JSONArray(ex.getStackTrace());
                exMessage = s.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String exType = ex.toString() != null ? ex.toString() : "unknow exType";
            Log.i("Application","应用Crash" + exType + "|" + exMessage);
            // 1秒钟后重启应用
            AlarmManager mgr = (AlarmManager) instance.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent);
            System.exit(0);
        }
    };

    public static BiSheApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initUncaughtException();
    }

    /**
     * init uncaught exception
     */
    private void initUncaughtException() {
        // 以下用来捕获程序崩溃异常
        Intent intent = new Intent();
        // 参数1：包名，参数2：程序入口的activity
        intent.setClassName(instance.getPackageName(), instance.getPackageName() + ".activity.PreparationActivity");
        restartIntent = PendingIntent.getActivity(instance, 0, intent, PendingIntent.FLAG_ONE_SHOT | Intent.FILL_IN_ACTION);

        // 程序崩溃时触发线程
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);
    }



}
