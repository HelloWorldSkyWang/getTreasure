package com.king.mavenweb.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameUtils {
	
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

	public static boolean isFileName(){
		return true;
	}
	
	
	public static String generateFileName(){
		return generateFileName(null);
	}
	
	public static String generateFileName(String prefix){
		if(prefix == null || "".equalsIgnoreCase(prefix)) prefix = "file_";
		return new String(prefix + sdf.format(new Date()));
	}
}
