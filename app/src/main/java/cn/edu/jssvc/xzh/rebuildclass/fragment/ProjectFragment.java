package cn.edu.jssvc.xzh.rebuildclass.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.jssvc.xzh.rebuildclass.R;
import cn.edu.jssvc.xzh.rebuildclass.adapter.ProjAdapter;
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
 *  课程界面
 *
 *      发送网络请求，读取课程名称列表
 */
public class ProjectFragment extends Fragment {

    private static final int UPDATE = 0x00;
    // 存放从服务器获取的数据数据
    private General[] projs = {};
    private List<General> projList = new ArrayList<>();
    private ProjAdapter adapter;
    // 科目列表的服务器请求地址
    private String URL = webAddress+ "readProj";
    // 科目对应图片所在位置的网络请求地址
    private String IMG_URL = webAddress+ "";
    private Handler mHandler;
    private RecyclerView recyclerView;
    private MemoryCacheUtils memoryCacheUtils ;
    private View rootView;//缓存Fragment view
    /**
     * 加载对应的布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            //假如有网络操作建议放在这里面，避免重复加载
            rootView = inflater.inflate(R.layout.fragment_project, container, false);
            memoryCacheUtils =new MemoryCacheUtils();
            if (isNetworkAvalible(getActivity())){
                sendRequestWithOkHttp();
            }
        } else {
            if (memoryCacheUtils != null) {
            String responseData=memoryCacheUtils.getJsonLruCache(1);
            if (responseData != null&&!responseData.equals("")) {
                LogUtil.e("list取出=="+responseData);
                projList.clear();
                parseJSON(responseData);
                adapter.notifyDataSetChanged();

            }
            }else{
                if (isNetworkAvalible(getActivity())){
                    sendRequestWithOkHttp();
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
        initView();
        handleMessage();
        Log.i("hswtest2","hswtest");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     *  消息处理
     */
    private void handleMessage() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case UPDATE:
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 发送网络请求放入
     */
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(URL)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    memoryCacheUtils.addJsonLruCache(1,responseData);
                    LogUtil.e("list放入=="+responseData);
                    String ww=memoryCacheUtils.getJsonLruCache(1);
                    LogUtil.e("list取出=="+ww);
                    parseJSON(responseData);
                    mHandler.sendEmptyMessage(UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 解析从服务器获取的JSON格式数据并存放到Fruit数组中
     */
    private void parseJSON(String jsonData) {
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            projs = new General[jsonArray.length()];
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String pro_name = jsonObject.getString("pro_name");
                String pro_image = jsonObject.getString("pro_image");

                General f = new General(id,pro_name,IMG_URL+pro_image);
                projs[i] = f;
                projList.add(projs[i]);
            }


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
         recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        // 设置两列布局
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        Log.i("hswtest3","hswtest");
        adapter = new ProjAdapter(getActivity(),projList,handler);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCacheUtils.SUCESS://图片请求成功
                    int position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ImageView projImage = (ImageView) recyclerView.findViewWithTag(position);
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
}
