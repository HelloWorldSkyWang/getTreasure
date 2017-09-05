package com.king.mavenweb.common.jsSign;

import java.util.Map;

import com.google.common.collect.Maps;

public class JsonResultView {

	/**
	 * 可以做池化，防止产生过多的对象。
	 */
	private Map<String, Object> data;

	public JsonResultView(boolean success) {
		data = Maps.newHashMap();
		data.put("success", success);
	}

	public void setMsg(Object msg) {
		data.put("msg", msg);
	}

	public JsonResultView(boolean success, Object msg) {
		data = Maps.newHashMap();
		data.put("success", success);
		data.put("msg", msg);
	}

	public void setSuccess(Boolean result) {
		data.put("success", result);
	}

	public static Map<String, Object> ofSuccess(Object msg) {
		return new JsonResultView(true, msg).getData();
	}

	public static Map<String, Object> ofFailure(Object msg) {
		return new JsonResultView(false, msg).getData();
	}

	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * 使用setSuccess(),setMsg()来替换。
	 *
	 * @param data
	 * 
	 * @author zhangdy
	 * @date 2016年7月14日 下午3:27:29
	 */
	@Deprecated
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
