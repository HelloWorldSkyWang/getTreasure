package com.king.mavenweb.treasure.controller;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.king.mavenweb.common.GlobalVariable;
import com.king.mavenweb.common.MemoryMap;
import com.king.mavenweb.common.jsSign.RandomUtils;
import com.king.mavenweb.common.jsSign.SHA1;
import com.king.mavenweb.common.jsSign.WxJsapiSignature;
import com.king.mavenweb.utils.HttpPoster;
import com.king.mavenweb.utils.wechat.WechatUtils;



/**
 * 微信综合Controller
 * @author 
 * @date 2017年5月28日 上午10:46:08
 */
@Controller
@RequestMapping("/wechat")
public class WeChatCoreController{
    
    private static Logger logger = LoggerFactory.getLogger(WeChatCoreController.class);
    
	@Autowired
	GlobalVariable globalVariable;

    /**
     * 域名认证绑定（ing）
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="getPreInfo",method=RequestMethod.GET)
    public String getPreInfo(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
    	logger.info("域名认证绑定--getPreInfo--开始");
    	
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        
        String echostr = request.getParameter("echostr");
        
        String calcSignature = SHA1.genWithAmple("token=king","timestamp="+timestamp,"nonce="+nonce);
        
        if (StringUtils.isNotBlank(echostr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            response.getWriter().println(echostr);
        }
        
        logger.info("域名认证绑定--getPreInfo--结束");
        return null;
    }
    
    @RequestMapping("/loginInfo")
	public @ResponseBody JSONObject loginInfo(String code) {
    	
    	JSONObject result = new JSONObject();
    	
    	String url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code"
    			.replace("APPID", globalVariable.getAppId()).replace("SECRET", globalVariable.getAppSecret()).replace("JSCODE", code);
    	String resultStr = HttpPoster.get(url);
    	if(resultStr!=null){
    		JSONObject loginInfo = JSON.parseObject(resultStr);
    		if(loginInfo.containsKey("openid")){
    			logger.info("成功获取会话秘钥");
    			//用户唯一标识
    			String openid = loginInfo.getString("openid");
    			//会话密钥
    			String session_key = loginInfo.getString("session_key");

    			//保存会话秘钥和openid对应关系
    			MemoryMap.sessionKeyMap.put(openid, session_key);
    			
    			result.put("result", "success");
    			return result;
    			
    		}
    	}
    	logger.error("获取会话秘钥失败");
    	result.put("result", "failure");
    	return result;
    }
    
    
    /**
     * 获取微信用户信息，并跳转到指定url
     * @param request
     * @return
     */
	@RequestMapping("/redirect")
	public String redirect(HttpServletRequest request) {
		logger.info("获取微信用户信息，并跳转到指定url--redirect--开始");
		
		/**
		 * 通过code获得用户信息
		 */
		//1、拿到code
		String code = request.getParameter("code");
		if(code!=null && !"".equals(code)){

	        //2、根据传入的code获取access_token和openid（这里通过code换取的是一个特殊的网页授权access_token，不同于基础支持中的access_token）
	        JSONObject openidToken = this.getOpenIdToken(code);
	        String openid = openidToken.getString("openid");
	        String access_token = openidToken.getString("access_token");
	        
	        if(openid==null||"".equals(openid)){
	        	return null;
	        }
	        
	        //3、通过openid和access_token获得用户基本信息
	        JSONObject json = null;
	        String state = request.getParameter("state");
	        if(state.equals("123")){
	        	json = this.getUserInfoAuthed(openid);
	        }else if(state.equals("456")){
	        	json = this.getUserInfoUnAuthed(openid,access_token);
	        }
	        
	        logger.info("获取微信用户信息，并跳转到指定url--redirect--结束结束");
	        return "redirect:" + globalVariable.getRedirectUrl()+"?openid="+openid;
		}
		logger.info("获取微信用户信息，并跳转到指定url--redirect--结束");
		return null;
    }
	
	/**
	 * 通过code获得openid和access_token
	 * @param code
	 * @return
	 * @Date 2017年5月24日
	 * @author lidong
	 */
	private JSONObject getOpenIdToken(String code){
		logger.info("通过code获得openid和access_token--getOpenIdToken--开始");
		//通过code获取openid和access_token
		String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
                .replace("APPID", globalVariable.getAppId()).replace("SECRET", globalVariable.getAppSecret()).replace("CODE", code);
		String jsr = HttpPoster.get(URL);
		JSONObject jsonObj = JSON.parseObject(jsr);
		logger.info("通过code获得openid和access_token--getOpenIdToken--结束");
		return jsonObj;
	}
	
	/**
	 * 获取已授权用户基本信息（snsapi_base）
	 * @param openid
	 * @return
	 * @Date 2017年5月24日
	 * @author lidong
	 */
	private JSONObject getUserInfoAuthed(String openid){
		//通过openId和access_token获取用户基本信息
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN"
        		.replace("ACCESS_TOKEN", GlobalVariable.access_token).replace("OPENID", openid);
        String info = HttpPoster.get(url);
        JSONObject jsonObj= JSON.parseObject(info);
		return jsonObj;
	}
	
	/**
	 * 获取未授权用户基本信息（snsapi_userinfo）
	 * @param openid
	 * @param access_token
	 * @return
	 * @Date 2017年5月24日
	 * @author lidong
	 */
	private JSONObject getUserInfoUnAuthed(String openid,String access_token){
		//通过openId和access_token获取用户基本信息
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN"
        		.replace("ACCESS_TOKEN", access_token).replace("OPENID", openid);
        String info = HttpPoster.get(url);
        JSONObject jsonObj= JSON.parseObject(info);
		return jsonObj;
	}

	/**
	 * 获取JsSignPackage
	 * @param requestStr
	 * @return
	 * @Date 2017年5月24日
	 * @author lidong
	 */
	@RequestMapping(value = "/getJsSignPackage", method = RequestMethod.POST)
	public @ResponseBody JSONObject getJsSignPackage(@RequestBody String requestStr) {
		logger.info("getJsSignPackage:requestStr="+requestStr);
		JSONObject jobj = JSON.parseObject(requestStr);
		String url = jobj.getString("url");
//		JsonResultView jsonResultView = new JsonResultView(false, "");
		JSONObject jsonobj = new JSONObject();
		Map<String, Object> jsonMsg = Maps.newHashMap();
		if (!StringUtils.isBlank(url)) {
			try {
				WxJsapiSignature wxJsapiSignature = createJsapiSignature(url);
				jsonMsg.put("appId", wxJsapiSignature.getAppId());
				jsonMsg.put("nonceStr", wxJsapiSignature.getNonceStr());
				jsonMsg.put("timestamp", wxJsapiSignature.getTimestamp());
				jsonMsg.put("url", wxJsapiSignature.getUrl());
				jsonMsg.put("signature", wxJsapiSignature.getSignature());
				jsonobj.put("success", true);
				jsonobj.put("msg", jsonMsg);

//				jsonResultView.setSuccess(true);
//				jsonResultView.setMsg(jsonMsg);
			} catch (Exception e) {

			}
		}
//		if (StringUtils.isEmpty(jsonpCallback)) {// json
//			return jsonResultView.getData();
//		}
//		return new JSONPObject(jsonpCallback, jsonResultView.getData());
		return jsonobj;
    }
	
	
	private WxJsapiSignature createJsapiSignature(String url) {
		long timestamp = System.currentTimeMillis() / 1000;
		String noncestr = RandomUtils.getRandomStr();
    	String jsapiTicket = GlobalVariable.jsapi_ticket;
		try {// signature 算法正确
			String signature = SHA1.genWithAmple("jsapi_ticket=" + jsapiTicket, "noncestr=" + noncestr,
					"timestamp=" + timestamp, "url=" + url);
			logger.info("createJsapiSignature:signature="+signature);
			WxJsapiSignature jsapiSignature = new WxJsapiSignature();
			jsapiSignature.setAppId(globalVariable.getAppId());
			jsapiSignature.setTimestamp(timestamp);
			jsapiSignature.setNonceStr(noncestr);
			jsapiSignature.setUrl(url);
			jsapiSignature.setSignature(signature);
			return jsapiSignature;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 手动刷新access_token和jsapi_ticket
	 * @return
	 */
	@RequestMapping(value = "/refresh")
	public @ResponseBody JSONObject refresh() {
		logger.info("手动--refresh--开始");
		
		String accessToken = null;
		String ticket = null;
		
		String appId = globalVariable.getAppId();
		String appSecret = globalVariable.getAppSecret();
		
		accessToken = WechatUtils.getBaseAccessToken(appId, appSecret);
		if(accessToken!=null&&!"".equals(accessToken)){
			GlobalVariable.access_token = accessToken;
			logger.info("获取access_token的结果："+accessToken);
			
			ticket = WechatUtils.getTicket(accessToken);
			if(ticket!=null&&!"".equals(ticket)){
				GlobalVariable.jsapi_ticket = ticket;
				logger.info("获取ticket的结果："+ticket);
				
				//返回监视结果
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("access_token", accessToken);
				jsonobj.put("ticket", ticket);
				logger.info("手动--refresh--结束");
				return jsonobj;
			}else{
				logger.info("获取ticket无效");
				return null;
			}
		}else{
			logger.info("获取access_token无效");
			return null;
		}
	}
	
	
	@RequestMapping(value = "/getUrl")
	public void getUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("获取最终授权链接url--开始");
		String appId = globalVariable.getAppId();
		String redirectUri = request.getParameter("reUrl");
		String url = WechatUtils.getAuthorizeUrl(appId, redirectUri, 2, "456");
		response.getWriter().println(url);
		logger.info("获取最终授权链接url--结束");
	}

    
}
