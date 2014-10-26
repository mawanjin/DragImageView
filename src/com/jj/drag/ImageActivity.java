package com.jj.drag;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;

public class ImageActivity extends Activity {
	private int window_width, window_height;// 控件宽度
	private DragImageView dragImageView;// 自定义控件
    private MarkerImage markerContainer;
	private int state_height;// 状态栏的高度

	private ViewTreeObserver viewTreeObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
//        markerContainer = (MarkerImage) findViewById(R.id.markerContainer);
		/** 获取可视区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
		Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.f1,
				window_width, window_height);
		// 设置图片
		dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);//注入Activity.
		/** 测量状态栏高度 **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// 获取状况栏高度
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);
							dragImageView.setScreen_W(window_width);
						}

					}
				});

	}

    @Override
    protected void onStart() {
        super.onStart();
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                markerContainer.updateMarker(intent.getIntExtra("l",0),intent.getIntExtra("t",0),intent.getIntExtra("r",0),intent.getIntExtra("b",0));
//            }
//        },new IntentFilter("broadcast_marker"));

    }
}