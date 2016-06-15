## cordova-plugin-alipay ##

### 本cordova插件是基于支付宝支付的简单demo

###主要功能
		
 - 主要功能是：服务器把订单信息签名后，调用该插件调用支付宝sdk进行支付，支付完成后如支付成功，还要去服务端去验证是否真正支付
  
 - 虽然插件中我保留了在客户端签名调用支付的代码，但不建议更改使用 不建议更改使用 不建议更改使用 
	
###准备工作

 - 这里我默认环境已经安装完毕，只需要进行插件安装即可

###Cordova/Phonegap 安装 （现仅支持Android。ios版本正在开发）

   在线：cordova plugin add https://github.com/chenyuanchn/cordova-plugin-alipay.git
    
   本地：下载后再进行安装cordova plugin add 插件目录
   
### 支持平台

		Android only
		
### Android API

+ 插件 API
    		
1.js调用插件方法
```js
		//初始化
		//注册监听
	document.addEventListener("alipay.receiveMessage", function(event) {
     	var resultStatus = event.resultStatus;
     	//取到支付完成后的状态 resultStatus，如果订单支付成功，则去服务端去验证是否真正支付，防止客户端篡改伪造
     	//9000  订单支付成功 ;8000 正在处理中 ;4000  订单支付失败 ;6001  用户中途取消 ;6002 网络连接出错
    	}, false);  
    	
    //调用支付
     var payInfo  = "xxxx";//payInfo 是服务端签名生成的订单信息，具体请参考官网进行签名处理
     cordova.plugins.AliPay.pay(payInfo,function(e){},function(e){});
	
	
		  
```