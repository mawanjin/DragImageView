package org.melonframwork.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.List;

/**
 * User: melon
 * Date: 12/17/13
 * Time: 5:13 PM
 */
public class TouchImageView1 extends ImageView {
    String TAG = TouchImageView1.class.getName();

    private EventDispatcher eventDispatcher;
    private Activity mActivity;
    private MyAsyncTask myAsyncTask;// 异步动画
    Paint mPaint;
    PointF mid = new PointF();
    float x_down = 0;
    float y_down = 0;
    float oldDist = 1f;
    float oldRotation = 0;
    Matrix matrix;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;

    /**
     * 是否需要运行回弹动画
     */
    boolean isScaleAnim = false;
    boolean click = false;

    int widthScreen;
    int heightScreen;
    double minWidthScreen;
    double minHeightScreen;
    double maxWidthScreen;
    double maxHeightScreen;
    /**
     * 当前图片大小，放大后，或缩小后
     */
    float current_Width;
    float current_Height;
    float boundWidth = 800;
    float minBoundWidth = 200;

    /**
     * 图片居中时的上下间距
     */
    int marginLeft = 0, marginTop = 0;

    Bitmap originalMap;
    //原图
    Bitmap gintama;

    float postScale1;
    float postScale2;
    float postScale3;
    float postScale4;
    float scale = 1;
    float offsetX;
    float offsetY;
    float movePreX;
    float movePreY;
    //向下拖动时加的间距
    float paddingDown=20;
    //向上拖动时加的间距
    float paddingUp=20;
    Matrix inverseMatrix = new Matrix();
    //动态生成的可点击的控件
    List<? extends Pointer> viewItems;
    private float textSize=40;
    private boolean interceptor = true;

    public TouchImageView1(Context context, Bitmap background, List<? extends Pointer> pinters) {
        super(context);
        viewItems = pinters;
//        gintama = BitmapFactory.decodeResource(getResources(), background);
        gintama = background;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthScreen = dm.widthPixels;
        heightScreen = dm.heightPixels;

        maxWidthScreen = widthScreen * 3;
        maxHeightScreen = heightScreen * 3;
        minWidthScreen = widthScreen / 3;
        minHeightScreen = heightScreen / 3;

        Log.d(TAG, "width=" + gintama.getWidth() + ";height=" + gintama.getHeight() + ";widthScreen=" + widthScreen + ";heightScreen=" + heightScreen);

        matrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        originalMap = initMap();
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        mPaint.setAlpha(255);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(originalMap, 0, 0, mPaint);
        refreshPic(canvas);
        canvas.restore();
    }



    /**
     * 图片缩放到一定程序的时候，小图标不可点击。
     *
     * @param canvas
     */
    private void refreshPic(Canvas canvas) {
        retrieveCurrentMapInfo();
        click = true;
        double rat = current_Width / 1000;

        for (Pointer p : viewItems) {
//            if (rat >= 1) {
//                mPaint.setAlpha(255);
//                click = true;
//            } else {
//                int alpha = (int) (255 * rat);
//                click = true;
//                if (alpha < 200) click = false;
//                mPaint.setAlpha((int) (255 * rat));
//            }
            canvas.drawBitmap(p.bitmap, p.immutableX, p.immutableY, mPaint);
            if (p.showing) {

                Pointer tip = p.tip;
                canvas.drawBitmap(tip.bitmap, tip.immutableX, tip.immutableY, mPaint);

                Paint txtp = new Paint();
                txtp.setColor(Color.WHITE);
                txtp.setTextAlign(Paint.Align.CENTER);
                txtp.setAntiAlias(true);//去除锯齿
                txtp.setFilterBitmap(true);//对位图进行滤波处理
                txtp.setTextSize(textSize);
                canvas.drawText(p.txt, tip.immutableX + tip.width / 2, tip.immutableY + tip.height / 2 + tip.txtPadding, txtp);
            }
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
//                getParent().requestDisallowInterceptTouchEvent(interceptor);
                mode = DRAG;
                try {
                    x_down = event.getX();
                    y_down = event.getY();
                }catch (IllegalArgumentException e){
                    x_down=movePreX;
                    y_down=movePreY;
                }

                movePreX = x_down;
                movePreY = y_down;
                Onclick(x_down, y_down);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
//                savedMatrix.set(matrix);
                midPoint(mid, event);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    interceptor = true;
                    if (isScaleAnim) break;
                    float newDist = spacing(event);
                    scale = newDist / oldDist;
                    oldDist = newDist;
                    postScale1 = scale;
                    postScale2 = scale;
                    postScale3 = mid.x;
                    postScale4 = mid.y;
                    if (!matrixCheck()) {
                        matrix.postScale(scale, scale, mid.x, mid.y);// 縮放
                        invalidate();
                    } else {
                        if (!isScaleAnim) {
                            retrieveCurrentMapInfo();
                            doScaleAnim();
                            mode = NONE;
                        }
                    }
                } else if (mode == DRAG) {

                    try {
                        offsetX = event.getX() - movePreX;
                        offsetY = event.getY() - movePreY;
                        movePreX = event.getX();
                        movePreY = event.getY();
                    }catch (IllegalArgumentException e){
                    }

                    float[] a = checkBounds(offsetX, offsetY);
                    matrix.postTranslate(a[0], a[1]);// 平移
                    invalidate();
                    getParent().requestDisallowInterceptTouchEvent(interceptor);
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                mode = NONE;
                break;
        }
        return true;
    }

    private void Onclick(float x, float y) {
        if (!click) return;
        float[] a = {x, y};
        matrix.invert(inverseMatrix);
        inverseMatrix.mapPoints(a);
        Log.i(TAG, "onclick..x=" + x + ";y=" + y + ";a.x=" + a[0] + ";a.y=" + a[1]);
        for (Pointer pointer1 : viewItems) {
            //检查小图标点击事件
//            if (pointer1.checkRange(a[0], a[1])) {
//                if (!pointer1.showing) {//显示tip
//                    pointer1.showing = true;
//                } else
//                    pointer1.showing = false;
//                invalidate();
//                if (eventDispatcher != null) eventDispatcher.onClick(pointer1,false);
//                break;
//            }
//            //检查tip点击事件
//            if (pointer1.showing&&pointer1.tip.checkRange(a[0], a[1])) {
////                if (eventDispatcher != null) eventDispatcher.onClick(pointer1.tip);
//                if (eventDispatcher != null) eventDispatcher.onClick(pointer1,true);
//                break;
//            }
        }
    }

    /**
     * 检查平移是否越界，如果越界，则返回为0.
     *
     * @return
     */
    private float[] checkBounds(float _offsetX, float _offsetY) {
        interceptor = true;
        float[] a = new float[]{_offsetX, _offsetY};
        float[] f = new float[9];
        matrix.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * originalMap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * originalMap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * originalMap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * originalMap.getHeight() + f[5];
        float x4 = f[0] * originalMap.getWidth() + f[1] * originalMap.getHeight() + f[2];
        float y4 = f[3] * originalMap.getWidth() + f[4] * originalMap.getHeight() + f[5];

        Log.d(TAG, a[0] + ";" + a[1] + ";offset(x,y)=" + _offsetX + "," + _offsetY + "+checkBounds:: (x1,y1)=" + x1 + "," + y1 + ";(x2,y2)=" + x2 + "," + y2 + ";(x3,y3)=" + x3 + "," + y3 + ";(x4,y4)=" + x4 + "," + y4);

        if ((x1 + _offsetX - marginLeft) >= 0 && a[0] > 0) a[0] = 0;
        if ((x2 + _offsetX + marginLeft) <= widthScreen && a[0] < 0){interceptor=false;a[0] = 0;}
        //下向拖动
        if ((y2+_offsetY+paddingDown > 0 && a[1] > 0)) a[1] = 0;
        //向上拖动
        if ((y3+_offsetY-paddingUp < heightScreen && a[1] < 0)) a[1] = 0;
        return a;
    }

    /**
     * 检查是否已越界
     *
     * @return true 已越界 false 越界
     */
    private boolean matrixCheck() {
        retrieveCurrentMapInfo();
        // 缩放比率判断
        if (current_Width <= minWidthScreen || current_Width >= maxWidthScreen) return true;
        return false;
    }

    /**
     * 获取当前图片信息。更新currentwidth
     */
    private void retrieveCurrentMapInfo() {
        float[] f = new float[9];
        matrix.getValues(f);
        // 图片4个顶点的坐标
//        float x1 = f[0] * 0 + f[1] * 0 + f[2];
//        float y1 = f[3] * 0 + f[4] * 0 + f[5];
//        float x2 = f[0] * originalMap.getWidth() + f[1] * 0 + f[2];
//        float y2 = f[4] * 0 + f[5];
//        float x3 = x1;
//        float y3 = f[3] * 0 + originalMap.getHeight() + f[5];
//        float x4 = originalMap.getWidth() + f[2];
//        float y4 = y3;
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * originalMap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * originalMap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * originalMap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * originalMap.getHeight() + f[5];
        float x4 = f[0] * originalMap.getWidth() + f[1] * originalMap.getHeight() + f[2];
        float y4 = f[3] * originalMap.getWidth() + f[4] * originalMap.getHeight() + f[5];
        // 图片现宽度
        current_Width = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        try{
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return FloatMath.sqrt(x * x + y * y);
        }catch (IllegalArgumentException e){

        }
        return 0;
    }

    // 取屏幕中间点作为缩放的中心
    private void midPoint(PointF point, MotionEvent event) {
//        float x = event.getX(0) + event.getX(1);
//        float y = event.getY(0) + event.getY(1);
        point.set(widthScreen / 2, heightScreen / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 将移动，缩放以及旋转后的图层保存为新图片
    // 本例中沒有用到該方法，需要保存圖片的可以參考
    public Bitmap CreatNewPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen,
                Bitmap.Config.ARGB_8888); // 背景图片
        Canvas canvas = new Canvas(bitmap); // 新建画布
        canvas.drawBitmap(gintama, matrix, null); // 画图片
        canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布
        canvas.restore();
        return bitmap;
    }

    public Bitmap initMap() {
        //用于居中
        Bitmap bitmap = Bitmap.createBitmap(gintama.getWidth(), gintama.getHeight(),
                Bitmap.Config.ARGB_8888); // 背景图片
        Canvas canvas = new Canvas(bitmap); // 新建画布
        canvas.drawBitmap(gintama, matrix, null); // 画图片
        canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布

        if (widthScreen > gintama.getWidth()) {
            marginLeft = (widthScreen - gintama.getWidth()) / 2;
        }
        if (heightScreen > gintama.getHeight()) {
            marginTop = (heightScreen - gintama.getHeight()) / 2;
        }
        matrix.setTranslate(marginLeft, marginTop);
        canvas.restore();
        return bitmap;
    }

    /**
     * 缩放动画处理
     */
    public synchronized void doScaleAnim() {
        isScaleAnim = true;
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getPaddingDown() {
        return paddingDown;
    }

    public void setPaddingDown(float paddingDown) {
        this.paddingDown = paddingDown;
    }

    public float getPaddingUp() {
        return paddingUp;
    }

    public void setPaddingUp(float paddingUp) {
        this.paddingUp = paddingUp;
    }

    class MyAsyncTask extends AsyncTask<Void, Float, Void> {

        public MyAsyncTask() {
            super();
        }

        @Override
        protected Void doInBackground(Void... params) {

            /* 放小到一定程序，要回弹*/
            while (current_Width <= minWidthScreen + minBoundWidth) {

                onProgressUpdate(new Float[]{1.05f, 1.05f});
                try {
                    Thread.sleep(10);
                    retrieveCurrentMapInfo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
              /* 放大到一定程序，要回弹*/
            while (current_Width >= maxWidthScreen - boundWidth) {

                onProgressUpdate(new Float[]{0.95f, 0.95f});
                try {
                    Thread.sleep(30);
                    retrieveCurrentMapInfo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            isScaleAnim = false;
            return null;
        }

        @Override
        protected void onProgressUpdate(final Float... values) {
            super.onProgressUpdate(values);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    matrix.postScale(values[0], values[1], mid.x, mid.y);
                    invalidate();
                }
            });

        }

    }


    public interface EventDispatcher {
        public Pointer onClick(Pointer p, boolean isTip);
    }
}
