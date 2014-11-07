package com.jj.drag;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.melonframwork.android.Pointer;
import org.melonframwork.android.TouchImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends Activity {

    private FrameLayout container;
    private TouchImageView touchImageView;
    private List<Pointer> pointers = new ArrayList<Pointer>(0);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);
        container = (FrameLayout) findViewById(R.id.container);
        container.removeAllViews();

        initList();

        touchImageView = new org.melonframwork.android.TouchImageView(this,BitmapFactory.decodeResource(getResources(), R.drawable.f1),pointers);

        touchImageView.setmActivity(this);
        touchImageView.setEventDispatcher(new org.melonframwork.android.TouchImageView.EventDispatcher() {

            public org.melonframwork.android.Pointer onClick(Pointer p,boolean isTip) {
                //不是tip,则只隐藏或显示
                if (!isTip) return null;
                Toast.makeText(MainActivity1.this,"good"+p.txt,1000).show();
                return null;
            }
        });
        container.addView(touchImageView);

	}

    private void initList() {
        Pointer pointer = new Pointer();

        pointer.bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.marker_film);
        pointer.immutableX = 100;
        pointer.immutableY = 800;
        pointer.txt = "电影";
        pointer.width = pointer.bitmap.getWidth();
        pointer.height = pointer.bitmap.getHeight();

        Bitmap bitmapTip = BitmapFactory.decodeResource(getResources(), R.drawable.tip_frame);
        pointer.tip = new Pointer(this, bitmapTip, (pointer.immutableX + pointer.width) - pointer.width / 2 - bitmapTip.getWidth() / 2, pointer.immutableY - pointer.height);
        pointers.add(pointer);


        Pointer pointer11 = new Pointer();
        pointer11.bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.marker_film);
        pointer11.immutableX = 500;
        pointer11.immutableY = 800;
        pointer11.txt = "电影11";
        pointer11.width = pointer11.bitmap.getWidth();
        pointer11.height = pointer11.bitmap.getHeight();

        Bitmap bitmapTip1 = BitmapFactory.decodeResource(getResources(), R.drawable.tip_frame);
        pointer11.tip = new Pointer(this, bitmapTip1, (pointer11.immutableX + pointer11.width) - pointer11.width / 2 - bitmapTip1.getWidth() / 2, pointer11.immutableY - pointer11.height);

        pointers.add(pointer11);

    }

}