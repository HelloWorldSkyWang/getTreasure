package com.king.mavenweb.utils.wechat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.king.mavenweb.common.GlobalVariable;
import com.king.mavenweb.utils.HttpPoster;

public class WechatUtils {
	
	public final static String oauth2_authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public final static String oauth2_authorize_response_type = "code";
	public final static String oauth2_authorize_scope_base = "snsapi_base";
	public final static String oauth2_authorize_scope_userinfo = "snsapi_userinfo";
	public final static String oauth2_authorize_suffix = "#wechat_redirect";
	
	//https://open.weixin.qq.com/connect/oauth2/authorize
	//?appid=wxcefbcb5f88501a7f
	//&redirect_uri=http%3A%2F%2Fwxshop.epsit.cn%2Flongzhou%2Flongzhou%2FuserInfo
	//&response_type=code
	//&scope=snsapi_userinfo
	//&state=456
	//#wechat_redirect
	
	/**
	 * 生成授权跳转链接
	 * 	scope：1-snsapi_base，2-snsapi_userinfo
	 * @param appId
	 * @param redirectUri
	 * @param scope(1:snsapi_base,2:snsapi_userinfo)
	 * @param state
	 * @return
	 */
	
	public static String getAuthorizeUrl(String appId, String redirectUri, int scope, String state){

		//参数校验
		if(appId==null||"".equals(appId)
				||redirectUri==null||"".equals(redirectUri)
				||state==null||"".equals(state)){
			return "";
		}
		//scope校验
		String finalScope = "";
		if(scope==1){
			finalScope = oauth2_authorize_scope_base;
		}else if(scope==2){
			finalScope = oauth2_authorize_scope_userinfo;
		}else{
			return "";
		}
		
		//生成url
		StringBuffer sb = new StringBuffer();
		sb.append(oauth2_authorize_url);
		sb.append("?appid="+appId);
		try {
			sb.append("&redirect_uri="+URLEncoder.encode(redirectUri, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("&response_type="+oauth2_authorize_response_type);
		sb.append("&scope="+finalScope);
		sb.append("&state="+state);
		sb.append(oauth2_authorize_suffix);
		
		return sb.toString();
	}
	
	/**
	 * 通过appId和appSecret获取access_token
	 * @param appId
	 * @param appSecret
	 * @return
	 */
	public static String getBaseAccessToken(String appId, String appSecret){
		//通过appId和appSecret获取access_token
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET"
        		.replace("APPID", appId).replace("SECRET", appSecret);
        String result = HttpPoster.get(url);
        if(result!=null){
        	JSONObject jsonObj= JSON.parseObject(result);
        	return jsonObj.getString("access_token");
        }else{
        	return null;
        }
	}
	
	/**
	 * 通过access_token获取ticket
	 * @param accessToken
	 * @return
	 */
	public static String getTicket(String accessToken){
		//通过access_token获取ticket
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi"
				.replace("ACCESS_TOKEN", accessToken);
    	String result= HttpPoster.get(url);
        if(result!=null){
        	JSONObject jsonObj = JSON.parseObject(result);
        	return jsonObj.getString("ticket");
        }else{
    		return null;
    	}
	}

}
