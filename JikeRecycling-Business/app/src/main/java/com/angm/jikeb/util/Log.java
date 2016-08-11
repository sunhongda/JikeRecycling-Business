package com.angm.jikeb.util;

public class Log {

	private final static String LOGTAG = "Log";
	private static final boolean LOGE = true;
	private static final boolean LOGW = true;
	private static final boolean isEngProject = true;
	private static boolean isLogerRunning = false; 

	public static void setLogerRunningStatus(boolean status) {
		isLogerRunning = status;
	}

	public static void v(String msg) {
		
		if(isEngProject || isLogerRunning) {
			android.util.Log.v(LOGTAG, msg);
		}
    }
	
	public static void d(String msg) {
		if(isEngProject || isLogerRunning) {
			android.util.Log.d(LOGTAG, msg);
		}
	}
	
	public static void a(String msg) {
		if(LOGE){
			android.util.Log.e("KD", msg);
		}
	}

    public static void i(String msg) {
    	if(isEngProject || isLogerRunning) {
    		android.util.Log.i(LOGTAG, msg);
		}
    }

	public static void i(String tag, String msg, Object... args)
	{
		if (isEngProject || isLogerRunning)
		{
			android.util.Log.i(tag, String.format(msg, args));
		}
	}

    public static void e(String msg) {
    	if(LOGE){
    		android.util.Log.e(LOGTAG, msg);
    	}
    }

	public static void e(String tag, String msg, Object... args)
	{
		if (LOGE)
		{
			android.util.Log.e(tag, String.format(msg, args));
		}
	}

    public static void e(String msg, Exception ex) {
    	if(LOGE){
    		android.util.Log.e(LOGTAG, msg, ex);
    	}
    }

    public static void wtf(String msg) {
    	if(LOGW){
    		android.util.Log.wtf(LOGTAG, msg);
    	}
    }

	public static void w(String tag, String msg, Object... args)
	{
		if (LOGW)
		{
			android.util.Log.w(tag, String.format(msg, args));
		}
	}


}
