

package com.android.hiparker.lierda_light.adapter;


import com.android.hiparker.lierda_light.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author : Zhenshui.Xia
 * @date   : 2014-2-18
 * @desc   : 闪屏(初始化）页面
 */
public class FeatureAdapter extends FragmentPagerAdapter {
    private static final int[] FEATURES = new int[] {
    	R.drawable.feature_background1,
        R.drawable.feature_background2,
        R.drawable.feature_background3
    };

    private int mCount = FEATURES.length; 

    public FeatureAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FeatureFragment.newInstance(FEATURES[position % FEATURES.length], 
        		position==mCount-1);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return "";
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
    
    public static final class FeatureFragment extends Fragment {
        private static final String FEATURE_KEY = "feature_key";
        private static final String LAST_KEY = "last_key";
        private int mFeature;
        private boolean mLast;
        
        public static FeatureFragment newInstance(int feature, boolean last) {
            FeatureFragment fragment = new FeatureFragment();
            fragment.mFeature = feature;
            fragment.mLast = last;
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if ((savedInstanceState != null) 
            		&& savedInstanceState.containsKey(FEATURE_KEY)
            		&& savedInstanceState.containsKey(LAST_KEY)) {
            	mFeature = savedInstanceState.getInt(FEATURE_KEY);
            	mLast = savedInstanceState.getBoolean(LAST_KEY);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	View layout = inflater.inflate(R.layout.feature_layout, null);
        	ImageView imageView = (ImageView) layout.findViewById(R.id.feature_id_background);
        	imageView.setImageResource(mFeature);

        	//最后一页，显示按钮
//        	if(mLast) {
//        		start.setVisibility(View.VISIBLE);
//        		start.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						//启动框架页
//						SpUtil.setAppVersion(getActivity(), AppUtil.getVersionName(getActivity()));
//						Intent intent = new Intent(getActivity(), HomeActivity.class);
//						startActivity(intent);
//						getActivity().finish();
//					}
//				});
//        	} else {
//        		start.setVisibility(View.GONE);
//        	}
        	
            return layout;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(FEATURE_KEY, mFeature);
            outState.putBoolean(LAST_KEY, mLast);
        }
    }
}