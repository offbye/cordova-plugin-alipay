## cordova-plugin-alipay ##

### 本cordova插件是基于支付宝支付的简单demo

###主要功能
		
 - 主要功能是：服务器把订单信息签名后，调用该插件调用支付宝sdk进行支付，支付完成后如支付成功，还要去服务端去验证是否真正支付
	
###准备工作

 - 这里我默认环境已经安装完毕，只需要进行插件安装即可

###Cordova/Phonegap 安装 

   在线：cordova plugin add https://github.com/chenyuanchn/cordova-plugin-alipay.git --variable PARTNER_ID=[你的商户PID可以在账户中查询]
   （对于android系统，可以不传PARTNER_ID）
    
   本地：下载后再进行安装cordova plugin add 插件目录
   
### 支持平台

		Android IOS
		
### Android API

+ 插件 API
    		
1.js调用插件方法
```js
				
    	
    //第一步：订单在服务端签名生成订单信息，具体请参考官网进行签名处理
     var payInfo  = "xxxx"
;
            	
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