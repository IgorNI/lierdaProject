package com.android.hiparker.lierda_light.utils;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class AppUtil {
	private static final String PARAMETERS_SEPARATOR   = "&";
	private static final String EQUAL_SIGN             = "=";
	private static final int BLACK 					   = 0xff000000;
	private static final int WHITE 					   = 0xffffffff;
	
	/**
	 * 获取当前时间，格式为yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String getTime() {
		Date today=new Date();
		SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(today);
	}
	
	/**
	 * 获取当前运行界面的包名
	 * @param context
	 * @return
	 */
	public static String getTopPackageName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
		return cn.getPackageName();
	}

	/**
	 * 显示Toast
	 * @param context
	 * @param resId
	 */
	public static void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 显示Toast
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获取当前应用的versionName
	 * @param context
	 * @return
	 */
	public static final String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 获取当前运行的进程名
	 * @param context
	 * @return
	 */
	public static String getProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		
		return null;
	}
	

	/**
	 * 获取url所带的文件名
	 * @param url
	 * @return
	 */
	public static String urlToName(String url) {
		String name = url.substring(url.lastIndexOf(File.separator) + 1);
		int index = name.indexOf("?");
		if (index > -1) {
			name = name.substring(0, index);
		}
		name = name.replace("\\", "_")
				.replace("/", "_")
				.replace(":", "_")
				.replace("*", "_")
				.replace("?", "_")
				.replace("\"", "_")
				.replace("<", "_")
				.replace(">", "_")
				.replace("|", "_");
		
		return name;
	}
	
	/**
	 * 获取url所带的文件名
	 * @param url
	 * @param suffix
	 * @return
	 */
	public static String urlToName(String url, String suffix) {
		String name = url.substring(url.lastIndexOf(File.separator) + 1)
				.replace("\\", "_")
				.replace("/", "_")
				.replace(":", "_")
				.replace("*", "_")
				.replace("?", "_")
				.replace("\"", "_")
				.replace("<", "_")
				.replace(">", "_")
				.replace("|", "_")+ suffix;
		return name;
	}
}
