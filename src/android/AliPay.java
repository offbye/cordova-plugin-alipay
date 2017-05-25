package com.offbye.cordova.alipay;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.util.Map;

import com.alipay.sdk.app.EnvUtils;

import com.alipay.sdk.app.PayTask;

public class AliPay extends CordovaPlugin {

	private static final int SDK_PAY_FLAG = 1;
	private static String TAG = "AliPay";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				String resultStatus = (String) msg.obj;
				String toast = "支付失败";
				if (TextUtils.equals(resultStatus, "9000")) {
					toast = "支付成功";
				} else if (TextUtils.equals(resultStatus, "8000")) {
					toast = "支付结果确认中";
				}
				Toast.makeText(cordova.getActivity(), toast , Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		PluginResult result = null;
		if ("pay".equals(action)) {
			 //订单信息在服务端签名后返回
			 final String payInfo = args.getString(0);
			 final Boolean isSandbox = args.getBoolean(1);

			 if (payInfo == null || payInfo.equals("") || payInfo.equals("null")) {
				callbackContext.error("Please enter order information");
			 	return true;
			 }

			cordova.getThreadPool().execute(new Runnable() {
		        public void run() {

					Log.i(TAG, " 构造PayTask 对象 ");

					if(isSandbox) {
						Log.i(TAG, " 使用沙箱 ");
						EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
					}
					PayTask alipay = new PayTask(cordova.getActivity());
					Log.i(TAG, " 调用支付接口，获取支付结果 ");

					Map<String,String> res = alipay.payV2(payInfo, true);
					String resultStatus = res.get("resultStatus");
					String result = res.get("result");
					String memo = res.get("memo");

					// 更新主ui的Toast
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = resultStatus;
					mHandler.sendMessage(msg);

					if ("9000".equals(resultStatus) || "8000".equals(resultStatus)) {
						// 8000：等待结果确认。要在success的callback中server端确认
						callbackContext.success(result);
					} else {
						callbackContext.error(memo);
					}
				}
			});
			return true;
		} else {
			callbackContext.error("no such method:" + action);
			return false;
		}
	}

}
