package com.funyoung.quickrepair.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfeng on 13-7-13.
 */
public class TelephonyUtils {
    private TelephonyUtils() {
        // no instance
    }
    // todo: mms gate could not recognize +86 prefix.
    private static final int MOBILE_NUM_LEN = 11;

    public static List<String> queryAllMyMobile(Context context) {
        List<String> mobileNumbers = new ArrayList<String>();
        TelephonyManager phoneMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String number;
        number = phoneMgr.getLine1Number();
        if (!mobileNumbers.contains(number) &&
                !TextUtils.isEmpty(number) &&
                number.length() >= MOBILE_NUM_LEN) {
            mobileNumbers.add(trimMobileNumber(number, MOBILE_NUM_LEN));
        }
//        number = phoneMgr.getSimSerialNumber();
//        if (!mobileNumbers.contains(number) &&
//                !TextUtils.isEmpty(number) &&
//                number.length() >= MOBILE_NUM_LEN) {
//            mobileNumbers.add(trimMobileNumber(number, MOBILE_NUM_LEN));
//        }

//        number = phoneMgr.getVoiceMailNumber();
//        if (!mobileNumbers.contains(number) &&
//                !TextUtils.isEmpty(number) &&
//                number.length() >= MOBILE_NUM_LEN) {
//            mobileNumbers.add(trimMobileNumber(number, MOBILE_NUM_LEN));
//        }

        return mobileNumbers;
    }
    public static String queryMyMobile(Context context) {
//        return "18618481850";
        return TelephonyUtils.getPhoneNumber(context, MOBILE_NUM_LEN);
    }

    public static String getPhoneNumber(Context context, int len) {
        TelephonyManager phoneMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (len > 0) {
            return trimMobileNumber(phoneMgr.getLine1Number(), len);
        } else {
            return phoneMgr.getLine1Number();
        }
    }


    private static String trimMobileNumber(String mobile, int len) {
        if (TextUtils.isEmpty(mobile) || mobile.length() <= len) {
            return mobile;
        }

        final int offset = mobile.length() - len;
        return mobile.substring(offset);
    }

    public static boolean isMobileNumber(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        if (text.length() < 8) {
            return false;
        }

        /// todo : verify valid mobile number text
        return true;
    }

}
