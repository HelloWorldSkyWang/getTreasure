package com.king.mavenweb.common;

public class GlobalVariable {

	//开发者ID
	//密钥
	
//	public static String appId = "wx8b665ba22120882d";
//	public static String appSecret = "6c33e20aaab7e0d0c0e1e25e04062ae2";
	
//	public static String appId = "wxe0078db8091f603c";
//	public static String appSecret = "d4624c36b6795d1d99dcf0547af5443d";
	
//	public static String appId = "wx3c246f174b249cc6";
//	public static String appSecret = "cdd2d5221dec1fc8c9a7f83a8f2dc971";
	
//	public static String appId = "wxcefbcb5f88501a7f";
//	public static String appSecret = "7ffb486fa736005e680fe1496e5949db";
	
	
	//动态token
	public static String access_token = "";
	//签名需要
	public static String jsapi_ticket = "";
	
	//开发者ID
	private String appId;
	//密钥
	private String appSecret;
	//重定向url
	private String redirectUrl;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
