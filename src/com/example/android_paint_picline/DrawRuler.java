package com.example.android_paint_picline;

import java.text.DecimalFormat;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;



/**
 * 画“长度测量”的控件UI
 * @author Jack  
 * @version 创建时间：2014-1-24  上午10:01:44
 */
public class DrawRuler extends View {

	private DisplayMetrics dm;
	private float mMoveY1, mMoveY2;
	private Context mContext;
	private Paint paint;	// 声明画笔
	private Canvas canvas;	// 画布
	private Bitmap mBackGround;	// 背景图
	private Bitmap mHorizontalBitmap;  //水平线
	private Bitmap mVerticalBitmapCm, mVerticalBitmapInch;  //垂直尺
	private float screenWidth, screenHeight; // 屏幕的宽高
	private float marginTop = 0;
	/**按住选中一条线的影响范围*/
	private float effectiveRange = 50;
	
	/**
	 * 构造方法
	 * @param context
	 * @param width
	 * @param height
	 */
	public DrawRuler(Context context, int width, int height, DisplayMetrics dm) {
		super(context);
		mContext = context;
		this.dm = dm;
		screenHeight = height;
		screenWidth = width;
		
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		mBackGround = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // 设置位图的宽高
		mHorizontalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_horizontalline);
		mVerticalBitmapCm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_verticalline_cm);
		mVerticalBitmapInch = BitmapFactory.decodeResource(getResources(), R.drawable.bg_verticalline_inch);
		
		canvas = new Canvas();
		canvas.setBitmap(mBackGround);
		drawHorizontalLine(null, 0);   //在构造中画，否则不显示
		drawHorizontalLine(null, mHorizontalBitmap.getHeight()); 
		canvas.drawBitmap(mVerticalBitmapInch, 100, getTop(0), paint);
		canvas.drawBitmap(mVerticalBitmapCm, screenWidth - 160, getTop(0), paint);
		mMoveY1 = marginTop;
		mMoveY2 = marginTop + mHorizontalBitmap.getHeight();
	}

	/**
	 * 画位图，两条垂直线 
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBackGround, 0, 0, null);
		paint.setColor(getResources().getColor(R.color.verticallinebgcolor));
		canvas.drawBitmap(mVerticalBitmapInch, 100, getTop(0), paint);
		canvas.drawBitmap(mVerticalBitmapCm, screenWidth - 160, getTop(0), paint);
	}

	/**
	 * 获得左右两条线的起始点的纵坐标
	 * @param y
	 * @return
	 */
	public float getTop(float y) {
		return marginTop + y;
	}

	/**
	 * 监听触摸事件来画横标尺线
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {// 如果拖动
			clearCanvas();
			drawHorizontalLine(event, event.getY());
			invalidate();
			DecimalFormat format = new DecimalFormat("#.##");
			double inch = getInchFromPix(Math.abs(mMoveY1 - mMoveY2));
			paintInch(String.valueOf(format.format(inch)));
			paintCm(String.valueOf(format.format(inch * 2.54)));
		}
		return true; 
	}
	
	/**
	 * 画inch值
	 */
	public void paintInch(String inch){
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(30);
		canvas.rotate(90, 60, screenHeight / 2);
		canvas.drawText(inch, 20, screenHeight / 2, paint);
		canvas.rotate(-90, 60, screenHeight / 2);
	}
	
	/**
	 *  画cm值
	 * @param cm
	 */
	public void paintCm(String cm){
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(30);
		canvas.rotate(90, screenWidth -60, screenHeight /2);
		canvas.drawText(cm, screenWidth - 80, screenHeight / 2, paint);
		canvas.rotate(-90, screenWidth - 60, screenHeight / 2);
	}
	
	/**
	 * 通过像素点获得相应的inch大小
	 * @param pix
	 * @return
	 */
	public double getInchFromPix(float pix){
		double result = 0;
		int dpi  = dm.densityDpi;
		result = pix / dpi;
		return result;
	}
	
	
	/**
	 * 画水平分界线
	 * @param event
	 * @param y  初始化时用，画出上下两条线
	 */
	private void drawHorizontalLine(MotionEvent event, float temy){
		BitmapShader bitmapShader = new BitmapShader(mHorizontalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(bitmapShader);
		//创建原图的水平拉伸整个屏幕的副本
		Bitmap scaleBitmap = Bitmap.createScaledBitmap(mHorizontalBitmap, (int)screenWidth, mHorizontalBitmap.getHeight(), true);
		if(event != null && event.getAction() == MotionEvent.ACTION_MOVE){ //初始化画上面的线
			if(Math.abs(event.getY() - mMoveY1) <= effectiveRange){ //重画第一条
				mMoveY1 = getNewY(event.getY(), scaleBitmap);
				canvas.drawBitmap(scaleBitmap, 0f, mMoveY1, paint);
				canvas.drawBitmap(scaleBitmap, 0f, mMoveY2, paint);
			}else if(Math.abs(event.getY() - mMoveY2) <= effectiveRange){  //重画第二条
				canvas.drawBitmap(scaleBitmap, 0f, mMoveY1, paint);
				mMoveY2 = getNewY(event.getY(), scaleBitmap);
				canvas.drawBitmap(scaleBitmap, 0f, mMoveY2, paint);
			}else{  //都不画
				canvas.drawBitmap(scaleBitmap, 0f, mMoveY1, paint);
				canvas.drawBitmap(scaleBitmap, 0f, mMoveY2, paint);
			}
		}else{  //初始化画下面的线
			canvas.drawBitmap(scaleBitmap, 0f, getTop(temy), paint);
		}
	}

	
	/**
	 * 防止滑动线出界，设置最小的Y轴坐标
	 * @param y
	 * @return
	 */
	public int getNewY(float y, Bitmap bitmap){
		if(y < marginTop){
			y = marginTop;
		}else if(y > screenHeight - getNewMarginBottom()){
			y = screenHeight - getNewMarginBottom();
		}
		return (int)y;
	}
	
	/**
	 * 获得测量线和屏幕下方的边距，避免测量线出界看不见
	 * @return
	 */
	public float getNewMarginBottom(){
		float result = 0;
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		int height = displayMetrics.heightPixels / 2;
		if(height <= 480){  //854×480小摩托    lenove 960*540
			System.out.println("------->480density" + displayMetrics.density);   //1.5
			result = 125;
		}else if(height > 480 && height < 640){   //1280x720  海尔592
			System.out.println("------->640density" + displayMetrics.density);    //2.0
			result = 180;
		}else if(height >= 640 && height < 960){  //1280x720   galaxy3
			System.out.println("------->960density" + displayMetrics.density);
			result = 130;
		}else if(height >= 960){  //1920*1080  galaxys4 
			System.out.println("------->1920density" + displayMetrics.density);  //3.0
			result = 260;
		}
		return result;
	}
	
	/**
	 * 清除之前画过的刻度尺线
	 */
	public void clearCanvas() {

		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
	}

	public DrawRuler(Context context) {
		super(context);
	}
}