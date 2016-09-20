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

import com.alipay.sdk.app.PayTask;

public class AliPay extends CordovaPlugin {

	private static final int SDK_PAY_FLAG = 1;
	private static String TAG = "AliPay";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(cordova.getActivity(), "支付成功",
							Toast.LENGTH_SHORT).show();
				} else {

					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(cordova.getActivity(), "支付结果确认中",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(cordova.getActivity(), "支付失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		PluginResult result = null;
		if ("pay".equals(action)) {

			 //订单信息在服务端签名后返回
			 final String payInfo = args.getString(0);

			 if (payInfo == null || payInfo.equals("") || payInfo.equals("null")) {
				callbackContext.error("Please enter order information");
			 	return true;
			 }

			cordova.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, " 构造PayTask 对象 ");
					PayTask alipay = new PayTask(cordova.getActivity());
					Log.i(TAG, " 调用支付接口，获取支付结果 ");
					String result = alipay.pay(payInfo, true);

					// 更新主ui的Toast
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);

					PayResult payResult = new PayResult(result);
					if (TextUtils.equals(payResult.getResultStatus(), "9000")) {
						Log.i(TAG, " 9000则代表支付成功，具体状态码代表含义可参考接口文档 ");
						callbackContext.success(payResult.toJson());
					} else {
						Log.i(TAG, " 为非9000则代表可能支付失败 ");
						if (TextUtils.equals(payResult.getResultStatus(),
								"8000")) {
							Log.i(TAG,
									" 8000代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态） ");
							callbackContext.success(payResult.toJson());
						} else {
							Log.i(TAG, " 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误 ");
							callbackContext.error(payResult.toJson());
						}
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
