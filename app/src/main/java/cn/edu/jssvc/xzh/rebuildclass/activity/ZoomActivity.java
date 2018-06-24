package cn.edu.jssvc.xzh.rebuildclass.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.bumptech.glide.Glide;

import cn.edu.jssvc.xzh.rebuildclass.R;

/**
 * Created by Rachel on 2017/4/11.
 */

public class ZoomActivity extends Activity{
    ZoomImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.zoom_layout);
        imageView = (ZoomImageView) findViewById(R.id.zoomview);

        Intent intent = getIntent();
        String data = intent.getStringExtra("extra_data");
        Log.d("SecondActivity", data);
        Glide.with(this).load(data).into(imageView);
        //没用
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ddddddddd", "hsw");
                ZoomActivity.this.finish();
            }
        });

    }
}
