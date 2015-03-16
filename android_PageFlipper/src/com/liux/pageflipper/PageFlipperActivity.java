package com.liux.pageflipper;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ViewFlipper;
/**
 * 悬浮按钮实现翻篇效果
 * @author liux  http://my.oschina.net/liux
 * @date 2012-2-10 下午2:48:52
 */
public class PageFlipperActivity extends Activity{
	 
	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams=null;
	
	private ImageView leftbtn=null;
	private ImageView rightbtn=null;
	
	// ImageView的alpha值   
	private int mAlpha = 0;
	private boolean isHide;
	
	private ViewFlipper viewFlipper = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        viewFlipper = (ViewFlipper) this.findViewById(R.id.myViewFlipper);
        
        //初始化悬浮按钮
        initFloatView();
    
    }
    /**
     * 初始化悬浮按钮
     */
	private void initFloatView(){
    	//获取WindowManager
    	wm = (WindowManager)getApplicationContext().getSystemService("window");
        //设置LayoutParams(全局变量）相关参数
    	wmParams = new WindowManager.LayoutParams();
    	
        wmParams.type=LayoutParams.TYPE_PHONE;   //设置window type
        wmParams.format=PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明
        //设置Window flag
         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
                               | LayoutParams.FLAG_NOT_FOCUSABLE;

        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x=0;
        wmParams.y=0;
        //设置悬浮窗口长宽数据
        wmParams.width=50;
        wmParams.height=50;
    	
        //创建悬浮按钮
        createLeftFloatView();
        createRightFloatView();
	}
    
    /**
	 * 创建左边悬浮按钮
	 */
    private void createLeftFloatView(){
    	leftbtn=new ImageView(this);
    	leftbtn.setImageResource(R.drawable.prev);
    	leftbtn.setAlpha(0);
    	leftbtn.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
				//上一篇
				viewFlipper.setInAnimation(PageFlipperActivity.this, R.anim.in_leftright);
				viewFlipper.setOutAnimation(PageFlipperActivity.this, R.anim.out_leftright);
				viewFlipper.showPrevious();
			}
		});
    	//调整悬浮窗口
        wmParams.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
        //显示myFloatView图像
        wm.addView(leftbtn, wmParams);
    }
    /**
	 * 创建右边悬浮按钮
	 */
    private void createRightFloatView(){
    	rightbtn=new ImageView(this);
    	rightbtn.setImageResource(R.drawable.next);
    	rightbtn.setAlpha(0);
    	rightbtn.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
				//下一篇
				viewFlipper.setInAnimation(PageFlipperActivity.this, R.anim.in_rightleft);
				viewFlipper.setOutAnimation(PageFlipperActivity.this, R.anim.out_rightleft);
				viewFlipper.showNext();
			}
		});
    	//调整悬浮窗口
        wmParams.gravity=Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        //显示myFloatView图像
        wm.addView(rightbtn, wmParams);
    }
    /**
     * 图片渐变显示处理
     */
    private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			if(msg.what==1 && mAlpha<255){   
				//System.out.println("---"+mAlpha);					
				mAlpha += 50;
				if(mAlpha>255)
					mAlpha=255;
		        leftbtn.setAlpha(mAlpha);
		        leftbtn.invalidate();
		        rightbtn.setAlpha(mAlpha);
		        rightbtn.invalidate();
				if(!isHide && mAlpha<255)
					mHandler.sendEmptyMessageDelayed(1, 100);
			}else if(msg.what==0 && mAlpha>0){
				//System.out.println("---"+mAlpha);
				mAlpha -= 10;
				if(mAlpha<0)
					mAlpha=0;
		        leftbtn.setAlpha(mAlpha);
		        leftbtn.invalidate();
		        rightbtn.setAlpha(mAlpha);
		        rightbtn.invalidate();
		        if(isHide && mAlpha>0)
		        	mHandler.sendEmptyMessageDelayed(0, 100);
			}			
		}
	};
	
    private void showFloatView(){
    	isHide = false;
    	mHandler.sendEmptyMessage(1);
    }
    
    private void hideFloatView(){
		new Thread(){
			public void run() {
				try {
	                Thread.sleep(1500);
	                isHide = true;
	                mHandler.sendEmptyMessage(0);
	            } catch (Exception e) {
	                ;
	            }
			}
		}.start();
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
    		case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_DOWN:
				//System.out.println("========ACTION_DOWN");
				showFloatView();			
				break;
			case MotionEvent.ACTION_UP:
				//System.out.println("========ACTION_UP");
				hideFloatView();				
				break;
		}
		return true;
	}

	@Override
    public void onDestroy(){
    	super.onDestroy();
    	//在程序退出(Activity销毁）时销毁悬浮窗口
    	wm.removeView(leftbtn);
    	wm.removeView(rightbtn);
    }
}
