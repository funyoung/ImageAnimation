package com.funyoung.quickrepair.model;

import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfeng on 13-8-11.
 */
public class User {
    public static final String KEY_UID = "uid";
    public static final String KEY_NAME = "name";
    public static final String KEY_AVATAR = "header";

    public static final String KEY_GENDER = "sex";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_CATEGORY = "master_category";
    public static final String KEY_RANK = "rank";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_MOBILE = "mobile";

    public static final int USER_TYPE_DEFAULT = 0;
    public static final int USER_TYPE_PROVIDER = 1;

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_UNKNOWN = 2;

    public boolean isProviderType() {
        return mProfileExtra != null && mProfileExtra.type == USER_TYPE_PROVIDER;
    }

    public static class ProfileDetail {
        public int type;
        public int sex;
        public String address;
        public long latitude;
        public long longitude;
        public CategoryList mMasterList;
        public Rank mRank;
        public String mMobile;
    }

    private long mUid;
    private String mNickName;
    private String mAvatarUrl;

    private ProfileDetail mProfileExtra;

    public User(long uid, String nickName, String avatarUrl, String address, String mobile) {
        mUid = uid;
        mNickName = nickName;
        mAvatarUrl = avatarUrl;
        mProfileExtra = new ProfileDetail();
        mProfileExtra.address = address;
        mProfileExtra.mMobile = mobile;
    }

    public long getUid() {
        return mUid;
    }

    public String getNickName() {
        if (TextUtils.isEmpty(mNickName)) {
            return String.valueOf(mUid);
        }
        return mNickName;
    }

    public String getAvatarUrl() {
        if (null == mAvatarUrl) {
            return "";
        }
        return mAvatarUrl;
    }

    public int getGender() {
        return null == mProfileExtra ? 0 : mProfileExtra.sex;
    }

    public String getAddress() {
        return null == mProfileExtra ? "" : mProfileExtra.address;
    }


    public String getMobile() {
        return null == mProfileExtra ? "" : mProfileExtra.mMobile;
    }

    public static User parseFromJson(String jstr) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(jstr);
            if (null == jsonObject) {
                throw new Exception("Invalid json to convert as User " + jstr);
            }
            long uid = jsonObject.optLong(KEY_UID);
            if (uid > 0) {
                String nickName= jsonObject.optString(KEY_NAME);
                String avatarUrl = jsonObject.optString(KEY_AVATAR);
                String address = jsonObject.optString(KEY_ADDRESS);
                String mobile = jsonObject.optString(KEY_USER_MOBILE);
                return new User(uid, nickName, avatarUrl, address, mobile);
            } else {
                throw new Exception("Invalid uid in " + jstr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public static User parseProfileFromJson(String jstr) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(jstr);
            if (null == jsonObject) {
                throw new Exception("Invalid json to convert as User " + jstr);
            }
            jsonObject = jsonObject.getJSONObject("member");
            if (null == jsonObject) {
                throw new Exception("Invalid json to without member key  " + jstr);
            }
            long uid = Long.parseLong(jsonObject.optString(KEY_UID));
            if (uid > 0) {
                String nickName= jsonObject.optString(KEY_NAME);
                String avatarUrl = jsonObject.optString(KEY_AVATAR);
                String address = jsonObject.optString(KEY_ADDRESS);
                String mobile = jsonObject.optString(KEY_USER_MOBILE);
                User user = new User(uid, nickName, avatarUrl, address, mobile);

                if (null == user.mProfileExtra) {
                    user.mProfileExtra =new ProfileDetail();
                }

                ProfileDetail detail = user.mProfileExtra;

                detail.latitude = jsonObject.optLong(KEY_LATITUDE);
                detail.longitude = jsonObject.optLong(KEY_LONGITUDE);
                detail.sex = jsonObject.optInt(KEY_GENDER);
                detail.mMasterList = CategoryList.parseListFromJson(jsonObject.optString(KEY_CATEGORY));
                detail.mRank = Rank.parseFromJson(jsonObject.optString(KEY_RANK));
                detail.type = jsonObject.optInt(KEY_USER_TYPE);
                return user;
            } else {
                throw new Exception("Invalid uid in " + jstr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_UID, mUid);
        bundle.putString(KEY_NAME, mNickName);
        bundle.putString(KEY_AVATAR, mAvatarUrl);
        return bundle;
    }
}
