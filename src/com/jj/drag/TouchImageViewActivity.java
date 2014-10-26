package com.jj.drag;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mawanjin@join-cn.com
 * Date: 12/17/13
 * Time: 5:13 PM
 */
public class TouchImageViewActivity extends Activity {
    String TAG = TouchImageViewActivity.class.getName();
    FrameLayout container;
    FrameLayout picContainer;
    TouchImageView touchImageView;
    List<ImageView> images = new ArrayList<ImageView>(0);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_imageview_activity);
//        picContainer = (FrameLayout) findViewById(R.id.picContainer);
        container = (FrameLayout) findViewById(R.id.container);
        touchImageView = new TouchImageView(this,R.drawable.f1);
        touchImageView.setmActivity(this);
        touchImageView.setEventDispatcher(new TouchImageView.EventDispatcher() {
            @Override
            public Pointer onClick(Pointer p) {
                Log.d(TAG,"good");
                return null;
            }
        });
        container.addView(touchImageView);
    }

//    /**
//     * 通过缩放级别显示图片
//     */
//    public void freshPic(Double width) {
//        Log.i(TAG, "image width=" + width);
//        int i = 0;
//        //移动image
//        for (ImageView imageView : images) {
//            Pointer pointer = ImageFactory.getInstance(TouchImageViewActivity.this).getViewItems().get(i);
//            slideview(imageView, pointer.leftTopX, pointer.leftTopY);
//            i++;
//            Log.i(TAG, "scroll to::X=" + pointer.leftTopX + ";Y=" + pointer.leftTopY);
//        }
//    }
//
//
//    public void slideview(final View view, final float p1, final float p2) {
//        Log.i(TAG, "slideview=" + view.getX() + ";" + view.getY());
//        view.setX(p1);
//        view.setY(p2);
//        view.invalidate();
//    }
//
//    public void onClick(float x, float y) {
//        //右上角
//        int width = 61;
//        int height = 51;
//        if (x > 550 && x < width + 520 && y > 22 && y < height + 22) {
//
//        }
//        //返回
//        int widthBack = 52;
//        int heightBack = 38;
//        if (x > 80 && x < widthBack + 80 && y > 950 && y < heightBack + 950) {
//            finish();
//        }
//    }
}
