package com.android.hiparker.lierda_light.view;

import com.android.hiparker.lierda_light.R;
import com.android.hiparker.lierda_light.utils.ScreenUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class ColorTextView extends TextView {

	private Context mContext;
	private float mTopDrawableSize;
	private float mDrawableTextSize;
	private int mTextNumber;
	public ColorTextView(Context context) {
		super(context);
		mContext = context;
	}

	public ColorTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView(context, attrs);
	}

	public ColorTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView(context, attrs);
	}
	
	private void initView(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorTextView, 0, 0);
		try {
			mTopDrawableSize = ta.getDimension(R.styleable.ColorTextView_topDrawableSize, ScreenUtil.dip2px(42));
			mTextNumber = ta.getInteger(R.styleable.ColorTextView_textNumber, -1);
			mDrawableTextSize = ta.getDimension(R.styleable.ColorTextView_drawableTextSize, ScreenUtil.sp2px(42));
        } finally {
            ta.recycle();
        }
		
		if (mTextNumber > -1) {
			Bitmap colorBitmap = setupTargetBitmap((int)mTopDrawableSize);
			BitmapDrawable colorDrawable = new BitmapDrawable(
					mContext.getResources(), colorBitmap);
	
			setCompoundDrawablesWithIntrinsicBounds(null, colorDrawable, null, null);
		}
	}
	
	public void setTextNumber(int number) {
		mTextNumber = number;
		if (mTextNumber > -1) {
			Bitmap colorBitmap = setupTargetBitmap((int)mTopDrawableSize);
			BitmapDrawable colorDrawable = new BitmapDrawable(
					mContext.getResources(), colorBitmap);
	
			setCompoundDrawablesWithIntrinsicBounds(null, colorDrawable, null, null);
		}
	}

	private Bitmap setupTargetBitmap(int size) {
		Bitmap mBitmap = Bitmap.createBitmap(size,
				size, Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mBitmap);

		Paint mPaint = new Paint();
		mPaint.setColor(getTextColors().getDefaultColor());
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
    	mPaint.setStyle(Paint.Style.STROKE);
		mCanvas.drawCircle(size/2, size/2, size/2 - 3, mPaint);
		
    	mPaint.setTextSize(mDrawableTextSize);
    	mPaint.setColor(getTextColors().getDefaultColor());
    	mPaint.setStyle(Paint.Style.FILL);
    	mPaint.setTextAlign(Paint.Align.CENTER);
    	FontMetrics fr = mPaint.getFontMetrics();
    	float fontHeight = fr.bottom + fr.top;
    	float testBaseY = (size - fontHeight)/2;
		mCanvas.drawText(String.valueOf(mTextNumber), size/2, testBaseY, mPaint);

		return mBitmap;
	}

	@Override
	public void setTextColor(int color) {
		super.setTextColor(color);
		
		if (mTextNumber > -1) {
			Bitmap colorBitmap = setupTargetBitmap((int)mTopDrawableSize);
			BitmapDrawable colorDrawable = new BitmapDrawable(
					mContext.getResources(), colorBitmap);
	
			setCompoundDrawablesWithIntrinsicBounds(null, colorDrawable, null, null);
		}
	}
}
