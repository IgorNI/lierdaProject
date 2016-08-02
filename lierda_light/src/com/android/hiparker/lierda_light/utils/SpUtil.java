
package com.android.hiparker.lierda_light.utils;

import android.content.Context;

public class SpUtil {
	private static final String DEFAULT_CONTROL_TYPE_KEY     = "default_control_type_key";
	private static final String DEFAULT_CONTROL_ID_KEY       = "default_control_id_key";
	private static final String APP_VERSION_KEY              = "app_version_key";
	
	
	public static void setDefaultControlType(Context context, int type) {
		PrefUtil.putInt(context, DEFAULT_CONTROL_ID_KEY, type);
	}
	
	public static int getDefaultControlType(Context context) {
		return PrefUtil.getInt(context, DEFAULT_CONTROL_ID_KEY, 0);
	}
	
	public static void setDefaultControlId(Context context, String id) {
		PrefUtil.putString(context, DEFAULT_CONTROL_TYPE_KEY, id);
	}
	
	public static String getDefaultControlId(Context context) {
		return PrefUtil.getString(context, DEFAULT_CONTROL_TYPE_KEY, null);
	}
	

	/**
	 * 设置当前应用版本
	 * @param context
	 * @param version
	 */
	public static void setAppVersion(Context context, String version) {
		PrefUtil.putString(context, APP_VERSION_KEY, version);
	}
	
	/**
	 * 获取当前应用版本
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		return PrefUtil.getString(context, APP_VERSION_KEY, null);
	}
	
}
