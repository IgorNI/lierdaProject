package com.android.hiparker.lierda_light;

import com.android.hiparker.lierda_light.adapter.FeatureAdapter;
import com.android.hiparker.lierda_light.utils.AppUtil;
import com.android.hiparker.lierda_light.utils.SpUtil;
import com.android.hiparker.lierda_light.view.viewpagerindicator.PageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * @date   : 2015-12-18
 * @desc   : 闪屏(初始化）页面
 */
public class FeatureActivity extends FragmentActivity  {
    private ViewPager pager;
    private PageIndicator indicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_activity);
        initView();
    }
    
    private void initView() {
      	FeatureAdapter adapter = new FeatureAdapter(getSupportFragmentManager());
    	pager = (ViewPager)findViewById(R.id.feature_id_pager);
    	pager.setAdapter(adapter);

    	indicator = (PageIndicator)findViewById(R.id.feature_id_indicator);
    	indicator.setViewPager(pager);
    	indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			private boolean misScrolled;
			
			@Override
			public void onPageSelected(int arg0) {
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				switch (state) {
				case ViewPager.SCROLL_STATE_DRAGGING:
					Log.i("zfc", "SCROLL_STATE_DRAGGING");
					misScrolled = false;
					break;
				case ViewPager.SCROLL_STATE_SETTLING:
					Log.i("zfc", "SCROLL_STATE_SETTLING");
					misScrolled = true;
					break;
				case ViewPager.SCROLL_STATE_IDLE:
					Log.i("zfc", "SCROLL_STATE_IDLE");
					if (pager.getCurrentItem() == pager.getAdapter().getCount() - 1 && !misScrolled) {
						SpUtil.setAppVersion(getBaseContext(), AppUtil.getVersionName(getBaseContext()));
						Intent intent = new Intent(getBaseContext(), HomeActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						finish();
					}
					misScrolled = true;
					break;
				}				
			}
		});
    }
    
    
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
    
    @Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}