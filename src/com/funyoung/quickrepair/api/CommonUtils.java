package com.funyoung.quickrepair.api;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by yangfeng on 13-8-7.
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";

    private static final String SIGN_SEPARATOR = "|";
    private static final String EMPTY_SIGN_STR = "";

    public static String md5Sign(List<String> list) {
        if (list.isEmpty()) {
            Log.e(TAG, "md5Sign with empty value list.");
            return EMPTY_SIGN_STR;
        }
        StringBuilder builder = new StringBuilder(list.get(0));
        final int size = list.size();
        for (int i = 1; i < size; i++) {
            builder.append(SIGN_SEPARATOR).append(list.get(i));
        }
        return md5Sign(builder.toString().getBytes());
    }

    public static String md5Sign(String module, String method, String key) {
        final StringBuilder builder = new StringBuilder();
        builder.append(module)
                .append(SIGN_SEPARATOR).append(method)
                .append(SIGN_SEPARATOR).append(key);
        return md5Sign(builder.toString().getBytes());
    }

    private static String md5Sign(byte[] rawData) {
        return MD5.toMd5(rawData);
    }

    private static String md5SignBase64(byte[] rawData) {
        return MD5.md5Base64(rawData);
    }

    public static boolean parseBooleanResult(String result) {
        Log.d(TAG, "parseBooleanResult, result = " + result);
        if (TextUtils.isEmpty(result)) {
            return false;
        }
        // todo:
        return true;
    }
}
