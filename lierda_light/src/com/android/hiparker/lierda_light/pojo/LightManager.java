package com.android.hiparker.lierda_light.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.bluetooth.BluetoothDevice;

public class LightManager {
	private static LightManager manager;
	private List<Light> lights;
	private List<Light> groupLights;
	private Map<String, Light> lightMap;
	private LightManager() {
		lights = new ArrayList<Light>();
		groupLights = new ArrayList<Light>();
		lightMap = new HashMap<String, Light>();
	}
	
	public static LightManager getInstance() {
		if (manager == null) {
			manager = new LightManager();
		}
		return manager;
	}
	
	public void addLight(Light light) {
		String key = light.device.getAddress();
		if (lightMap.containsKey(key)) {
			return;
		}
		lights.add(light);
		lightMap.put(key, light);
	}
	
	public Light getLight(String address) {
		return lightMap.get(address);
	}
	
	public Light makLightByDevice(BluetoothDevice device) {
		String key = device.getAddress();
		if (lightMap.containsKey(key)) {
			return null;
		} else {
			Light light = new Light(device);
			addLight(light);
			return light;
		}
	}

	public void release() {
		for (Light light : lights) {
			if (light.mBluetoothGatt == null) {
				continue;
			}
			light.mBluetoothGatt.disconnect();
			light.mBluetoothGatt.close();
			light.mBluetoothGatt = null;
		}
		lights.clear();
		lightMap.clear();
		manager = null;
	}
	
	public List<Light> getLights() {
		return lights;
	}

	public List<Light> getGroupLights() {
		return groupLights;
	}

}
