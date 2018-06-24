package cn.edu.jssvc.xzh.rebuildclass.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者：尚硅谷-杨光福 on 2016/8/26 10:23
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：网络缓存工具类
 */
public class NetCacheUtils {

    /**
     * 请求图片成功
     */
    public static final int SUCESS = 1;
    /**
     * 请求图片失败
     */
    public static final int FAIL = 2;
    private final Handler handler;
    /**
     * 本地缓存工具类
     */
    private final LocalCacheUtils localCacheUtils;
    /**
     * 内存缓存工具类
     */
    private final MemoryCacheUtils memoryCacheUtils;
    /**
     * 线程池类
     */
    private ExecutorService service;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        this.handler = handler;
        service = Executors.newFixedThreadPool(10);
        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils =memoryCacheUtils;
    }

    //联网请求得到图片
    public void getBitmapFomNet(String imageUrl, int position) {
       doLogin(imageUrl,position);
    }
    private void doLogin(final String imageUrl, final int position) {

        try {
            RequestBody requestBody = new FormBody.Builder()
                    .build();
            HttpUtil.sendOkHttpRequest(imageUrl, requestBody, new Callback() {
                /**
                 * 请求失败
                 */
                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg = Message.obtain();
                    msg.what = FAIL;
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                }

                /**
                 * 请求成功
                 */
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //图片下到手机上都是以流的形式，得到一个输入流
                    InputStream is = response.body().byteStream();
                    //流在内存中，需要把一个流转化成一个bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //显示到控件上,发消息吧Bitmap发出去和position
                    Message msg = Message.obtain();
                    msg.what = SUCESS;
                    msg.arg1 = position;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                    //在内存中缓存一份
                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    //在本地中缓存一份
                    localCacheUtils.putBitmap(imageUrl,bitmap);
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    class MyRunnable implements Runnable {

        private final String imageUrl;
        private final int position;

        public MyRunnable(String imageUrl, int position) {
            this.imageUrl = imageUrl;
            this.position = position;
        }

        @Override
        public void run() {
            //子线程
            //请求网络图片
            try {
                URL urL = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) urL.openConnection();
                connection.setRequestMethod("GET");//只能大写
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.connect();//可写可不写
                int code = connection.getResponseCode();
                if(code ==200){
                    //图片下到手机上都是以流的形式，得到一个输入流
                    InputStream is = connection.getInputStream();
                    //流在内存中，需要把一个流转化成一个bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //显示到控件上,发消息吧Bitmap发出去和position
                    Message msg = Message.obtain();
                    msg.what = SUCESS;
                    msg.arg1 = position;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);

                    //在内存中缓存一份
                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    //在本地中缓存一份
                    localCacheUtils.putBitmap(imageUrl,bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Message msg = Message.obtain();
                msg.what = FAIL;
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        }
    }
}
