package com.king.mavenweb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {
	
	public static String getRequestStr(HttpServletRequest request){
		StringBuffer sb = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			while((line=br.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}

}
