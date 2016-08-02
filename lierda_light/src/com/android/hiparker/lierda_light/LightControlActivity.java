package com.android.hiparker.lierda_light;

import java.util.ArrayList;
import java.util.List;

import com.android.hiparker.lierda_light.base.BaseTitleActivity;
import com.android.hiparker.lierda_light.dao.Groups;
import com.android.hiparker.lierda_light.pojo.Light;
import com.android.hiparker.lierda_light.pojo.LightManager;
import com.android.hiparker.lierda_light.view.LightControlView;
import com.android.hiparker.lierda_light.view.LightControlView.LightControlListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.greenrobot.event.EventBus;

public class LightControlActivity extends BaseTitleActivity implements OnSeekBarChangeListener {
	public static final int REQUEST_EDIT_GROUP = 1;
	
	private int controlType = 0; // 1:灯   2:灯组
	private Light mLight;
	private Groups mGroups;
	private List<Light> groupLights;
	
	private LightControlView mControlView;
	private SeekBar mSeekBar1, mSeekBar2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		String address = getIntent().getStringExtra("lightAddress");
		if (address != null) {
			controlType = 1;
			mLight = LightManager.getInstance().getLight(address);
			if (mLight == null) {
				finish();
				return;
			}
			if (mLight.getStatus() != Light.STATE_CONNECTED) {
				enableProgress(true);
			} else {
				initLightParam();
			}
		}
		mGroups = (Groups) getIntent().getSerializableExtra("group");
		if (mGroups != null) {
			controlType = 2;
			initGroup();
		}
		if (controlType < 1) {
			finish();
			return;
		}
		
		if (controlType == 1) {
			setTopTitle(mLight.name);
		} else {
			setTopTitle(mGroups.getName());
		}
		enableRightBtn("Edit", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (controlType == 1) {
					goEditLightName();
				} else {
					goEditGroup();
				}
			}
		});
		
		setContentView(R.layout.activity_light_control);
		mControlView = (LightControlView) findViewById(R.id.light_control_view);
		mControlView.setControlListener(new LightControlListener() {
			
			@Override
			public void onSwitchClick(int status) {
				controlLight();
			}
			
			@Override
			public void onProgressChange(int progress) {
				
			}
			
			@Override
			public void onEndProgressChange(int progress) {
				controlLight();
			}
			
			@Override
			public void onBeginProgressChange(int progress) {
				
			}
		});
		
		mSeekBar1 = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar2 = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar1.setOnSeekBarChangeListener(this);
		mSeekBar2.setOnSeekBarChangeListener(this);
	}
	
	private void initGroup() {
		groupLights = new ArrayList<Light>();
		for (Light light : LightManager.getInstance().getLights()) {
			if (light.getStatus() == Light.STATE_CONNECTED) {
				groupLights.add(light);
			}
		}
	}

	private void initLightParam() {
		mLight.readCharacteristic(new Light.OnValueReaded() {
			@Override
			public void onReaded(byte[] values) {
				boolean isOpen = (values[0]&0xFF) == 1;
				int lightness = (values[1]&0xFF) * 100 /0xFF;
				int colorTemp = (values[2]&0xFF) * 100/0xFF;
				Log.d("Activity", "lightness:" + lightness);
				Log.d("Activity", "colorTemp:" + colorTemp);
				mSeekBar1.setProgress(lightness);
				mSeekBar2.setProgress(colorTemp);
			}
		});
	}
	
	private void goEditLightName() {
		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.dialog_add_controller, null);
		final EditText exitName = (EditText) view
				.findViewById(R.id.controller_name_input);
		exitName.setText(mLight.name);
		
		final AlertDialog dialog  = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
				.setView(view)
				.create();

		Button negativeBtn = (Button) view
				.findViewById(R.id.negativeButton);
		negativeBtn.setText(R.string.cancel);
		negativeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button positiveBtn = (Button) view
				.findViewById(R.id.positiveButton);
		positiveBtn.setText(R.string.ok);
		positiveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				String name = exitName.getText().toString().trim();
				
			}
		});
		
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			public void onShow(DialogInterface dialog) {
				exitName.setSelection(0, exitName.getText().length());
				exitName.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.showSoftInput(exitName, 0);
			}
		});
		
		dialog.show();
	}
	
	private void goEditGroup() {
		Intent intent = new Intent();
		intent.putExtra("group", mGroups);
		startActivityForResult(intent, REQUEST_EDIT_GROUP);
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_EDIT_GROUP && resultCode == RESULT_OK) {
			mGroups = (Groups) data.getSerializableExtra("group");
			initGroup();
		}
	}

	public void onEventMainThread(Light light) {
		if (controlType == 1) {
			if (light == mLight && light.getStatus() == Light.STATE_CONNECTED) {
				enableProgress(false);
				initLightParam();
			}
		} else if (controlType == 2) {
			if (light.getStatus() == Light.STATE_CONNECTED && !groupLights.contains(light)) {
				groupLights.add(light);
				// refrashView
			}
			if (light.getStatus() == Light.STATE_DISCONNECTED && groupLights.contains(light)) {
				groupLights.remove(light);
				// refrashView
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == mSeekBar1) {
			
		} else if (seekBar == mSeekBar2) {
			
		}
		int lightness = mSeekBar1.getProgress();
		int colorTemperature = mSeekBar2.getProgress();
		if (controlType == 1) {
			mLight.wirteCharacteristic(1, lightness, colorTemperature);
		} else {
			for (Light light : groupLights) {
				light.wirteCharacteristic(0, lightness, colorTemperature);
			}
		}
	}
	
	private void controlLight() {
		int lightness = mSeekBar1.getProgress();
		int colorTemperature = mControlView.getProgress();
		int lightStatus = mControlView.getSwitchStatus();
		if (controlType == 1) {
			mLight.wirteCharacteristic(lightStatus, lightness, colorTemperature);
		} else {
			for (Light light : groupLights) {
				light.wirteCharacteristic(lightStatus, lightness, colorTemperature);
			}
		}
	}
}
