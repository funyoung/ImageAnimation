/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.funyoung.quickrepair.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.utils.MockServer;
import com.funyoung.quickrepair.utils.TelephonyUtils;

public class LoginFragment extends UserFragment {
    private static final String TAG = "LoginFragment";

    @Override
    protected int getLayoutRes() {
        return R.layout.login;
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = validateUserName();
            if (TextUtils.isEmpty(name)) {
                return;
            }

            String passwd = validatePassword();
            if (TextUtils.isEmpty(passwd)) {
                return;
            }

//            if (null == mUser) {
//                mUser = new ParseUser();
//            }
//            mUser.setUsername(name);
//            mUser.setPassword(passwd);
//
//            mUser.signUpInBackground(new SignUpCallback() {
//                public void done(ParseException e) {
//                    if (e == null) {
//                        // Hooray! Let them use the app now.
//                        invokeSession(FragmentSession.SELECTION, null);
//                    } else {
//                        // Sign up didn't succeed. Look at the ParseException
//                        // to figure out what went wrong
//                        ParseErrorMap.showToast(getActivity(), e);
//                    }
//                }
//            });
        }
    };

    @Override
    protected void onCreateViewFinish(final View rootView) {
        if (null != rootView) {
            View verify = rootView.findViewById(R.id.get_verify_code);
            setupVerifyButton(verify);
        }
    }

    @Override
    protected View.OnClickListener getClickListener() {
        return mListener;
    }


    private static final int CODE_COUNT = 4;
    private String verifyCode;

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setupVerifyButton(View button) {
        if (null != button) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String text = validateUserName();
                    if (!TelephonyUtils.isMobileNumber(text)) {
                        Log.e(TAG, "Should not be here!");
                        mNameView.selectAll();
                        mNameView.requestFocus();
                    }

                    mLoginButton.setEnabled(false);

                    MockServer.requestSendingVerifyCode(text, CODE_COUNT,
                            new MockServer.ServerCallback() {
                                @Override
                                public void done(String code, String result, Exception e) {
                                    final String toastMsg;
                                    if (null == e) {
                                        verifyCode = result;
                                        toastMsg = getString(R.string.succeed_message_send_verify_code, verifyCode);
                                    } else {
                                        verifyCode = "";
                                        toastMsg = getString(R.string.error_message_send_verify_code);
                                        Log.e(TAG, toastMsg);
                                    }
                                    Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
                                }
                            });
//
//                    new AsyncTask<Void, Void, String>() {
//                        @Override
//                        protected String doInBackground(Void... voids) {
//                            try {
//                                MmsGateway.sendWebchineseMsg(text, verifyCode);
//                                return "Verify code in mms was sent to " + text;
//                            } catch (Exception e) {
//                                return "request mms code exception " + e.getMessage();
//                            }
//                        }
//
//                        protected void onPostExecute(String result) {
//                            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
//                        }
//                    }.execute();

                    mLoginButton.setEnabled(true);
                }
            });
        }
    }

    private String queryMyMobile() {
        return TelephonyUtils.queryMyMobile(getActivity().getApplicationContext());
    }
}
