package com.android.hiparker.lierda_light;

import java.util.ArrayList;
import java.util.List;

import com.android.hiparker.lierda_light.base.BaseTitleActivity;
import com.android.hiparker.lierda_light.constant.AppCst;
import com.android.hiparker.lierda_light.dao.DaoMaster;
import com.android.hiparker.lierda_light.dao.DaoSession;
import com.android.hiparker.lierda_light.dao.Groups;
import com.android.hiparker.lierda_light.dao.GroupsDao;
import com.android.hiparker.lierda_light.pojo.Light;
import com.android.hiparker.lierda_light.pojo.LightManager;
import com.android.hiparker.lierda_light.pojo.ScanRecord;
import com.android.hiparker.lierda_light.utils.ScreenUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseTitleActivity implements OnClickListener {
	public static final int REQUEST_CODE_EDIT_GROUP = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	public static final int SCAN_PERIOD = 20000;

	private boolean mScanning = false;
	private BluetoothManager bManager;
	private BluetoothAdapter bAdapter;
	private Handler mHandler;
	private List<Groups> mGroups;

	private static SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private GroupsDao groupsDao;

	private GridLayout groupsLayout, lightLayout;
	private View addBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScreenUtil.GetInfo(this);
		mHandler = new Handler();

		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
		    finish();
		    return;
		} else {
			bManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	        bAdapter = bManager.getAdapter();
		}
		// 检查设备上是否支持蓝牙
        if (bAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mGroups = new ArrayList<Groups>();

		setContentView(R.layout.activity_main);
		setTopTitle("Lights");
		enableRefresh(true);
		setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				startScanLight(true);
			}
		});

		groupsLayout = (GridLayout) findViewById(R.id.grid_groups);
		lightLayout = (GridLayout) findViewById(R.id.grid_lights);
		addBtn = findViewById(R.id.add);
		addBtn.setOnClickListener(this);

		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lights.db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		groupsDao = daoSession.getGroupsDao();

		List<Groups> groups = groupsDao.loadAll();
		mGroups.addAll(groups);
		Groups[] gs = new Groups[0];
		loadGroupView(groups.toArray(gs));
//		loadLightView(null);
		// 此处原本是loadLightView(null)
		// build时会出现警告，显示：
		// 最后一个参数使用了不准确的变量类型的 varargs 方法的非 varargs 调用; 对于 varargs 调用, 应使用 Light 对于非 varargs
		// 网上解释，JDK 1.5以后支持可变参数，以上的写法不明确，编译器不知道你是不想传参数呢，还是想传一个null的参数
		// 所以，如果你不想传参数的时候，可以用ms[0].invoke(o,new Object[0])就可以了，它表示参数的长度是０
		// 或者以下这种方式
		Light[] objs = null;
		loadLightView(objs);

        if (!bAdapter.isEnabled()) {
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
        	startScanLight(true);
        }
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.add:
			Intent intent = new Intent(this, EditGroupActivity.class);
			startActivityForResult(intent, REQUEST_CODE_EDIT_GROUP);
			break;
		}
		Object object = view.getTag();
		if (object != null) {
			if (object instanceof Light) {
				Light light = (Light) object;
				Intent intent = new Intent(this, LightControlActivity.class);
				intent.putExtra("lightAddress", light.device.getAddress());
				startActivity(intent);
			} else if (object instanceof Groups) {
				Groups group = (Groups) object;

				Intent intent = new Intent(this, LightControlActivity.class);
				intent.putExtra("group", group);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_EDIT_GROUP && resultCode == RESULT_OK) {
			Groups group = (Groups) data.getSerializableExtra("group");
			long id = groupsDao.insert(group);
			group.setId(id);
			loadGroupView(group);
		} else if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
			startScanLight(true);
		}
	}

	private void loadGroupView(Groups...groups) {
		for (Groups group : groups) {
			TextView view = (TextView) LinearLayout.inflate(this, R.layout.item_group, null);
			view.setText(group.getName());
			groupsLayout.addView(view, 0);
		}
	}

	private void loadLightView(Light...lights) {
		if (lights == null) {
			return;
		}
		for (Light light : lights) {
			TextView view = (TextView) LinearLayout.inflate(this, R.layout.item_light, null);
			view.setText(light.name);
			view.setTag(light);
			view.setOnClickListener(this);
			lightLayout.addView(view, 0);
		}
	}

	private void startScanLight(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run() {
					mScanning = false;
					bAdapter.stopLeScan(mLeScanCallback);
					enableRefreshProgress(false);
				}
			}, SCAN_PERIOD);
			mScanning = true;
			bAdapter.startLeScan(mLeScanCallback);
			enableRefreshProgress(true);
		} else {
			mScanning = false;
			bAdapter.stopLeScan(mLeScanCallback);
			enableRefreshProgress(false);
		}
	}

	private LeScanCallback mLeScanCallback = new LeScanCallback(){

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			int type = device.getType();
			Log.d("", "----scan type:" + type + "         name:"+device.getName());
			ScanRecord record = ScanRecord.parseFromBytes(scanRecord);

			boolean isLight = false;
			if (record.getServiceUuids() != null) {
				for (ParcelUuid uuid : record.getServiceUuids()) {
					Log.d("BLE", "---uuid:" + uuid.toString());
					if (AppCst.LIGHT_UUID.equals(uuid.toString())) {
						isLight = true;
					}
				}
			}
			if (!isLight) {
				return;
			}
			final Light light = LightManager.getInstance().makLightByDevice(device);

			if (light == null) {
				return;
			}
			light.connect(MainActivity.this, mHandler);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loadLightView(light);
				}
			});
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mScanning) {
			startScanLight(false);
		}
		LightManager.getInstance().release();
	}



}
