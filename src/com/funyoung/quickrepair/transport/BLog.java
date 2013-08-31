/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.funyoung.quickrepair.transport;

public class BLog {
	private static String TAG = "NetworkTransport";
	private static boolean ACCOUNT_LOG_EABLED = true;
	
	public static void d(String msg){
	    if(ACCOUNT_LOG_EABLED){
	        Logger.logD(TAG, msg);
	    }
	}
	
	public static void d(String tag, String msg){
        if(ACCOUNT_LOG_EABLED){
            Logger.logD(tag, msg);
        }
    }
	
	public static void w(String msg){
	    if(ACCOUNT_LOG_EABLED){
	        Logger.logW(TAG, msg);
	    }
	}
	
	public static void e(String msg){
	    if(ACCOUNT_LOG_EABLED){
	        Logger.logE(TAG, msg);
	    }
	}
}
