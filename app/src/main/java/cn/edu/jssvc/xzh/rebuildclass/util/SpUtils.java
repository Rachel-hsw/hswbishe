package cn.edu.jssvc.xzh.rebuildclass.util;

/**
 * Created by hsw on 2018/6/27.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.jssvc.xzh.rebuildclass.activity.BiSheApplication;
import cn.edu.jssvc.xzh.rebuildclass.pojo.Forum;


public class SpUtils {
    public static final String PRINTER = "printer";
    public static final String CURRENT_WORK = "current_work";
    private SharedPreferences sp;
    static SpUtils instance;
    final static String SP_SET_CONFIG = "config";

    public SpUtils() {
        sp = BiSheApplication.get().getSharedPreferences(SP_SET_CONFIG, Context.MODE_PRIVATE);
    }

    public static SpUtils get() {
        if (instance == null) {
            instance = new SpUtils();
        }
        return instance;
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public void setDataList(String tag, List<Forum> datalist) {
        try {
            SharedPreferences.Editor editor = sp.edit();
            if (null == datalist || datalist.size() <= 0)
                return;
            String strJson = JSON.toJSONString(datalist);
            editor.putString(tag, strJson);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public List<Forum> getDataList(String tag) {

        List<Forum> datalist = new ArrayList<Forum>();
        String strJson = sp.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        datalist = JSONObject.parseArray(strJson, Forum.class);
        return datalist;

    }
    public Object get(String key) {
        return sp.getAll().get(key);
    }

    public boolean put(String key, Object value) {
        SharedPreferences.Editor editor = sp.edit();
        if (value == null) {
            editor.putString(key, null);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            editor.putString(key, value.toString());
        }
        return editor.commit();
    }

    public boolean clear() {
        return sp.edit().clear().commit();
    }
}
