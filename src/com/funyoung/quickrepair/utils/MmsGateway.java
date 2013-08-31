package com.funyoung.quickrepair.utils;

//import org.apache.http.client.HttpClient;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Mms Gateway from http://www.smschinese.cn/api.shtml
 * 1. Dependency jar
 * commons-logging-1.1.1.jar
 * commons-httpclient-3.1.jar
 * commons-codec-1.4.jar
 * 2. user name and api key
 * 3. named sender on website
 * Created by yangfeng on 13-7-13.
 */
public class MmsGateway {
    private static final String TAG = "MmsGateway";

    private static final String MMS_URL = "http://gbk.sms.webchinese.cn";
    private static final String CLIENT_ID = "全城维修";
    private static final String API_KEY = "7ccbd793670ce6d3f81a";


    private MmsGateway() {
        // no instance.
    }

    public static String sendWebchineseMsg(String phoneNumber, String verifyCode) throws Exception {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(MMS_URL);
        post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
        NameValuePair[] data ={ new NameValuePair("Uid", CLIENT_ID),
                new NameValuePair("Key", API_KEY),
                new NameValuePair("smsMob", phoneNumber),
                new NameValuePair("smsText", generateSmsText(phoneNumber, verifyCode))};
        post.setRequestBody(data);

        client.executeMethod(post);
        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:" + statusCode);
        for(Header h : headers)
        {
            System.out.println(h.toString());
        }
        String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
        System.out.println(result);

        post.releaseConnection();
        return result;
    }

    private static final String mmsTemplate = "你使用手机号%1$s申请加入【全城维修】，你的验证码为%2$s，如非本人申请，请忽略。";
    private static String generateSmsText(String phoneNumber, String verifyCode) {
        return String.format(mmsTemplate, phoneNumber, verifyCode);
    }
}
