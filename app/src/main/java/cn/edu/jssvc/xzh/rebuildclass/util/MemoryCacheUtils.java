package cn.edu.jssvc.xzh.rebuildclass.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.List;

import cn.edu.jssvc.xzh.rebuildclass.pojo.General;


/**
 * 作者：尚硅谷-杨光福 on 2016/8/26 11:52
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：Java之软引用&弱引用&虚引用
 */
public class MemoryCacheUtils {
    /**
     * 缓存json数据
     */
    private LruCache<Integer,String> mJsonCache;
    /**
     * 缓存图片信息
     */
    /**
     * 集合
     */
    private LruCache<String,Bitmap> lruCache;

    public MemoryCacheUtils(){
        //使用了系统分配给应用程序的八分之一内存来作为缓存大小
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/1024/8);
        lruCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                return super.sizeOf(key, value);
                return (value.getRowBytes() * value.getHeight())/1024;
            }
        };
        mJsonCache = new LruCache<Integer, String>(1 * 1024 * 1024);
      /*  mJsonCache = new LruCache<String,String>(maxSize){
            @Override
            protected int sizeOf(String key,String value) {
//                return super.sizeOf(key, value);
                return 10 * 1024 * 1024;
            }
        };*/
    }

    /**
     * 根据url从内存中获取图片
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmapFromUrl(String imageUrl) {
        return lruCache.get(imageUrl);
    }

    /**
     * 根据url保存图片到lruCache集合中
     * @param imageUrl 图片路径
     * @param bitmap 图片
     */
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        lruCache.put(imageUrl,bitmap);
    }
    /**
     * 添加进入缓存列表
     *
     * @param key
     * @param value
     */
    public void addJsonLruCache(Integer key, String value) {
        mJsonCache.put(key, value);
    }

    /**
     * 从缓存列表中拿出来
     *
     * @param key
     * @return
     */
    public String getJsonLruCache(Integer key) {
        return mJsonCache.get(key);
    }


}
