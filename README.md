## cordova-plugin-alipay ##

Makes your Cordova application enable to use the [Alipay SDK](https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.hT44dE&treeId=54&articleId=104509&docType=1)
for mobile payment with Alipay App or Mobile Web. Requires cordova-android 4.0 or greater.

### ChangeLogs
  本cordova插件是基于支付宝App支付SDK的Demo实现
  this cordova plugin is based on alipay sdk demo
 - 升级支付宝SDK版本到20170510；
 - updated to alipay sdk 20170510;
 - 修改了一些bug;
 - fixed some bug;
 - 支持Android和iOS Alipay SDK
 - support android and iOS alipay SDK
 - 支持支付宝android版沙箱
 - android version support alipay sandbox
 
### Notice 注意事项

- android版插件，注意使用沙箱调试时，order info中subject，body都不能为空，金额不要用0.01（支付宝的大坑），可以用个10块钱，订单号可以加个自己的任意前缀如(haha20170519020202012)。不要直接用demo里的时间戳+随机数,否则可能会碰到“无法获取订单信息”等错误（在网页支付是可以的，app支付不行。另外一个大坑）
- android plugin,when you're testing with sanbox, subject,body fields of order info should not be null, amount should not be 0.01 rmd, use 10(app pay bug of alipay sandbox), append a suffix (whatever you like e.g. hello20170909090909 )to order number. Don't use timestamp+random number(works with web pay, not app pay, this is a bug of alipay sandbox I guess), otherwise you might get "can not get order info" error

### Main feature 主要功能

 - 主要功能是：服务器把订单信息签名后，调用该插件调用支付宝sdk进行支付，支付完成后如支付成功，如果是9000状态，还要去服务端去验证是否真正支付
 - Main feature: invoke alipay plugin sdk to pay with order info generated from backend. if the result is 9000, your serverend still need to check with alipay server.

### Install 安装

The following directions are for cordova-cli (most people).  

* Open an existing cordova project, with cordova-android 4.0.0+, and using the latest CLI. TBS X5  variables can be configured as an option when installing the plugin
* Add this plugin

  ```sh
  cordova plugin add https://github.com/offbye/cordova-plugin-alipay.git --variable PARTNER_ID=[你的商户PID可以在账户中查询]
  ```
  （对于android，可以不传PARTNER_ID）

   离线安装：下载后再进行安装 `cordova plugin add  YOUR_DIR`
   offline：download and install via `cordova plugin add  YOUR_DIR`

### support platforms 支持平台

		Android IOS

### Android API

* js调用插件方法

```js

    //第一步：订单在服务端签名生成订单信息，具体请参考官网进行签名处理
	//1st step, generate order info in server-end, get it via ajax or whatever.
    var payInfo  = "xxxx";

    //第二步：调用支付插件, isSandBox : 是否使用沙箱，默认不使用
	//2rd step: invoke plugin, param isSandBox: using alipay sandbox or not , default false
    cordova.plugins.AliPay.pay(payInfo,function success(e){},function error(e){}, isSandBox);

	 //e.resultStatus  状态代码  e.result  本次操作返回的结果数据 e.memo 提示信息
	 //e.resultStatus  9000  订单支付成功(success) ;8000(still in processing) 正在处理中  调用function success
	 //e.resultStatus  4000  订单支付失败(failed) ;6001(user canceled)  用户中途取消 ;6002(connection error) 网络连接出错  调用function error
	 //当e.resultStatus为9000时，请去服务端验证支付结果(though 9000 means ok, you might still need to check with alipay through backend)
	/**
	 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&docType=1)
	 * 建议商户依赖异步通知
	 */

```
