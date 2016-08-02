package com.android.hiparker.lierda_light.view;

import com.android.hiparker.lierda_light.dao.LightTemp;
import com.android.hiparker.lierda_light.utils.ScreenUtil;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

public class LightThumb {
	public float mAngle = 0;
	public float centerX;
	public float centerY;
	
	private LightTemp mLightTemp;
	private Bitmap mTickBitmap;
	private float mTickSize;
	private int mTickTextColor;
	private float mTickTextSize;
	private Paint mPaint;

    // Indicates whether this thumb is currently pressed and active.
    private boolean mIsPressed = false;
    
	public LightThumb(LightTemp temp, Bitmap bitmap, float size, int textColor, float textSize, Paint paint) {
		mLightTemp = temp;
		mTickBitmap = bitmap;
		mTickSize = size;
		mTickTextColor = textColor;
		mTickTextSize = textSize;
		mPaint = paint;
		
		if (mTickBitmap != null) {
			mTickBitmap = Bitmap.createBitmap((int)size,
					(int)size, Config.ARGB_8888);
			
			Canvas mCanvas = new Canvas(mTickBitmap);

			mPaint.setColor(0xfff0eff5);
			mPaint.setStyle(Paint.Style.FILL);
			mCanvas.drawCircle(size/2, size/2, size/2, mPaint);
			int sc = mCanvas.saveLayer(0, 0, size, size, null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
					| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
					| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
					| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
			
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Rect mIconRect = new Rect((int)((size - w)/2), (int)((size - h)/2), (int)((size + w)/2),
					(int)((size + h)/2));
			Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
			pt.setAntiAlias(true);
			pt.setColor(textColor);
			pt.setDither(true);
			mCanvas.drawRect(mIconRect, pt);
			pt.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			mCanvas.drawBitmap(bitmap, null, mIconRect, pt);
			mCanvas.restoreToCount(sc);
			
			pt = new Paint(Paint.ANTI_ALIAS_FLAG);
			pt.setAntiAlias(true);
			pt.setColor(textColor);
			pt.setAntiAlias(true);
			pt.setStyle(Paint.Style.STROKE);
			pt.setStrokeWidth(5);
			mCanvas.drawCircle(size/2, size/2, size/2 - 3, pt);
		}
	}

	void draw(Canvas canvas) {
		float r = mTickSize/2;
		
		Rect src = new Rect(0, 0, mTickBitmap.getWidth(), mTickBitmap.getHeight());
		Rect dst = new Rect((int)(centerX - r), (int)(centerY - r), (int)(centerX + r), (int)(centerY + r));
		canvas.drawBitmap(mTickBitmap, src, dst, mPaint);
    	mPaint.setStyle(Paint.Style.FILL);
    	mPaint.setColor(mTickTextColor);
    	mPaint.setTextSize(mTickTextSize);
    	mPaint.setTextAlign(Paint.Align.CENTER);
    	FontMetrics fr = mPaint.getFontMetrics();
    	float height = fr.bottom - fr.top + 2;
    	int padding = ScreenUtil.dip2px(5);
    	float baseline = (int)(centerY + r + height/2 + padding);
		canvas.drawText(mLightTemp.getName(), centerX, baseline, mPaint);
	}
	

    boolean isPressed() {
        return mIsPressed;
    }

    void press() {
        mIsPressed = true;
    }

    void release() {
        mIsPressed = false;
    }
    
    boolean isInTargetZone(float x, float y) {
    	double r = mTickSize/2;
    	if (Math.abs(x - centerX) <= r && Math.abs(y - centerY) <= r) {
            return true;
        }
        return false;
    }

}
