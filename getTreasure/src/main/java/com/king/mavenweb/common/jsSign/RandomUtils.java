package com.king.mavenweb.common.jsSign;

import java.util.Random;

public class RandomUtils {

	private static final String RANDOM_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final Random RANDOM = new java.util.Random();

	public static String getRandomStr() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			sb.append(RANDOM_STR.charAt(RANDOM.nextInt(RANDOM_STR.length())));
		}
		return sb.toString();
	}

}
