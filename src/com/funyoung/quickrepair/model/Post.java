package com.funyoung.quickrepair.model;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangfeng on 13-8-18.
 * "uid":"XXX" //当前登录用户的uid
 category: xxx  // 大分类id
 sub_category:xxx // 小分类id
 description：”XXX” // 描述信息
 “latitude”:”XXXX” //维度
 “longitude”XXXX” //经度
 address：详细地址
 area：区域
 brand:品牌
 version:型号
 createyear:年份
 photo[]: “XXX” // 图片URL列表， 图片文件需先调接口生成URL
 */
public class Post {
    private static final String TAG = "Post";
    public long uid;
    public int category;
    public int subCategory;
    public long latitude;
    public long longitude;

    public String description;
    public String address;
    public String area;
    public String brand;
    public String contact;
    public String mobile;

    public String model;
    public String createYear;
    public String[] photos;

    // remote attribute
    public long createTime;
    public long updateTime;

    public static ArrayList<Post> parseListResult(String result) {
        ArrayList<Post> postList = new ArrayList<Post>();
        if (TextUtils.isEmpty(result)) {
            return postList;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray posts = jsonObject.optJSONArray("posts");
            if (null == posts || posts.length() == 0) {
                Log.i(TAG, "parseListResult, got empty list");
            } else {
                final int len = posts.length();
                JSONObject obj;
                for (int i = 0; i < len; i++) {
                    obj = posts.getJSONObject(i);
                    postList.add(fromJson(obj));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postList;
    }

    private static Post fromJson(JSONObject obj) {
        Post post = new Post();
        if (null != obj) {
            post.uid = obj.optLong("uid");
            post.category = obj.optInt("categoryid");
            post.subCategory = obj.optInt("sub_categoryid");
            post.description = obj.optString("description");
        }
        return post;
    }
}
