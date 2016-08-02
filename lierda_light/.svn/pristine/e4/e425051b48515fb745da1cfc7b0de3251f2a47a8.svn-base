package com.android.hiparker.lierda_light;

import java.util.ArrayList;
import java.util.List;

import com.android.hiparker.lierda_light.dao.DaoMaster;
import com.android.hiparker.lierda_light.dao.DaoSession;
import com.android.hiparker.lierda_light.dao.Groups;
import com.android.hiparker.lierda_light.dao.GroupsDao;
import com.android.hiparker.lierda_light.pojo.Light;
import com.android.hiparker.lierda_light.pojo.LightManager;
import com.android.hiparker.lierda_light.utils.ScreenUtil;
import com.android.hiparker.lierda_light.view.ColorTextView;
import com.android.hiparker.lierda_light.view.LightTextView;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class HomeMenuFragment extends Fragment implements OnClickListener {
	public static final int REQUEST_CODE_EDIT_GROUP = 1;
	
	private Handler mHandler;
	private HomeActivity mActivity;
	private View mRlRefresh;
	private ProgressBar mRefreshProgress;
	
	private List<Groups> mGroups;
	private GroupsDao groupsDao;
	
	private GridLayout groupsLayout, lightLayout;
	private View addBtn;

	public HomeMenuFragment() {
		super();
		mHandler = new Handler();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = (HomeActivity) getActivity();
		mGroups = new ArrayList<Groups>();
		
		View layout = inflater.inflate(R.layout.activity_main, null);
		// 刷新
		mRlRefresh = layout.findViewById(R.id.base_id_refresh);
		mRlRefresh.setVisibility(View.VISIBLE);
		mRlRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.startScanLight(true);
			}
		});
		mRefreshProgress = (ProgressBar)layout.findViewById(R.id.refresh_progress);

		groupsLayout = (GridLayout) layout.findViewById(R.id.grid_groups);
		lightLayout = (GridLayout) layout.findViewById(R.id.grid_lights);
		addBtn = layout.findViewById(R.id.add);
		addBtn.setOnClickListener(this);

		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mActivity, "lights.db", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		groupsDao = daoSession.getGroupsDao();
		
		List<Groups> groups = groupsDao.loadAll();
		mGroups.addAll(groups);
		Groups[] gs = new Groups[0];
		loadGroupView(groups.toArray(gs));
		loadLightView(LightManager.getInstance().getLights().toArray(new Light[0]));

		return layout;
	}
	

	public void enableRefreshProgress(final boolean enabled) {
		if (mRlRefresh == null || mRefreshProgress == null) {
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enableRefreshProgress(enabled);
				}
			}, 100);
			return;
		}
		if (enabled) {
			mRlRefresh.setVisibility(View.GONE);
			mRefreshProgress.setVisibility(View.VISIBLE);
		} else {
			mRlRefresh.setVisibility(View.VISIBLE);
			mRefreshProgress.setVisibility(View.GONE);
		}
	}
	
	public void deleteGroupView(Groups groups) {
		for (Groups group : mGroups) {
			if (group.getId().equals(groups.getId())) {
				groups = group;
				break;
			}
		}
		mGroups.remove(groups);
		int childCount = groupsLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = groupsLayout.getChildAt(i);
			if (childView.getTag() == groups) {
				groupsLayout.removeViewAt(i);
				break;
			}
		}
		
		List<Light> lights = LightManager.getInstance().getLights();
		if (lights.isEmpty() || lights.get(0).device == null || lights.get(0).device.getAddress() == null) {
			mActivity.setControlEmtpy();
		} else {
			mActivity.setControlLight(lights.get(0).device.getAddress());
		}
	}
	
	public void loadGroupView(Groups...groups) {
		for (Groups group : groups) {
			ColorTextView view = (ColorTextView) LinearLayout.inflate(mActivity, R.layout.item_group, null);
			view.setText(group.getName());
			String s = group.getLights();
			String[] ls = s.split(",");
			view.setTextNumber(ls.length);
			view.setTextColor(group.getValue1());
			view.setTag(group);
			view.setOnClickListener(this);
			
			GridLayout.LayoutParams params = new GridLayout.LayoutParams();
			GridLayout.LayoutParams p = (LayoutParams) addBtn.getLayoutParams();
			params.columnSpec = p.columnSpec;
			params.topMargin = getResources().getDimensionPixelSize(R.dimen.grid_item_margin);
			params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.grid_item_margin);
			params.width = ScreenUtil.dip2px(80);
			groupsLayout.addView(view, 0, params);
		}
	}
	
	public void loadLightView(Light...lights) {
		if (lights == null) {
			return;
		}
		for (Light light : lights) {
			LightTextView view = (LightTextView) LinearLayout.inflate(mActivity, R.layout.item_light, null);
			view.setText(light.name);
			view.setTextColor(light.mColor);
			view.setTag(light);
			view.setOnClickListener(this);
			
			GridLayout.LayoutParams params = new GridLayout.LayoutParams();
			GridLayout.LayoutParams p = (LayoutParams) addBtn.getLayoutParams();
			params.columnSpec = p.columnSpec;
			params.topMargin = getResources().getDimensionPixelSize(R.dimen.grid_item_margin);
			params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.grid_item_margin);
			params.width = ScreenUtil.dip2px(80);
			lightLayout.addView(view, 0, params);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK) {
			Groups group = (Groups) data.getSerializableExtra("group");
			mGroups.add(group);
			loadGroupView(group);
		}
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.add:
			Intent intent = new Intent(mActivity, EditGroupActivity.class);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			break;
		}
		Object object = view.getTag();
		if (object != null) {
			if (object instanceof Light) {
				Light light = (Light) object;
				mActivity.setControlLight(light.device.getAddress());
				mActivity.toggle();
			} else if (object instanceof Groups) {
				Groups groups = (Groups) object;
				mActivity.setControlGroup(groups);
				mActivity.toggle();
			}
		}
	}
	
	public void refrshView() {
		int childCount = groupsLayout.getChildCount() - 1;
		for (int i = 0; i < childCount; i++) {
			ColorTextView childView = (ColorTextView) groupsLayout.getChildAt(i);
			Groups groups = (Groups) childView.getTag();
			childView.setText(groups.getName());
		}
		
		childCount = lightLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			LightTextView childView = (LightTextView) lightLayout.getChildAt(i);
			Light light = (Light) childView.getTag();
			childView.setText(light.name);
		}
	}
}
