package com.himoo.ydsc.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 网络处理模块
 */
public class NetWorkUtils {
	/**
	 * 获取网络是否连接
	 * 
	 * @return 连接与否
	 */
	public static boolean getNetConnecState(Context context) {
		// 连接管理�?
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectMgr != null) {
			NetworkInfo[] info = connectMgr.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取网络连接类型
	 * 
	 * @return 连接网络的状�?WIFI表示无线网络;mobile表示手机网络)
	 */
	public static String getNetConnecType(Context context) {
		// 连接管理�?
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectMgr != null) {
			NetworkInfo[] info = connectMgr.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return info[i].getTypeName();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取ipV4地址
	 * 
	 * @return 地址
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {

						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Mac地址
	 * 
	 * @param context
	 *            上下�?
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {

		String macSerial = null;
		String str = "";
		InputStreamReader ir = null;
		LineNumberReader input = null;
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			ir = new InputStreamReader(pp.getInputStream());
			input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();
					// 去空�?;
				}
			}
		} catch (IOException ex) {
			// 赋予默认�?
			Log.e("msg", ex.getMessage() + "");
		} finally {
			try {
				if (ir != null)
					ir.close();
				if (input != null)
					input.close();
			} catch (IOException e) {
				Log.e("msg", e.getMessage() + "");
			}
		}
		if (macSerial == null) {// 若未能获取MAC，换取另�?��法获�?
			macSerial = getWifiMacAddress(context);
		}
		return macSerial;
	}

	public static String macAddress = null;

	/**
	 * 有WiFi获取MAC
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiMacAddress(Context context) {
		try {
			final WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (wifi == null)
				return null;

			WifiInfo info = wifi.getConnectionInfo();
			macAddress = info.getMacAddress();

			if (macAddress == null && !wifi.isWifiEnabled()) {
				new Thread() {
					@Override
					public void run() {
						wifi.setWifiEnabled(true);
						for (int i = 0; i < 10; i++) {
							WifiInfo _info = wifi.getConnectionInfo();
							macAddress = _info.getMacAddress();
							if (macAddress != null)
								break;
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						wifi.setWifiEnabled(false);
					}
				}.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return macAddress;
	}

	/**
	 * 获取设备�?
	 * 
	 * @param context
	 *            上下�?
	 * @return
	 */
	public static String getDiviceNo(Context context) {
		// TelephonyManager提供设备上获取�?讯服务信息的入口
		TelephonyManager telephonyManager = null;
		// 获取手机管理对象
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 设备标号
		String diviceNo = telephonyManager.getDeviceId();
		if (diviceNo == null || diviceNo.length() <= 0) {
			// 如果设备号获取不�?就获取安卓设备标�?
			diviceNo = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
		}
		return diviceNo;
	}

	public static boolean isNetConnected(Context context) {
		boolean flag = false;
		if (!isNetworkConnected(context)) {
			flag = false;
			return flag;
		}
		if (isWifiConnected(context) || isMobileConnected(context)) {
			flag = true;
		}
		return flag;
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 显示tost信息
	 * 
	 * @param msg
	 *            显示的信�?
	 */
	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
