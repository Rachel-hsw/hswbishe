package cn.edu.jssvc.xzh.rebuildclass.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.jssvc.xzh.rebuildclass.R;
import cn.edu.jssvc.xzh.rebuildclass.adapter.GridViewAdapter;
import cn.edu.jssvc.xzh.rebuildclass.adapter.ViewPager2Adapter;
import cn.edu.jssvc.xzh.rebuildclass.pojo.General;
import cn.edu.jssvc.xzh.rebuildclass.util.LogUtil;
import cn.edu.jssvc.xzh.rebuildclass.util.MemoryCacheUtils;
import cn.edu.jssvc.xzh.rebuildclass.util.NetCacheUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.edu.jssvc.xzh.rebuildclass.util.ConnectUtil.webAddress;
import static cn.edu.jssvc.xzh.rebuildclass.util.IsInternet.isNetworkAvalible;

/**
 * Created by xzh on 2017/1/16.
 *
 *  练习界面
 *
 *      1、添加viewPager
 *      2、发送网络请求，读取练习列表数据
 */
public class ExerciseFragment extends Fragment {
    private static final int UPDATE = 0x00;
    private ViewPager viewPager;
    private List<Integer> mList;
    private ViewPager2Adapter mAdapter;
    private LinearLayout linear_point;
    private GridView gridView;
    private List<General> mGridList = new ArrayList<General>();
    private GridViewAdapter mGridViewAdapter;
    private String mResponseData = "";
    private Thread mThread;
    private Handler mHandler;
    private String IP = webAddress+ "";
    private View rootView;//缓存Fragment view
    private MemoryCacheUtils memoryCacheUtils ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            //假如有网络操作建议放在这里面，避免重复加载
            rootView = inflater.inflate(R.layout.fragment_exercise, container, false);
            memoryCacheUtils =new MemoryCacheUtils();
            if (isNetworkAvalible(getActivity())){
                setRequest();
                mThread.start();
            }
        } else {
            if (memoryCacheUtils != null) {
                 mResponseData=memoryCacheUtils.getJsonLruCache(2);
                if (mResponseData != null&&!mResponseData.equals("")) {
                    LogUtil.e("list取出=="+mResponseData);
                    mGridList.clear();
                    try {
                        parseJSON();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mGridViewAdapter.notifyDataSetChanged();

                }
            }else{
                if (isNetworkAvalible(getActivity())){
                    mThread.start();
                }
            }
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }}
        return rootView;
    }

    /**
     * 相当于Activity的onCreate()方法加载控件和操作
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            handleMessage();
            initView();
//        initDate();
//        initPoint();
            setViewPager();
          /*  mThread.start();*/

    }


    /**
     * 消息处理
     */
    private void handleMessage() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case UPDATE:
                        mGridViewAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 发送网络请求
     */
    private void setRequest() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(IP + "readExerName")
                            .build();
                    Response response = client.newCall(request).execute();
                    mResponseData = response.body().string();
                    memoryCacheUtils.addJsonLruCache(2,mResponseData);
                    LogUtil.e("list放入=="+mResponseData);
                    parseJSON();
                    mHandler.sendEmptyMessage(UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 解析JSON格式数据
     */
    private void parseJSON() throws JSONException {
        JSONArray jsonArray = new JSONArray(mResponseData);
        General[] generals = new General[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            General general = new General(jsonObject.getInt("id"),
                    jsonObject.getString("e_name"),
                    IP + jsonObject.getString("e_image"));
            generals[i] = general;
            mGridList.add(generals[i]);
        }
    }

    /**
     * 初始化图片指示器
     */
//    private void initPoint() {
//        for (int i = 0; i < mList.size(); i++) {
//            ImageView imageView = new ImageView(getActivity());
//            if (i == 0) {
//                imageView.setImageResource(R.mipmap.dot_black);
//            } else {
//                imageView.setImageResource(R.mipmap.dot_white);
//            }
//            linear_point.addView(imageView);
//        }
//    }

    /**
     * 设置ViewPager的适配器和监听器
     * 设置GridView的适配器和监听器
     */
    private void setViewPager() {
//        mAdapter = new ViewPager2Adapter(getActivity(), mList);
//        viewPager.setAdapter(mAdapter);
//        viewPager.addOnPageChangeListener(this);
//        viewPager.setCurrentItem(0);
        try{
       gridView = (GridView) getView().findViewById(R.id.gridview);
        mGridViewAdapter = new GridViewAdapter(getActivity(), mGridList,handler);
        gridView.setAdapter(mGridViewAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCacheUtils.SUCESS://图片请求成功
                    int position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ImageView projImage = (ImageView) gridView.findViewWithTag(position);
                    if(projImage != null && bitmap!= null){
                        projImage.setImageBitmap(bitmap);
                    }


                    LogUtil.e("请求图片成功=="+position);

                    break;
                case NetCacheUtils.FAIL://图片请求失败
                    position = msg.arg1;
                    LogUtil.e("请求图片失败=="+position);
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
//    private void initDate() {
//        mList = new ArrayList<Integer>();
//        mList.add(R.mipmap.img1);
//        mList.add(R.mipmap.img2);
//        mList.add(R.mipmap.img3);
//    }

    /**
     * 初始化控件
     */
    private void initView() {
        // ViewPager
//        viewPager = (ViewPager) getView().findViewById(R.id.view_pager);
//        linear_point = (LinearLayout) getView().findViewById(R.id.linear_point);

        // GridView
    }

//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        for (int i = 0; i < linear_point.getChildCount(); i++) {
//            ImageView imageView = (ImageView) linear_point.getChildAt(i);
//            if (i == position) {
//                imageView.setImageResource(R.mipmap.dot_black);
//            } else {
//                imageView.setImageResource(R.mipmap.dot_white);
//            }
//        }
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
}
