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

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;
import com.funyoung.quickrepair.model.User;
import com.funyoung.quickrepair.transport.UsersClient;
import com.funyoung.quickrepair.utils.PerformanceUtils;
import com.funyoung.quickrepair.utils.TelephonyUtils;

import baidumapsdk.demo.DemoApplication;

public class SignUpFragment extends UserFragment {
    private static final String TAG = "SignUpFragment";

    private TextView mSendCodeView;
    private AsyncTask<Void, Void, String> mSendCodeTask;
    private AsyncTask<Void, Void, String> mLoginTask;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_signup;
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

            performLoginTask();
        }
    };

    @Override
    protected void onCreateViewFinish(final View rootView) {
        if (null != rootView) {
            setNameViewLabel(queryMyMobile());
            mSendCodeView = (TextView)rootView.findViewById(R.id.get_verify_code);
            setupVerifyButton();
        }

        mLoginButton.setEnabled(false);
        mPasswordView.setEnabled(false);
    }

    @Override
    protected View.OnClickListener getClickListener() {
        return mListener;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mSendCodeTask) {
            mSendCodeTask.cancel(true);
            mSendCodeTask = null;
        }

        if (null != mLoginTask) {
            mLoginTask.cancel(true);
            mLoginTask = null;
        }

        if (null != handler) {
            handler.removeCallbacks(task);
        }
    }

    private void setupVerifyButton() {
        if (null != mSendCodeView) {
            mSendCodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String text = validateUserName();
                    if (TelephonyUtils.isMobileNumber(text)) {
                        performSendCodeTask(text);
                    } else {
                        Log.e(TAG, "Should not be here!");
                        mNameView.selectAll();
                        mNameView.requestFocus();
                    }
                }
            });
        }
    }

    private void performSendCodeTask(final String mobile) {
        mSendCodeTask = new AsyncTask<Void, Void, String>() {
            boolean mResult = false;
            long startTime;
            @Override
            protected void onPreExecute() {
                mResult = false;
                startTime = System.currentTimeMillis();

                performCountingTask();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    mResult = UsersClient.sendVerifyCode(getActivity(), mobile);
                    return "Code MMS was sent to " + mobile;
                } catch (Exception e) {
                    return "Code request exception " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                PerformanceUtils.showToast(getActivity(), result, diff);

                if (mResult) {
                    mLoginButton.setEnabled(true);
                    mPasswordView.setEnabled(true);
                    mPasswordView.selectAll();
                    mPasswordView.requestFocus();
                } else {
                    count = 0;
                    postCheckCountingTask();
                }
            }
        };
        mSendCodeTask.execute();
    }

    private static final long A_SECOND = 1000;
    private static final int TOTAL_COUNT = 60;
    private final Handler handler = new Handler();
    private boolean run = false;
    private int count;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (run) {
                handler.postDelayed(this, A_SECOND);
                count--;
                mSendCodeView.setText(String.valueOf(count));
                postCheckCountingTask();
            }
        }
    };

    private void postCheckCountingTask() {
        if (count <= 0) {
            run = false;
            mSendCodeView.setText(R.string.qp_login_get_verify_code_again);
            mSendCodeView.setEnabled(true);
        }
    }

    private void performCountingTask() {
        run = true;
        count = TOTAL_COUNT;
        mSendCodeView.setEnabled(false);
        handler.removeCallbacks(task);
        handler.post(task);
    }

    private void performLoginTask() {
        if (null == mLoginTask) {
            mLoginTask = new AsyncTask<Void, Void, String>() {
                boolean mResult = false;
                long startTime;
                @Override
                protected void onPreExecute() {
                    mResult = false;
                    startTime = System.currentTimeMillis();

                    mLoginButton.setEnabled(false);
                    mSendCodeView.setEnabled(false);
                }

                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        final String mobile = validateUserName();
                        final String code = validatePassword();
                        User user = UsersClient.login(getActivity(), mobile, code);
                        if (null == user) {
                            mResult = false;
                        } else {
                            mResult = true;
                            DemoApplication application = (DemoApplication)getActivity().getApplication();
                            application.setLoginUser(user);
                        }
                        return "Login with " + mobile + ", code " + code;
                    } catch (Exception e) {
                        return "Login exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    if (mResult) {
                        ((MainActivity)getActivity()).finishLogin();
                    } else {
                        mLoginButton.setEnabled(true);
                        mSendCodeView.setEnabled(true);
                    }
                }
            };
        }
        mLoginTask.execute();
    }

    private String queryMyMobile() {
        return TelephonyUtils.queryMyMobile(getActivity().getApplicationContext());
    }
}
