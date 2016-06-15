var exec = require('cordova/exec');

exports.pay = function (paymentInfo, successCallback, errorCallback) {   
		if(!paymentInfo){
			errorCallback && errorCallback("Please enter order information");  
		}else{
			exec(successCallback, errorCallback, "AliPay", "pay", [paymentInfo]);
		}
};

exports.receiveMessageInAndroidCallback = function(data) {
	console.log("alipay:receiveMessageInAndroidCallback");
	data = JSON.stringify(data);
	var bToObj = JSON.parse(data);
	cordova.fireDocumentEvent('alipay.receiveMessage', bToObj);
};
