package cn.edu.jssvc.xzh.rebuildclass.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import cn.edu.jssvc.xzh.rebuildclass.activity.BiSheApplication;

/**
 * Created by hsw on 2018/6/27.
 */

public  class TooUtil {


    public static  void getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) BiSheApplication.get().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
    }
}
