/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.funyoung.quickrepair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.push.Utils;
import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.model.User;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private SharedPreferences mPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(Preferences.NAME);
        addPreferencesFromResource(R.xml.preferences);

        mPreferences = getSharedPreferences(Preferences.NAME, MODE_PRIVATE);

        Preference prefNotification = findPreference(Preferences.KEY_ENABLE_NOTIFICATIONS);
        if (prefNotification != null) {
            prefNotification.setOnPreferenceChangeListener(this);
        }
    }

    /**
     * Starts the PreferencesActivity for the specified user.
     *
     * @param context The application's environment.
     */
    static void show(Context context) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static User getLoginUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Preferences.NAME, 0);
        long uid = prefs.getLong(Preferences.KEY_USER_ID, -1);
        if (uid > 0) {
            final String nickName = prefs.getString(Preferences.KEY_USER_NICKNAME, String.valueOf(uid));
            final String avatarUrl = prefs.getString(Preferences.KEY_USER_AVATAR, "");
            final String address = prefs.getString(Preferences.KEY_USER_ADDRESS, "");
            final String mobile = prefs.getString(Preferences.KEY_USER_MOBILE, "");
            User user = new User(uid, nickName, avatarUrl, address, mobile);
            return user;
        } else {
            return null;
        }
    }
    public static void setLoginUser(Context context, User user) {
        if (null != user) {
            SharedPreferences prefs = context.getSharedPreferences(Preferences.NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(Preferences.KEY_USER_ID, user.getUid());
            editor.putString(Preferences.KEY_USER_NICKNAME, user.getNickName());
            editor.putString(Preferences.KEY_USER_AVATAR, user.getAvatarUrl());
            editor.putString(Preferences.KEY_USER_ADDRESS, user.getAddress());
            editor.putString(Preferences.KEY_USER_MOBILE, user.getMobile());
            editor.commit();
        }
    }
    public static boolean hasLoginUser(Context context) {
        return null != getLoginUser(context);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        final String key = preference.getKey();
        if (key.equals(Preferences.KEY_ENABLE_NOTIFICATIONS)) {
            boolean checked = (Boolean)o;
            if (checked != mPreferences.getBoolean(Preferences.KEY_ENABLE_NOTIFICATIONS, true)) {
                ((CheckBoxPreference) (preference)).setChecked(checked);
                if (checked) {
                    // 以apikey的方式登录
                    PushManager.startWork(getApplicationContext(),
                            PushConstants.LOGIN_TYPE_API_KEY,
                            Utils.getMetaValue(SettingsActivity.this, "api_key"));
                } else {
                    PushManager.stopWork(getApplicationContext());
                }
            }
        }
        return false;
    }

    public static String getChannelIdsContent(Context context) {
        String appId = null;
        String channelId = null;
        String clientId = null;

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        appId = sp.getString("appid", "");
        channelId = sp.getString("channel_id", "");
        clientId = sp.getString("user_id", "");

        String content = "\tApp ID: " + appId + "\n\tChannel ID: " + channelId
                + "\n\tUser ID: " + clientId + "\n\t";

        return content;
    }
}
