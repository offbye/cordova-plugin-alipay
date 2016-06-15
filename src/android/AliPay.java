package com.cordova.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class AliPay extends CordovaPlugin {

	private static final int SDK_PAY_FLAG = 1;
	private static String TAG = "AliPay";

	// begin 订单信息在客户端签名 不推荐使用！不推荐使用！不推荐使用！	
	// 商户PID
	public static final String PARTNER = "";
	// 商户收款账号
	public static final String SELLER = "";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";
	//end
	
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
					Log.i(TAG, " 9000则代表支付成功，具体状态码代表含义可参考接口文档 ");
					Toast.makeText(cordova.getActivity(), "支付成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Log.i(TAG, " 为非9000则代表可能支付失败 ");
					if (TextUtils.equals(resultStatus, "8000")) {
						Log.i(TAG,
								" 8000代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态） ");
						Toast.makeText(cordova.getActivity(), "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						Log.i(TAG, " 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误 ");
						Toast.makeText(cordova.getActivity(), "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				receiveMessageInAndroidCallback(resultStatus);
				break;
			}
			default:
				break;
			}
		}

		private void receiveMessageInAndroidCallback(String resultStatus) {
			String format = "cordova.plugins.AliPay.receiveMessageInAndroidCallback(%s);";
			JSONObject jExtras = new JSONObject();
			try {
				jExtras.put("resultStatus", resultStatus);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String js = String.format(format, jExtras);
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:" + js);
				}
			});

		};
	};

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		PluginResult result = null;
		if ("pay".equals(action)) {
				
			//订单信息在服务端签名后返回
			  final String payInfo = args.getString(0);
			  
			  if (payInfo == null || payInfo.equals("") ||
			  payInfo.equals("null")) { result = new
			  PluginResult(PluginResult.Status.ERROR,
			  "Please enter order information");
			  callbackContext.sendPluginResult(result); return false; }
			 
			// 订单信息在客户端签名 不推荐使用！不推荐使用！不推荐使用！			  
//			final String payInfo = getTestPayInfo();

			cordova.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, " 构造PayTask 对象 ");
					PayTask alipay = new PayTask(cordova.getActivity());
					Log.i(TAG, " 调用支付接口，获取支付结果 ");
					String result = alipay.pay(payInfo, true);
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);
					// PayResult payResult = new PayResult(result);
					// if (TextUtils.equals(payResult.getResultStatus(),
					// "9000")) {
					// callbackContext.success(payResult.toJson());
					// } else {
					// // 判断resultStatus 为非“9000”则代表可能支付失败
					// //
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					// if (TextUtils.equals(payResult.getResultStatus(),
					// "8000")) {
					// callbackContext.success(payResult.toJson());
					// } else {
					// callbackContext.error(payResult.toJson());
					// }
					// }
				}
			});

			result = new PluginResult(PluginResult.Status.OK, "pay");
			callbackContext.sendPluginResult(result);
			return true;
		} else {
			result = new PluginResult(PluginResult.Status.ERROR,
					"no such method:" + action);
			callbackContext.sendPluginResult(result);
			return false;
		}
	}

	private String getTestPayInfo() {
		String orderInfo = getOrderInfo("小米max", "小米历史上最好用的大屏手机", "0.01");
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();
		return payInfo;
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
