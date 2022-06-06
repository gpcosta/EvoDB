package com.evo.jdbc;

public class Utils {
	
	public static int[] getIntArrayFromIntegerArray(Integer[] integers) {
		int[] res = new int[integers.length];
		for (int i = 0, len = integers.length; i < len; i++)
			res[i] = integers[i];
		return res;
	}
	
}
