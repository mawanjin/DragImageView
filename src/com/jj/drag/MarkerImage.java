package com.jj.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * User: mawanjin@join-cn.com
 * Date: 14/10/25
 * Time: 16:56
 */
public class MarkerImage extends ImageView {

    public MarkerImage(Context context) {
        super(context);
    }

    public MarkerImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateMarker(int l,int t,int r,int b){
        setFrame(l,t,r,b);
    }
}
