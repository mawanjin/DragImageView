package org.melonframwork.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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
    public  float immutableX;
    /**
     * 图片y坐标 该字段不可变
     */
    public  float immutableY;

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

    public int paddingBottom = 0;

    /**
     * 小图标的真实坐标，用于点击事件.
     * */
    private float realX,realY;

    public Pointer(){}

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

    public boolean checkRange(float x, float y,boolean isTip,Pointer tip) {
        if (!clickable) return false;
        Log.d("Pointer::","x="+x+"y="+y+";immutableX="+immutableX+";immutableY="+immutableY+";width="+(immutableX + width)+";height="+(immutableY + height));

        if(isTip){
            if (x >= tip.getRealX() && y >= tip.getRealY() && x <= tip.getRealX() + width && y <= tip.getRealY() + height){
                Log.d("Pointer","checkRange=true");
                return true;
            }
        }else{
            if (x >= immutableX && y >= immutableY && x <= immutableX + width && y <= immutableY + height){
                Log.d("Pointer","checkRange=true");
                return true;
            }
        }

        Log.d("Pointer","checkRange=false");
        return false;
    }

    public float getRealX() {
        return realX;
    }

    public void setRealX(float realX) {
        this.realX = realX;
    }

    public float getRealY() {
        return realY;
    }

    public void setRealY(float realY) {
        this.realY = realY;
    }
}
