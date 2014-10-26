package com.jj.drag;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.List;

/**
 * User: mawanjin@join-cn.com
 * Date: 12/17/13
 * Time: 5:13 PM
 */
public class TouchImageViewbak extends ImageView {

    String TAG = TouchImageViewbak.class.getName();

    Paint mPaint;
    private Path mPath;
    Pointer pointer = new Pointer(getContext(),0,0,0,0);
    float x_down = 0;
    float y_down = 0;
    PointF mid = new PointF();
    float oldDist = 1f;
    float oldRotation = 0;
    Matrix matrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;

    boolean matrixCheck = false;
    boolean drawOthers = false;
    boolean isMoved = false;
    boolean click = false;

    int widthScreen;
    int heightScreen;

    Bitmap originalMap;
    //原图
    Bitmap gintama;

    float postScale1;
    float postScale2;
    float postScale3;
    float postScale4;
    //动态生成的可点击的控件
    List<Pointer> viewItems;

    float scale = 1;
    float offsetX;
    float offsetY;
    float movePreX;
    float movePreY;
    Matrix inverseMatrix = new Matrix();

    float[] pts = {100, 100};

    public TouchImageViewbak(Context context) {
        super(context);
        viewItems = ImageFactory.getInstance(context).getViewItems();
        gintama = BitmapFactory.decodeResource(getResources(), R.drawable.f1);
        Log.i(TAG, "width=" + gintama.getWidth() + ";height=" + gintama.getHeight());
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthScreen = dm.widthPixels;
        heightScreen = dm.heightPixels;
        matrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPath = new Path();
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

    private void refreshPic(Canvas canvas) {
        double scaledWidth = getWidthAfterScaled();
        double rat = scaledWidth / 1000;

        for (Pointer p : ImageFactory.getInstance(getContext()).getViewItems()) {
            if (rat >= 1) {
                mPaint.setAlpha(255);
                click = true;
            } else {
                int alpha = (int) (255 * rat);
                click = true;
                if (alpha < 200) click = false;
                mPaint.setAlpha((int) (255 * rat));
            }

            matrix.mapPoints(pts);
            Log.i(TAG, "sssssssssssscale=" +rat);

//            canvas.drawBitmap(p.getBitmap((float)(1/rat)), p.immutableX, p.immutableY, mPaint);
            canvas.drawBitmap(p.bitmap, p.immutableX, p.immutableY, mPaint);
        }

    }


    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                movePreX = x_down;
                movePreY = y_down;
//                Log.i(TAG, "x_down=" + x_down + ";y=" + y_down + ";leftTopX=" + pointer.leftTopX + ";leftTopY" + pointer.leftTopY);
                savedMatrix.set(matrix);
                Onclick(x_down, y_down);
                mPath.reset();
                mPath.moveTo(x_down, y_down);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
                    float rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    scale = newDist / oldDist;
                    postScale1 = scale;
                    postScale2 = scale;
                    postScale3 = mid.x;
                    postScale4 = mid.y;

                    matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
//                    matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
                    matrixCheck = matrixCheck(false);
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    } else
                        drawOthers = true;

//                    correctCoordinate(scale, mid.x, mid.y, viewItems);

                } else if (mode == DRAG) {
                    offsetX = event.getX() - movePreX;
                    offsetY = event.getY() - movePreY;
                    movePreX = event.getX();
                    movePreY = event.getY();

                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY() - y_down);// 平移
                    matrixCheck = matrixCheck(true);
                    if (matrixCheck == false) {
                        isMoved = true;
                        matrix.set(matrix1);
                        invalidate();
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    /**
     * 为图标加上点击事件
     */
    private void resetOnclick() {
//        Log.i(TAG, "resetOnclick called.");
//        if (viewItems == null) return;
//        for (Pointer pointer1 : viewItems) {
//            pointer1.scaledX += offsetX;
//            pointer1.scaledY += offsetY;
//            pointer1.leftTopX = pointer1.scaledX;
//            pointer1.leftTopY = pointer1.scaledY;
//            Log.i(TAG, "resetOnclick called.offsetX" + offsetX + ";offsetY=" + offsetY + ";pointer1.leftTopX=" + pointer1.leftTopX + ";pointer1.leftTopY=" + pointer1.leftTopY);
//        }

    }

    private void Onclick(float x, float y) {
        if (!click) return;
        float[] a = {x, y};
        matrix.invert(inverseMatrix);
        inverseMatrix.mapPoints(a);
        Log.i(TAG, "onclick..x=" + x + ";y=" + y + ";a.x=" + a[0] + ";a.y=" + a[1]);
        int i=0;
        for (Pointer pointer1 : viewItems) {
            if (pointer1.checkRange(a[0], a[1])) {
                //todo 点击事件
                break;
            }
            i++;
        }

//        ((TouchImageViewActivity)getContext()).onClick(x,y);

    }

    public double getWidthAfterScaled() {
        float[] f = new float[9];
        matrix1.getValues(f);
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * originalMap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * originalMap.getWidth() + f[4] * 0 + f[5];

        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return width;
    }

    private boolean matrixCheck(boolean resetCoordinate) {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * originalMap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * originalMap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * originalMap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * originalMap.getHeight() + f[5];
        float x4 = f[0] * originalMap.getWidth() + f[1] * originalMap.getHeight() + f[2];
        float y4 = f[3] * originalMap.getWidth() + f[4] * originalMap.getHeight() + f[5];
//        if (resetCoordinate) {
//            pointer.leftTopX = x1;
//            pointer.leftTopY = y1;
//        }

        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        // 缩放比率判断
        if (width < widthScreen / 3 || width > widthScreen * 3) {
            return true;
        }
        // 出界判断
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
            return true;
        }
        return false;
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
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
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000,
                Bitmap.Config.ARGB_8888); // 背景图片
        Canvas canvas = new Canvas(bitmap); // 新建画布
        matrix.setTranslate(-20, 50);
        canvas.drawBitmap(gintama, matrix, null); // 画图片
        canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布
        canvas.restore();
        return bitmap;
    }

    /**
     * 计算缩放后的坐标
     *
     * @param scale
     * @param pivotX
     * @param pivotY
     * @param pointers
     */
    public void correctCoordinate(float scale, float pivotX, float pivotY, List<Pointer> pointers) {

//        for (Pointer pointer1 : pointers) {
//            float formerX = pointer1.scaledX;
//            float formerY = pointer1.scaledY;
//
//            if (scale >= 1) {
//                float newX = pointer1.originX + (pointer1.originX - pivotX) * (scale - 1);
//                float newY = pointer1.originY + (pointer1.originY - pivotY) * (scale - 1);
//                newX = (newX - formerX) / 4;
//                newY = (newY - formerY) / 4;
//                pointer1.scaledX = formerX + newX;
//                pointer1.scaledY = formerY + newY;
//
//            } else {
//                float newX = pointer1.originX - (pointer1.originX - pivotX) * (1 - scale);
//                float newY = pointer1.originY - (pointer1.originY - pivotY) * (1 - scale);
//                newX = (newX - formerX) / 4;
//                newY = (newY - formerY) / 4;
//                pointer1.scaledX = formerX + newX;
//                pointer1.scaledY = formerY + newY;
//            }
//
//
//            Log.i(TAG, "correctCoordinate:x=" + pointer1.scaledX + ";y=" + pointer1.scaledY);
//        }
    }
}
