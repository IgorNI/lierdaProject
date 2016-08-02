package com.android.hiparker.lierda_light.view;

import com.android.hiparker.lierda_light.R;
import com.android.hiparker.lierda_light.utils.ScreenUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class LightTextView extends TextView {

	private Context mContext;
	private float mTopDrawableSize;
	public LightTextView(Context context) {
		super(context);
		mContext = context;
	}
	
	public LightTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView(context, attrs);
	}
	
	public LightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView(context, attrs);
	}

	private void initView(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LightTextView, 0, 0);
		try {
			mTopDrawableSize = ta.getDimension(R.styleable.LightTextView_upDrawableSize, ScreenUtil.dip2px(42));
        } finally {
            ta.recycle();
        }
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
	}

	public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
			Drawable top, Drawable right, Drawable bottom) {
		if (top != null && top instanceof BitmapDrawable && mTopDrawableSize > 0) {
			
			Bitmap topBitmap = drawableToBitmap(top);
			Bitmap colorBitmap = setupTargetBitmap(topBitmap);
			BitmapDrawable colorDrawable = new BitmapDrawable(
					mContext.getResources(), colorBitmap);
			top = colorDrawable;
		}
		super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		return ((BitmapDrawable)drawable).getBitmap();
	}

	private Bitmap setupTargetBitmap(Bitmap top) {
		Bitmap mBitmap = Bitmap.createBitmap((int)mTopDrawableSize,
				(int)mTopDrawableSize, Config.ARGB_8888);

		Canvas mCanvas = new Canvas(mBitmap);
    	Paint mPaint = new Paint();
    	mPaint.setColor(getTextColors().getDefaultColor());
		mPaint.setAntiAlias(true);
		
		int w = top.getWidth();
		int h = top.getHeight();
		Rect mIconRect = new Rect((int)((mTopDrawableSize - w)/2), (int)((mTopDrawableSize - h)/2), (int)((mTopDrawableSize + w)/2),
				(int)((mTopDrawableSize + h)/2));
		mPaint.setDither(true);
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		mCanvas.drawBitmap(top, null, mIconRect, mPaint);
		
		mPaint = new Paint();
    	mPaint.setColor(getTextColors().getDefaultColor());
		mPaint.setAntiAlias(true);
    	mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(5);
		mCanvas.drawCircle(mTopDrawableSize/2, mTopDrawableSize/2, mTopDrawableSize/2 - 3, mPaint);
		return mBitmap;
	}


	@Override
	public void setTextColor(int color) {
		super.setTextColor(color);
		
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
	}
}
