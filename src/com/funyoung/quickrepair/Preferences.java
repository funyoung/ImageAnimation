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

import com.funyoung.quickrepair.model.User;

final class Preferences {
    static final String NAME = "QuickRepair";

    static final String KEY_ENABLE_NOTIFICATIONS = "photostream.enable-notifications";
    static final String KEY_VIBRATE = "photostream.vibrate";
    static final String KEY_RINGTONE = "photostream.ringtone";

    static final String KEY_USER_ID = User.KEY_UID;
    static final String KEY_USER_NICKNAME = User.KEY_NAME;
    static final String KEY_USER_AVATAR = User.KEY_AVATAR;
    static final String KEY_USER_MOBILE = User.KEY_USER_MOBILE;
    static final String KEY_USER_ADDRESS = User.KEY_ADDRESS;

    Preferences() {
    }
}
