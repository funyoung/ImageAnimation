
package com.funyoung.quickrepair.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.model.Post;
import com.funyoung.quickrepair.model.User;
import com.funyoung.quickrepair.transport.BillingClient;
import com.funyoung.quickrepair.utils.PerformanceUtils;

import baidumapsdk.demo.DemoApplication;

public class PostCreateFragment extends BaseFragment {
    private static final String TAG = "ProfileFragment";
    private static final int MIN_CHAR_NUM = 2;

    private View mRootView;

    private User mUser;

    private View mAddressView;
    private View mLocationView;
    private View mContactView;
    private View mPhoneView;

    private View mBrandView;
    private View mModelView;
    private View mDateView;

    private View mDescriptionView;

    private int mMainId;
    private int mSubId;

    private AsyncTask<Void, Void, String> mPublishTask;

    private View.OnFocusChangeListener mFocusChangeValidator = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (view instanceof EditText) {
                EditText editText = (EditText)view;
                final String typedText = editText.getText().toString();
                if (!TextUtils.isEmpty(typedText) && typedText.length() < MIN_CHAR_NUM) {
                    Toast.makeText(getActivity(), R.string.error_message_limit_two_char,
                            Toast.LENGTH_SHORT).show();
                    editText.requestFocus();
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (null != mRootView) {
            initViews(inflater);
        }

        return mRootView;
    }

    private void initViews(LayoutInflater inflater) {
        if (null == mRootView) {
            Log.e(TAG, "initViews, error with null view root");
            return;
        }

        ViewGroup profileContainer = (ViewGroup)mRootView.findViewById(R.id.container);
        if (null == profileContainer) {
            Log.e(TAG, "initViews, error with null view container");
            return;
        }

        if (null == mUser) {
            mUser = ((DemoApplication)getActivity().getApplication()).getLoginUser();
            if (mUser == null) {
                Log.e(TAG, "initViews, error with null user found");
                return;
            }
        }

        if (profileContainer instanceof ViewGroup) {
            mAddressView = addItemBane(inflater, profileContainer, R.string.post_address, mUser.getAddress());

            mLocationView = addItemBane(inflater, profileContainer, R.string.post_location, R.string.post_hint_location);
            mContactView = addItemBane(inflater, profileContainer, R.string.post_contact, R.string.post_hint_limit_two_char);
            mPhoneView = addItemBane(inflater, profileContainer, R.string.profile_user_mobile, mUser.getMobile());

            mBrandView= addItemBane(inflater, profileContainer, R.string.post_brand, R.string.post_hint_limit_two_char);
            mModelView= addItemBane(inflater, profileContainer, R.string.post_model, R.string.post_hint_limit_two_char);
            mDateView = addItemBane(inflater, profileContainer, R.string.post_date, R.string.post_hint_date);

            mDescriptionView = addItemBane(inflater, profileContainer, R.string.post_description, R.string.post_hint_description);

            View itemView = inflater.inflate(R.layout.simple_post_attach_poto, null);
            if (null != itemView) {
                ImageView photoView = (ImageView)itemView.findViewById(R.id.img_profile);
                if (null != photoView) {
                }
            }
            profileContainer.addView(itemView);

            Button publishButton = new Button(getActivity());
            publishButton.setText(R.string.publish);
            publishButton.setBackgroundResource(R.drawable.button_blue_bg);
            publishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAndSendPost();
                }
            });
            profileContainer.addView(publishButton);
        }
    }

    private View addItemBane(LayoutInflater inflater, ViewGroup profileContainer,
                             int labelResId, int hintResId) {
        View itemView = inflater.inflate(R.layout.simple_post_edit_item, null);
        if (null != itemView) {
            TextView textView = (TextView)itemView.findViewById(R.id.tv_label);
            if (null != textView) {
                textView.setText(labelResId);
            }

            EditText editText = (EditText)itemView.findViewById(R.id.tv_content);
            if (null != editText) {
                editText.setHint(hintResId);
                editText.setOnFocusChangeListener(mFocusChangeValidator);
            }
        }
        profileContainer.addView(itemView);
        return itemView;
    }

    private View addItemBane(LayoutInflater inflater, ViewGroup profileContainer,
                             int labelResId, String nameValue) {
        View itemView = inflater.inflate(R.layout.simple_profile_item, null);
        if (null != itemView) {
            TextView textView = (TextView)itemView.findViewById(R.id.tv_label);
            if (null != textView) {
                textView.setText(labelResId);
            }
            textView = (TextView)itemView.findViewById(R.id.tv_content);
            if (null != textView) {
                textView.setText(nameValue);
            }

            itemView.setOnClickListener(new ChangeTextListener(itemView, labelResId, null));
            profileContainer.addView(itemView);
        }
        return itemView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mPublishTask) {
            mPublishTask.cancel(true);
            mPublishTask = null;
        }
    }

    private void performPostTask() {
        if (null == mUser || mUser.getUid() <= 0) {
            Log.e(TAG, "performPostTask, skip without valid user.");
            return;
        }

        final Post post = encodePost();
        if (null == post) {
            Log.e(TAG, "performPostTask, skip with invalid post.");
            return;
        }

        if (null == mPublishTask) {
            mPublishTask = new AsyncTask<Void, Void, String>() {
                boolean mResult = false;
                long startTime;
                @Override
                protected void onPreExecute() {
                    mResult = false;
                    startTime = System.currentTimeMillis();
                }

                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        mResult = BillingClient.createBill(getActivity(), post);
                        return "createBill succeed by " + mUser.getNickName();
                    } catch (Exception e) {
                        return "createBill exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    if (mResult) {
                        Toast.makeText(getActivity(), R.string.post_result_succeed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.post_result_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        mPublishTask.execute();
    }

    private Post encodePost() {
        Post post = new Post();
        post.uid = mUser.getUid();
        post.category = mMainId;
        post.subCategory = mSubId;
        post.address = getContent(mAddressView);
        post.area = getContent(mLocationView);
        post.contact = getContent(mContactView);
        post.mobile = getContent(mPhoneView);
        post.brand = getContent(mBrandView);
        post.model = getContent(mModelView);
        post.createYear = getContent(mDateView);
        post.description = getContent(mContactView);
        return post;
    }

    private String getContent(View hostView) {
        if (null != hostView) {
            TextView textView = (TextView)hostView.findViewById(R.id.tv_content);
            if (null != textView) {
                return textView.getText().toString();
            }
        }
        return "";
    }

//    public void updateProfile(User user) {
//        if (mUser != user) {
//            mUser = user;
//            refreshUI();
//        }
//    }

//    private void refreshUI() {
//        TextView textView;
//        textView = (TextView)mAddressView.findViewById(R.id.tv_content);
//        textView.setText(mUser.getAddress());
//
//        textView = (TextView)mPhoneView.findViewById(R.id.tv_content);
//        textView.setText(mUser.getMobile());
//    }
    private void checkAndSendPost() {
        if (!checkNonEmptyField()) {
            Toast.makeText(getActivity(), R.string.error_post_empty_field, Toast.LENGTH_SHORT).show();
            return;
        }

        performPostTask();
    }

    private boolean checkNonEmptyField() {
        TextView textView = (TextView)mAddressView.findViewById(R.id.tv_content);
        if (null != textView) {
            if (TextUtils.isEmpty(textView.getText())) {
                return false;
            }
        }
        textView = (TextView)mLocationView.findViewById(R.id.tv_content);
        if (null != textView) {
            if (TextUtils.isEmpty(textView.getText())) {
                return false;
            }
        }

        textView = (TextView)mDescriptionView.findViewById(R.id.tv_content);
        if (null != textView) {
            if (TextUtils.isEmpty(textView.getText())) {
                return false;
            }
        }

        return true;
    }

    public void updateCategory(int mainId, int subId) {
        mMainId = mainId;
        mSubId = subId;
    }
}
