package com.funyoung.quickrepair.transport;

import android.content.Context;

import com.funyoung.quickrepair.api.ApiException;
import com.funyoung.quickrepair.api.CommonUtils;
import com.funyoung.quickrepair.model.User;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.File;
import java.io.IOException;

public final class PhotoClient extends HttpRequestExecutor {
    public PhotoClient(Context context, HttpClient httpClient){
        super(context, httpClient);
    }

    private static final String MODULE = "Photo";
    private final static class Method {
        private static final String UPLOAD_AVATAR = "uploadAvatar";
    }

    public  String uploadAvatar(User user, File file) throws IOException, ApiException {
        HttpRequestBase request = new HttpRequestBuilder(HttpRequestBuilder.POST,
                MODULE, Method.UPLOAD_AVATAR)
                .create();
        return doRequest(request);
    }

    public static boolean uploadAvatar(Context context, final User user, final File file) {
        PhotoClient ac = new PhotoClient(context, SimpleHttpClient.get());
        Exception exception = null;
        try {
            String result = ac.uploadAvatar(user, file);
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
}
