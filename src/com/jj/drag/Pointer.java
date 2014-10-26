package com.jj.drag;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * User: melon
 * Date: 12/19/13
 * Time: 8:59 PM
 */
public class Pointer {
    String TAG = Pointer.class.getName();
    /**
     * 图片
     */
    public Bitmap bitmap;

    /**
     * 图片x坐标 该字段不可变
     */
    public final float immutableX;
    /**
     * 图片y坐标 该字段不可变
     */
    public final float immutableY;

    /**
     * 图片地址
     * */
    public float width;
    public float height;

    /**
     * 文字
     */
    public String txt = "";

    /**
     * 是否可点击
     */
    public boolean clickable = true;
    /**
     * 是否正在显示tip
     */
    public boolean showing;

    public Pointer tip;
    /**
     * 图片底部有一个小箭头，上面是文本框，文字居中是按整个图片的大小，所以要去掉箭头部分的高度。
     */
    public int txtPadding = 10;

    public Pointer(Context context, float _immutableX, float _immutableY, float _width, float _height) {
        this.immutableX = _immutableX;
        this.immutableY = _immutableY;
        this.width = _width;
        this.height = _height;
    }

    public Pointer(Context context, Bitmap _bitmap, float _immutableX, float _immutableY) {
        this.bitmap = _bitmap;
        this.immutableX = _immutableX;
        this.immutableY = _immutableY;
        this.width = _bitmap.getWidth();
        this.height = _bitmap.getHeight();
    }

    public boolean checkRange(float x, float y) {
        if (!clickable) return false;
//        Log.i(TAG, "checkRange called.x=" + x + "y=" + y + ";leftTopX=" + leftTopX + ";leftTopY=" + leftTopY + ";rangeXS=" + (leftTopX + width) + ";rangeYS=" + (leftTopY + height));
        if (x >= immutableX && y >= immutableY && x <= immutableX + width && y <= immutableY + height)
            return true;
        return false;
    }

}
