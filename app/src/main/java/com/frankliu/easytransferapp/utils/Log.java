package com.frankliu.easytransferapp.utils;

public class Log {
	
	public static void log(String tag, LogLevel level, String content) {
		System.out.println(Util.getTimeString() + "  " + tag + "  " + level + "  " + content);
	}

}
