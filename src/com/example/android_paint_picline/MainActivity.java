package com.example.android_paint_picline;

import android.os.Bundle; 
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;


public class MainActivity extends Activity {

	private DrawRuler mDrawRuler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(new ColorDrawable(R.color.background_color));
		//得到屏幕宽高的画布
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mDrawRuler = new DrawRuler(this, dm.widthPixels, dm.heightPixels, dm);
		setContentView(mDrawRuler);// 将view视图放到Activity中显示
	}
}
