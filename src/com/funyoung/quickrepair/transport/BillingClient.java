package com.funyoung.quickrepair.transport;

import android.content.Context;

import com.funyoung.quickrepair.api.ApiException;
import com.funyoung.quickrepair.api.CommonUtils;
import com.funyoung.quickrepair.model.Post;
import com.funyoung.quickrepair.model.User;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public final class BillingClient extends HttpRequestExecutor {
    public BillingClient(Context context, HttpClient httpClient){
        super(context, httpClient);
    }

    private static final String MODULE = "Bill";
    private final static class Method {
        private static final String CREATE_BILL = "createBill";
        private static final String LIST_BILL = "getBillList";
    }

    public  String createBill(Post post) throws IOException, ApiException {
        HttpRequestBase request = new HttpRequestBuilder(HttpRequestBuilder.POST,
                MODULE, Method.CREATE_BILL)
                .parameter(API_PARAM_USER_ID, String.valueOf(post.uid))
                .parameter(API_PARAM_CATEGORY, String.valueOf(post.category))
                .parameter(API_PARAM_SUB_CATEGORY, String.valueOf(post.subCategory))
                .parameter(API_PARAM_LATITUDE, String.valueOf(post.latitude))
                .parameter(API_PARAM_LONGITUDE, String.valueOf(post.longitude))
                .parameter(API_PARAM_DESCRIPTION, post.description)
                .parameter(API_PARAM_ADDRESS, post.address)
                .parameter(API_PARAM_AREA, post.area)
                .parameter(API_PARAM_BRAND, post.brand)
                .parameter(API_PARAM_CONTACT, post.contact)
                .parameter(API_PARAM_MODEL, post.model)
                .parameter(API_PARAM_MODEL_AGE, post.createYear)
                .create();
        return doRequest(request);
    }

    public  String listBill(User user, HashMap<String, String> filter) throws IOException, ApiException {
        HttpRequestBuilder builder = new HttpRequestBuilder(HttpRequestBuilder.POST,
                MODULE, Method.LIST_BILL);
        if (null != user) {
            builder.parameter(API_PARAM_USER_ID, String.valueOf(user.getUid()));
        }

        if (null != filter) {
            // todo: intercept filter info into builder.
            builder.parameter(filter);
        }
//                .parameter(API_PARAM_USER_ID, String.valueOf(post.uid))
//                .parameter(API_PARAM_CATEGORY, String.valueOf(post.category))
//                .parameter(API_PARAM_SUB_CATEGORY, String.valueOf(post.subCategory))
//                .parameter(API_PARAM_LATITUDE, String.valueOf(post.latitude))
//                .parameter(API_PARAM_LONGITUDE, String.valueOf(post.longitude))
//                .parameter(API_PARAM_DESCRIPTION, post.description)
//                .parameter(API_PARAM_ADDRESS, post.address)
//                .parameter(API_PARAM_AREA, post.area)
//                .parameter(API_PARAM_BRAND, post.brand)
//                .parameter(API_PARAM_CONTACT, post.contact)
//                .parameter(API_PARAM_MODEL, post.model)
//                .parameter(API_PARAM_MODEL_AGE, post.createYear)
//                .create();
        return doRequest(builder.create());
    }

    public static boolean createBill(Context context, final Post post) {
        BillingClient ac = new BillingClient(context, SimpleHttpClient.get());
        Exception exception = null;
        try {
            String result = ac.createBill(post);
            return CommonUtils.parseBooleanResult(result);
        } catch (ClientProtocolException e) {
            exception = e;
            e.printStackTrace();
        } catch (IOException e) {
            exception = e;
            e.printStackTrace();
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        } finally {
            postCheckApiException(context, exception);
        }
        return false;
    }

    public static ArrayList<Post> listBill(Context context, User user, HashMap<String, String> filter) {
        BillingClient ac = new BillingClient(context, SimpleHttpClient.get());
        Exception exception = null;
        try {
            String result = ac.listBill(user, filter);
            return Post.parseListResult(result);
        } catch (ClientProtocolException e) {
            exception = e;
            e.printStackTrace();
        } catch (IOException e) {
            exception = e;
            e.printStackTrace();
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        } finally {
            postCheckApiException(context, exception);
        }
        return null;
    }

}
