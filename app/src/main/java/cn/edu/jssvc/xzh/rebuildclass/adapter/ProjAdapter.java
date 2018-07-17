package cn.edu.jssvc.xzh.rebuildclass.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import cn.edu.jssvc.xzh.rebuildclass.R;
import cn.edu.jssvc.xzh.rebuildclass.activity.ClasstitleActivity;
import cn.edu.jssvc.xzh.rebuildclass.pojo.General;
import cn.edu.jssvc.xzh.rebuildclass.util.BitmapCacheUtils;
import cn.edu.jssvc.xzh.rebuildclass.util.LogUtil;
import cn.edu.jssvc.xzh.rebuildclass.util.NetCacheUtils;

/**
 * Created by xzh on 2017/2/7.
 *  课程名称适配器
 */

public class ProjAdapter extends RecyclerView.Adapter<ProjAdapter.ViewHolder> {


    private Context mContext;
    private List<General> mProjList;
    private  Handler handler;
    /***
     * 图片三级缓存工具类：
     */
    private BitmapCacheUtils bitmapCacheUtils;
    /**
     *  定义一个内部类ViewHolder继承自RecyclerView.ViewHolder
     *      构造函数传递最外层布局，然后通过findViewById()获取布局中的控件实例
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView projImage;
        TextView projName;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            projImage = (ImageView) itemView.findViewById(R.id.proj_image);
            projName = (TextView) itemView.findViewById(R.id.proj_name);

        }
    }

    /**
     *  将数据源传进来，并赋值给全局变量
     * @param projList-数据源
     */
    public ProjAdapter(Context mContext,List<General> projList,Handler handler) {
        this.mContext = mContext;
        this.mProjList = projList;
        this.handler = handler;
        bitmapCacheUtils = new BitmapCacheUtils(handler);

    }
    /**
     *  创建ViewHolder实例，加载proj_item布局
     *      将加载的布局传入到构造函数中，返回ViewHolder实例
     *      添加点击功能，点击View跳转到对应的界面，并传递id
     */
    @Override
    public ProjAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.proj_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                General proj = mProjList.get(position);
                Intent intent = new Intent(mContext,ClasstitleActivity.class);
                intent.putExtra("proj_id",String.valueOf(proj.getId()));
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    /**
     *  对当前项的控件进行赋值
     */
    @Override
    public void onBindViewHolder(ProjAdapter.ViewHolder viewHolder, int position) {
        General proj = mProjList.get(position);
        viewHolder.projName.setText(proj.getName());
        //2.使用自定义的三级缓存请求图片

            Bitmap bitmap = bitmapCacheUtils.getBitmap(proj.getImageId(),position);//内存或者本地
            if(bitmap != null){
                viewHolder.projImage.setImageBitmap(bitmap);
            }
      /*  //2.使用Picasso请求网络图片
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.home_scroll_default)
                    .error(R.drawable.home_scroll_default)
                    .into(viewHolder.iv_icon);
*/
        //3.使用Glide请求图片
/*
            Glide.with(mContext)
                    .load(proj.getImageId())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.logo)//真正加载的默认图片
                    .error(R.mipmap.logo)//失败的默认图片
                    .into(viewHolder.projImage);*/

        viewHolder.projImage.setTag(R.id.imageloader_uri,position);

       /*Glide.with(mContext).load(proj.getImageId()).into(viewHolder.projImage);*/
    }

    /**
     * @return 数据源的长度
     */
    @Override
    public int getItemCount() {
        if ( mProjList.size()==0){
            return mProjList.size();
        }else{
            return   mProjList.size();
        }

    }
}
