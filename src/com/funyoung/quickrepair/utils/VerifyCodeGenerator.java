package com.funyoung.quickrepair.utils;

/**
 * Created by yangfeng on 13-7-13.
 */
public class VerifyCodeGenerator {
    private VerifyCodeGenerator() {
        // no instance
    }

    protected static String getDigitalSeries(int count) {
        final double offset = Math.pow(10, count);
        final double seed = offset * Math.random();
        final int code = (int)seed;
        StringBuffer codeBuf = new StringBuffer(code);
        final int append = count - codeBuf.length();
        for (int i = 0; i < append; i++) {
            codeBuf.append(i);
        }
        return codeBuf.toString();
    }
}
