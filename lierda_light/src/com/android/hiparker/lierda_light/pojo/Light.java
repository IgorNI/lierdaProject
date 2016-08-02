package com.android.hiparker.lierda_light.pojo;

import java.util.List;

import com.android.hiparker.lierda_light.constant.AppCst;
import com.android.hiparker.lierda_light.utils.HexUtil;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import de.greenrobot.event.EventBus;

// 相当于BluetoothLeService
public class Light {
	public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
	
	public String name;
	public int mColor;
	public BluetoothDevice device;
	public BluetoothGatt mBluetoothGatt;
	private BluetoothGattService mGattService; 
	private BluetoothGattCharacteristic mGattCharacteristic;
	private OnValueReaded valueListener;
	private Handler mHandler;
	
	private int mStatus;
	
	Light() {
		
	}
	
	Light(BluetoothDevice dev) {
		device = dev;
		name = device.getName();
		mStatus = STATE_DISCONNECTED;
	}

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

	public boolean connect(Context context, Handler handler) {
		mHandler = handler;
		
        if (device == null) {
            Log.w("", "Device not found.  Unable to connect.");
            return false;
        }
        if (mBluetoothGatt != null) {
            Log.d("", "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        Log.e("BLE", "发起连接"+ device.getName());
        
        

    	
    	// Implements callback methods for GATT events that the app cares about. For
        // example,
        // connection change and services discovered.
        BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                    int newState) {
            	Log.e("BLE", "onConnectionStateChange");
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BLE", "Connected to GATT server.");
                    // Attempts to discover services after successful connection.
                    Log.i("BLE", "Attempting to start service discovery:"
                            + mBluetoothGatt.discoverServices());
                    Log.e("BLE", "连接成功 : discoverServices");
                    mHandler.sendEmptyMessage(AppCst.MESSAGE_START_SUCCESS);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                	mStatus = STATE_DISCONNECTED;
                	mBluetoothGatt.disconnect();
                	mBluetoothGatt.close();
                	mBluetoothGatt = null;
                	EventBus.getDefault().post(Light.this);
                    Log.e("BLE", "连接失败");
                    Log.i("", "Disconnected from GATT server.");
                    mHandler.sendEmptyMessage(AppCst.STATE_CONNECTFAILED);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            	Log.e("BLE","onServicesDiscovered-----  status:" + status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    displayGattServices(getSupportedGattServices());
                    mHandler.sendEmptyMessage(AppCst.MESSAGE_BLUETOOTH_INIT);
                } else {
                    Log.w("BLE", "onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                    BluetoothGattCharacteristic characteristic, int status) {
            	Log.e("BLE","onCharacteristicRead");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                	byte[] values = characteristic.getValue();
                	Log.d("BLE", "onCharacteristicRead:" + HexUtil.bytes2HexStr(values));
                	if (valueListener != null) {
                		valueListener.onReaded(values);
                	}
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt,
                    BluetoothGattDescriptor descriptor, int status) {

            	Log.e("BLE","onDescriptorWriteonDescriptorWrite = " + status
                        + ", descriptor =" + descriptor.getUuid().toString());
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                    BluetoothGattCharacteristic characteristic) {
                if (characteristic.getValue() != null) {
                	Log.e("BLE",characteristic.getStringValue(0));
                }
                Log.e("BLE","--------onCharacteristicChanged-----");
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

            }

            public void onCharacteristicWrite(BluetoothGatt gatt,
                    BluetoothGattCharacteristic characteristic, int status) {
            	Log.e("BLE","--------write success----- status:" + status);
            };
        };
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
        requestDiscoverServices();
        Log.d("", "Trying to create a new connection.");
        return true;
    }
	
	public void requestDiscoverServices() {
		if (mBluetoothGatt == null || mHandler == null) {
			Log.e("BLE", "requestDiscoverServices error");
			return;
		}
		Log.d("BLE", "request DiscoverServices");
		mBluetoothGatt.discoverServices();
        mStatus = STATE_CONNECTING;
        mHandler.postDelayed(connectRunable, 5000);
	}
	
	private Runnable connectRunable = new Runnable() {
		@Override
		public void run() {
			mStatus = STATE_DISCONNECTED;
			EventBus.getDefault().post(this);
		}
	};
	
	  
    public void wirteCharacteristic(int open, int lightness, int colorTemperature) {
        if (mBluetoothGatt == null || mGattCharacteristic == null) {
            Log.w("", "BluetoothAdapter not initialized");
            return;
        }
        byte[] values = new byte[]{(byte)(open), (byte)(0xff*lightness/100), (byte)(0xff*colorTemperature/100), 0x00, 0x00};
        Log.d("BLE", "wirteCharacteristic:    " + HexUtil.bytes2HexStr(values));
        mGattCharacteristic.setValue(values);
        mBluetoothGatt.writeCharacteristic(mGattCharacteristic);
    }
    
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     * 
     * @param listener
     *            The characteristic to read from.
     */
    public void readCharacteristic(OnValueReaded listener) {
        if (mBluetoothGatt == null) {
            Log.w("BLE", "BluetoothAdapter not initialized");
            return;
        }
        valueListener = listener;
        boolean result = mBluetoothGatt.readCharacteristic(mGattCharacteristic);
        Log.d("BLE", "readCharacteristic return:" + result);
    }
    
    
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.d("", "gattService UUID:" + uuid);
            if (uuid.startsWith("0000ff13")) {
            	mGattService = gattService;
            }

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                Log.d("", "gattCharacteristic UUID:" + uuid);
                
                if (uuid.startsWith("0000ff01")) {
                	mGattCharacteristic = gattCharacteristic;
                	mStatus = STATE_CONNECTED;
            		EventBus.getDefault().post(this);
            		mHandler.removeCallbacks(connectRunable);
                }
            }
        }
    }
    
    public interface OnValueReaded {
    	public void onReaded(byte[] values);
    }
    
    public int getStatus() {
    	return mStatus;
    }
}
