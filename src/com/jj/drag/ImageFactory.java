package com.jj.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mawanjin@join-cn.com
 * Date: 12/19/13
 * Time: 8:53 PM
 */
public class ImageFactory {
    private Context context;
    private static ImageFactory imageFactory;
    private List<Pointer> viewItems;

    public static ImageFactory getInstance(Context context) {
        if (imageFactory == null) imageFactory = new ImageFactory(context);
        return imageFactory;
    }

    public ImageFactory(Context context) {
        this.context = context;
        init();
    }

//    public void refresh() {
//        viewItems.clear();
//        init();
//    }

    public void init() {
        viewItems = new ArrayList<Pointer>(0);

        Bitmap bitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_film);
        Pointer p = new Pointer(context,bitmap,10,10);
        p.txt ="艺术影厅";

        Bitmap bitmapTip =  BitmapFactory.decodeResource(context.getResources(), R.drawable.tip_frame);
        p.tip = new Pointer(context,bitmapTip,(p.immutableX+p.width)/2-bitmapTip.getWidth()/2,p.immutableY-p.height);
        viewItems.add(p);

//        p = new Pointer(context);
//        p.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_tree);
//        p.id=R.drawable.thumb_tree;
//        p.immutableX = 260;
//        p.immutableY = 490;
//        p.originX = 260;
//        p.originY = 490;
//        p.leftTopX = 260;
//        p.leftTopY = 490;
//        //开始时origin和scaled应该是一致的
//        p.scaledX = 260;
//        p.scaledY = 490;
//        p.originWidth = 142;
//        p.originHeight = 118;
//        p.width = 142;
//        p.height = 118;
//
//        viewItems.add(p);
//
//        p = new Pointer(context);
//        p.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_geo);
//        p.id=R.drawable.thumb_geo;
//        p.immutableX = 400;
//        p.immutableY = 450;
//        p.originX = 400;
//        p.originY = 450;
//        p.leftTopX = 400;
//        p.leftTopY = 450;
//        //开始时origin和scaled应该是一致的
//        p.scaledX = 400;
//        p.scaledY = 450;
//        p.originWidth = 142;
//        p.originHeight = 118;
//        p.width = 142;
//        p.height = 118;

//        viewItems.add(p);


    }

    public List<Pointer> getViewItems() {
        return viewItems;
    }


}
