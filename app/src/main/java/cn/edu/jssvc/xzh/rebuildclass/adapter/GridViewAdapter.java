package cn.edu.jssvc.xzh.rebuildclass.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.edu.jssvc.xzh.rebuildclass.R;
import cn.edu.jssvc.xzh.rebuildclass.activity.ExerciseContentActivity;
import cn.edu.jssvc.xzh.rebuildclass.pojo.General;
import cn.edu.jssvc.xzh.rebuildclass.util.BitmapCacheUtils;

/**
 * Created by xzh on 2017/3/14.
 * <p>
 * 练习界面的GridView适配器
 */

public class GridViewAdapter extends ArrayAdapter {

    private Context mContext;
    private List<General> mList;
    private  Handler handler;
    private BitmapCacheUtils bitmapCacheUtils;
    public GridViewAdapter(Context context, List<General> mList,Handler handler) {
        super(context, 0, mList);
        this.mContext = context;
        this.mList = mList;
        this.handler = handler;
        bitmapCacheUtils = new BitmapCacheUtils(handler);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final General general = mList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.exer_gridview_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.gridImage = (ImageView) convertView.findViewById(R.id.gridview_image);
            viewHolder.gridText = (TextView) convertView.findViewById(R.id.gridview_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (general != null) {
            viewHolder.gridText.setText(general.getName());
           /* Glide.with(mContext).load(general.getImageId()).into(viewHolder.gridImage);*/
            Bitmap bitmap = bitmapCacheUtils.getBitmap(general.getImageId(),position);//内存或者本地
            if(bitmap != null){
                viewHolder.gridImage.setImageBitmap(bitmap);
            }
            viewHolder.gridImage.setTag(R.id.imageloader_uri,position);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,String.valueOf(general.getId()),Toast.LENGTH_SHORT).show();
                Intent exerContentIntent = new Intent(mContext, ExerciseContentActivity.class);
                exerContentIntent.putExtra("exer_id", String.valueOf(general.getId()));
                mContext.startActivity(exerContentIntent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView gridImage;
        TextView gridText;
    }
}
