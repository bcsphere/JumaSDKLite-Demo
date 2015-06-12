package com.juma.bluetooth.sdklite;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import juma.sdk.lite.JumaDevice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;

public class JumaSDKLite extends CordovaPlugin {

	public static final String ACTION_START_SCAN = "com.juma.bluetooth.sdklite.ACTION_START_SCAN";
	public static final String ACTION_STOP_SCAN = "com.juma.bluetooth.sdklite.ACTION_STOP_SCAN";
	public static final String ACTION_DEVICE_DISCOVERED = "com.juma.bluetooth.sdklite.ACTION_DEVICE_DISCOVERED";
	public static final String ACTION_CONNECT = "com.juma.bluetooth.sdklite.ACTION_CONNECT";
	public static final String ACTION_CONNECTED = "com.juma.bluetooth.sdklite.ACTION_CONNECTED";
	public static final String ACTION_DISCONNECT = "com.juma.bluetooth.sdklite.ACTION_DISCONNECT";
	public static final String ACTION_DISCONNECTED = "com.juma.bluetooth.sdklite.ACTION_DISCONNECTED";
	public static final String ACTION_SEND_MESSAGE = "com.juma.bluetooth.sdklite.ACTION_SEND_MESSAGE";
	public static final String ACTION_RECEIVER_MESSAGE = "com.juma.bluetooth.sdklite.ACTION_RECEIVER_MESSAGE";
	public static final String ACTION_ERROR = "com.juma.bluetooth.sdklite.ACTION_ERROR";
	public static final String NAME_STR = "name";
	public static final String UUID_STR = "uuid";
	public static final String RSSI_STR = "rssi";
	public static final String MESSAGE_STR = "message";
	public static final String ERROR_STR = "error";
	public static final String CURRENT_DATE = "currentDate";

	private JumaDevice device;
	private Context context;

	private Map<String,CallbackContext> callbacks;
	
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		if(device == null){
			callbackContext.error("error");
			return false;
		}
		if(ACTION_START_SCAN.equals(action)){
			try {
				String name = args.getJSONObject(0).getString(NAME_STR);
				device.scan(name);
				callbackContext.success();
			} catch (RuntimeException e) {
				callbackContext.error(e.getMessage());
			}
		}else if(ACTION_DEVICE_DISCOVERED.equals(action)){
			callbacks.put(ACTION_DEVICE_DISCOVERED,callbackContext);
		}else if(ACTION_STOP_SCAN.equals(action)){
			try {
				device.stopScan();
				callbacks.put(ACTION_STOP_SCAN,callbackContext);
			} catch (RuntimeException e) {
				callbackContext.error(e.getMessage());
			}
		}else if(ACTION_CONNECT.equals(action)){
			try {
				String uuid = args.getJSONObject(0).getString(UUID_STR);
				device.connect(UUID.fromString(uuid));
				callbacks.put(ACTION_CONNECT,callbackContext);
			} catch (RuntimeException e) {
				callbackContext.error(e.getMessage());
			}
		}else if(ACTION_DISCONNECT.equals(action)){
			try {
				String uuid = args.getJSONObject(0).getString(UUID_STR);
				device.disconnect(UUID.fromString(uuid));
				callbacks.put(ACTION_DISCONNECT,callbackContext);
			} catch (RuntimeException e) {
				callbackContext.error(e.getMessage());
			}
		}else if(ACTION_SEND_MESSAGE.equals(action)){
			try {
				String message = args.getJSONObject(0).getString(MESSAGE_STR);
				device.send(hexToByte(message));
				callbackContext.success();
			} catch (RuntimeException e) {
				callbackContext.error(e.getMessage());
			}
		}else if(ACTION_RECEIVER_MESSAGE.equals(action)){
			callbacks.put(ACTION_SEND_MESSAGE,callbackContext);
		}else if(ACTION_ERROR.equals(action)){
			callbacks.put(ACTION_ERROR,callbackContext);
		}
		return true;
	}

	@Override
	public void initialize(final CordovaInterface cordova,CordovaWebView webView) {
		super.initialize(cordova, webView);
		context = cordova.getActivity();

		device = new JumaDevice() {

			@Override
			public void onScanStop() {
				CallbackContext callback = null;
				if((callback = callbacks.get(ACTION_STOP_SCAN)) != null){
					callback.success();
				}
			}

			@Override
			public void onMessage(byte[] message) {
				String currentDate = getCurrentData(context);
				
				JSONObject jsonObject = new JSONObject();
				addProperty(jsonObject,MESSAGE_STR, byteToHex(message));
				addProperty(jsonObject,CURRENT_DATE, currentDate);
				
				CallbackContext callback = null;
				if((callback = callbacks.get(ACTION_SEND_MESSAGE)) != null){
					PluginResult pr = new PluginResult(Status.OK,jsonObject);
					pr.setKeepCallback(true);
					callback.sendPluginResult(pr);
				}
			}

			@Override
			public void onDiscover(UUID uuid, String name, int rssi) {
				JSONObject jsonObject = new JSONObject();
				addProperty(jsonObject,UUID_STR, uuid);
				addProperty(jsonObject,NAME_STR, name);
				addProperty(jsonObject,RSSI_STR, rssi);
				
				CallbackContext callback = null;
				if((callback = callbacks.get(ACTION_DEVICE_DISCOVERED)) != null){
					PluginResult pluginResult = new PluginResult(Status.OK,jsonObject);
					pluginResult.setKeepCallback(true);
					callback.sendPluginResult(pluginResult);
				}
			}

			@Override
			public void onDisconnect(UUID uuid, String name) {
				String currentDate = getCurrentData(context);

				JSONObject jsonObject = new JSONObject();
				addProperty(jsonObject,UUID_STR, uuid);
				addProperty(jsonObject,NAME_STR, name);
				addProperty(jsonObject,CURRENT_DATE, currentDate);
				
				CallbackContext callback = null;
				if((callback = callbacks.get(ACTION_DISCONNECT)) != null){
					callback.success(jsonObject);
				}
			}

			@Override
			public void onConnect(UUID uuid, String name) {
				String currentDate = getCurrentData(context);
				JSONObject jsonObject = new JSONObject();
				addProperty(jsonObject,UUID_STR, uuid);
				addProperty(jsonObject,NAME_STR, name);
				addProperty(jsonObject,CURRENT_DATE, currentDate);
				
				CallbackContext callback = null;
				if((callback = callbacks.get(ACTION_CONNECT)) != null){
					callback.success(jsonObject);
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				String currentDate = getCurrentData(context);
				
				JSONObject jsonObject = new JSONObject();
				addProperty(jsonObject,ERROR_STR, e.getMessage());
				addProperty(jsonObject,CURRENT_DATE, currentDate);
				
				CallbackContext callback = null;
				if((callback = callbacks.get(ACTION_ERROR)) != null){
					PluginResult pluginResult = new PluginResult(Status.OK,jsonObject);
					pluginResult.setKeepCallback(true);
					callback.sendPluginResult(pluginResult);
				}
			}
			
		};
		
		device.init(context);
		if(callbacks == null){
			callbacks = new HashMap<String, CallbackContext>();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private static String getCurrentData(Context context) {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(new java.util.Date());
	}

	@SuppressLint("DefaultLocale")
	public static String byteToHex(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	@SuppressLint("UseValueOf")
	public static final byte[] hexToByte(String hex) throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] arr = hex.toCharArray();
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			String swap = "" + arr[i++] + arr[i];
			int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = new Integer(byteint).byteValue();
		}
		return b;
	}
	
	private void addProperty(JSONObject jsonObject,String key,Object value){
		try {
			jsonObject.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
