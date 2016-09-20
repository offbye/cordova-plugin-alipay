## cordova-plugin-alipay ##

Makes your Cordova application enable to use the [Alipay SDK](https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.hT44dE&treeId=54&articleId=104509&docType=1)
for mobile payment with Alipay App or Mobile Web. Requires cordova-android 4.0 or greater.

### ChangeLogs
  本cordova插件是基于支付宝App支付SDK的Demo实现
 - 升级支付宝SDK版本到20160825；
 - 修改了一些bug;
 - 支持Android和iOS Alipay SDK
###主要功能

 - 主要功能是：服务器把订单信息签名后，调用该插件调用支付宝sdk进行支付，支付完成后如支付成功，如果是9000状态，还要去服务端去验证是否真正支付

### Install 安装

The following directions are for cordova-cli (most people).  

* Open an existing cordova project, with cordova-android 4.0.0+, and using the latest CLI. TBS X5  variables can be configured as an option when installing the plugin
* Add this plugin

  ```sh
  cordova plugin add https://github.com/offbye/cordova-plugin-alipay.git --variable PARTNER_ID=[你的商户PID可以在账户中查询]
  ```
  （对于android，可以不传PARTNER_ID）

   offline：下载后再进行安装 `cordova plugin add  YOUR_DIR`

### 支持平台

		Android IOS

### Android API

* js调用插件方法

```js

    //第一步：订单在服务端签名生成订单信息，具体请参考官网进行签名处理
    var payInfo  = "xxxx";

    //第二步：调用支付插件        	
    cordova.plugins.AliPay.pay(payInfo,function success(e){},function error(e){});

	 //e.resultStatus  状态代码  e.result  本次操作返回的结果数据 e.memo 提示信息
	 //e.resultStatus  9000  订单支付成功 ;8000 正在处理中  调用function success
	 //e.resultStatus  4000  订单支付失败 ;6001  用户中途取消 ;6002 网络连接出错  调用function error
	 //当e.resultStatus为9000时，请去服务端验证支付结果
	 			/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */

```
