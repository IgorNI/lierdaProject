package com.android.hiparker.lierda_light.base;

import com.android.hiparker.lierda_light.R;
import com.android.hiparker.lierda_light.utils.AppUtil;
import com.android.hiparker.lierda_light.utils.StringUtil;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseTitleActivity extends BaseActivity {

	private boolean mTipExit;
	private boolean mExit;

	private ProgressBar mPbProgress;
	private RelativeLayout mRlBack;
	private TextView mTxtTitle;
	private View mRlRefresh;
	private TextView mRightBtn;
	private ProgressBar mRefreshProgress;
	private ViewGroup mFlRoot;

	private OnBackListener mBackListener;
	private OnRefreshListener mRefreshListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_base);

		// 返回
		mRlBack = (RelativeLayout) findViewById(R.id.base_id_back);
		mRlBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBackListener != null) {
					mBackListener.onBack();
				} else {
					finish();
				}
			}
		});

		// 标题
		mTxtTitle = (TextView) findViewById(R.id.base_id_title);
		// 刷新
		mRlRefresh = findViewById(R.id.base_id_refresh);
		mRlRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRefreshListener != null)
					mRefreshListener.onRefresh();
			}
		});
		mRefreshProgress = (ProgressBar)findViewById(R.id.refresh_progress);
		mRightBtn = (TextView) findViewById(R.id.base_right_btn);

		//进度
		mPbProgress = (ProgressBar)findViewById(R.id.base_id_progress);
		// 内容
		mFlRoot = (ViewGroup) findViewById(R.id.base_id_root);
	}

	@Override
	public void onBackPressed() {
		if (mTipExit) {
			if (mExit) {
				finish();
			} else {
				mExit = true;
				AppUtil.showToast(this, R.string.main_exit);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mExit = false;
					}
				}, 2000);
			}
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 
	 */
	public void setContentView(int layoutResId) {
		View view = LayoutInflater.from(this).inflate(layoutResId, null);
		mFlRoot.addView(view);
	}

	/**
	 * 
	 */
	public void setContentView(View view) {
		mFlRoot.addView(view);
	}

	/**
	 * 设置标题
	 */
	public void setTopTitle(int resId) {
		mTxtTitle.setText(resId);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTopTitle(String title) {
		mTxtTitle.setText(StringUtil.nullToEmpty(title));
	}

	/**
	 * 是否显示返回功能
	 * 
	 * @param enabled
	 */
	public void enableBack(boolean enabled) {
		if (enabled) {
			mRlBack.setVisibility(View.VISIBLE);
		} else {
			mRlBack.setVisibility(View.GONE);
		}
	}

	/**
	 * 是否显示刷新功能
	 * 
	 * @param enabled
	 */
	public void enableRefresh(boolean enabled) {
		if (enabled) {
			mRlRefresh.setVisibility(View.VISIBLE);
		} else {
			mRlRefresh.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 是否显示进度条（圈）
	 * @param enabled
	 */
	public void enableProgress(boolean enabled) {
		if(enabled) {
			mPbProgress.setVisibility(View.VISIBLE);
		} else {
			mPbProgress.setVisibility(View.GONE);
		}
	}
	
	public void enableRefreshProgress(boolean enabled) {
		if (enabled) {
			mRlRefresh.setVisibility(View.GONE);
			mRefreshProgress.setVisibility(View.VISIBLE);
		} else {
			mRlRefresh.setVisibility(View.VISIBLE);
			mRefreshProgress.setVisibility(View.GONE);
		}
	}
	
	public void enableRightBtn(String text, View.OnClickListener listener) {
		mRightBtn.setText(text);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setOnClickListener(listener);
	}
	
	public void disableRightBtn() {
		mRightBtn.setVisibility(View.GONE);
	}

	/**
	 * 监听刷新事件
	 * 
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mRefreshListener = listener;
	}

	public interface OnBackListener {
		public void onBack();
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}
}
