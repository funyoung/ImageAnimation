
package com.funyoung.quickrepair.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.model.User;
import com.funyoung.quickrepair.transport.UsersClient;
import com.funyoung.quickrepair.utils.DialogUtils;
import com.funyoung.quickrepair.utils.PerformanceUtils;
import com.funyoung.quickrepair.utils.QiupuHelper;

import java.io.File;
import java.io.FileOutputStream;

import baidumapsdk.demo.DemoApplication;

public class ProfileFragment extends BaseFragment {
    private static final String TAG = "ProfileFragment";

    private View mRootView;
    private User mUser;

    private View mNameView;
    private View mSexView;
    private View mAddressView;
    private View mPhoneView;
    private View mRankView;

    ChangeTextListener mChangeNameListener;
    ChangeTextListener mChangeGenderListener;
    ChangeTextListener mChangeAddressListener;
    ChangeTextListener mChangeMobileListener;
    ChangeTextListener mViewRankListener;

    private AsyncTask<Void, Void, String> mLoginTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (null != mRootView) {
            initViews(inflater);
        }

        performLoginTask();

        return mRootView;
    }

    private void initViews(LayoutInflater inflater) {
        if (null == mRootView) {
            Log.e(TAG, "initViews, error with null view root");
            return;
        }

        if (mUser == null) {
            Log.e(TAG, "initViews, error with empty user");
            return;
        }

        ViewGroup profileContainer = (ViewGroup)mRootView.findViewById(R.id.container);
        if (null == profileContainer) {
            Log.e(TAG, "initViews, error with null view container");
            return;
        }

        if (profileContainer instanceof ViewGroup) {
            View itemView = inflater.inflate(R.layout.simple_profile_item_photo, null);
            if (null != itemView) {
                ImageView photoView = (ImageView)itemView.findViewById(R.id.img_profile);
                if (null != photoView) {
                    final String url = mUser.getAvatarUrl();
                    // todo: set on background thread
                    if (!TextUtils.isEmpty(url)) {
//                        photoView.setImageURI(Uri.parse(url));
                    }
                    if (mUser.getGender() == User.GENDER_FEMALE) {
                        photoView.setImageResource(R.drawable.avatar_girl);
                    }
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performAvatarUpdateTask();
                    }
                });
            }
            profileContainer.addView(itemView);

//            View header = profileContainer.findViewById(R.id.profile_header);
//            if (null != header) {
//                ImageView photoView = (ImageView)header.findViewById(R.id.img_profile);
//                if (null != photoView) {
//                    final String url = mUser.getAvatarUrl();
//                    if (!TextUtils.isEmpty(url)) {
//                        photoView.setImageURI(Uri.parse(url));
//                    }
//                }
//            }

            mNameView = addItemBane(inflater, profileContainer, R.string.profile_user_name, mUser.getNickName());
            mSexView = addItemBane(inflater, profileContainer, R.string.profile_user_gender, getGenderLabel());
            mAddressView = addItemBane(inflater, profileContainer, R.string.profile_user_address, mUser.getAddress());
            mPhoneView = addItemBane(inflater, profileContainer, R.string.profile_user_mobile, mUser.getMobile());

            if (mUser.isProviderType()) {
                mRankView = addItemBane(inflater, profileContainer, R.string.profile_user_rank, "");
            } else {
                TextView tvContent = (TextView)mPhoneView.findViewById(R.id.tv_content);
                tvContent.setCompoundDrawables(null, null, null, null);
            }

            if (null == mChangeNameListener) {
                mChangeNameListener = new ChangeTextListener(mNameView, R.string.profile_user_name, mChangeListener);
                mChangeGenderListener = new ChangeTextListener(mSexView, R.string.profile_user_gender, mChangeListener);
                mChangeAddressListener = new ChangeTextListener(mAddressView, R.string.profile_user_address, mChangeListener);
                mViewRankListener = new ChangeTextListener(mRankView, R.string.profile_user_rank, mChangeListener);
            }

            mNameView.setOnClickListener(mChangeNameListener);
            mSexView.setOnClickListener(mChangeGenderListener);
            mAddressView.setOnClickListener(mChangeAddressListener);
            if (null != mRankView) {
                mRankView.setOnClickListener(mViewRankListener);
            }
            if (mUser.isProviderType()) {
                if (null == mChangeMobileListener) {
                    mChangeMobileListener = new ChangeTextListener(mPhoneView, R.string.profile_user_mobile, mChangeListener);
                }
                mPhoneView.setOnClickListener(mChangeMobileListener);
            }
        }
    }

    private ChangeTextListener.OnChangeListener mChangeListener = new ChangeTextListener.OnChangeListener() {
        @Override
        public void onChangedFinish(View view, int keyResId, boolean cancel) {
            switch (keyResId) {
                case R.string.profile_user_name:
                    break;
                case R.string.profile_user_gender:
                    break;
                case R.string.profile_user_address:
                    break;
                case R.string.profile_user_mobile:
                    break;
                case R.string.profile_user_rank:
                    break;
                default:
                    break;
            }
        }
    };

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
        }
        profileContainer.addView(itemView);
        return itemView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mLoginTask) {
            mLoginTask.cancel(true);
            mLoginTask = null;
        }
    }

    public void updateProfile(User user) {
        mUser = user;
    }

    private void performLoginTask() {
        if (null == mUser || mUser.getUid() <= 0) {
            Log.e(TAG, "performLoginTask, skip without valid user.");
            return;
        }

        if (null == mLoginTask) {
            mLoginTask = new AsyncTask<Void, Void, String>() {
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
                        User user = UsersClient.getProfile(getActivity(), mUser.getUid());
                        if (null == user) {
                            mResult = false;
                        } else {
                            mResult = true;
                            mUser = user;
                        }
                        return "getProfile for " + mUser.getNickName();
                    } catch (Exception e) {
                        return "Login exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    if (mResult) {
                        refreshUI();
                    } else {
                    }
                }
            };
        }
        mLoginTask.execute();
    }

    private void refreshUI() {
        ((DemoApplication)getActivity().getApplication()).setLoginUser(mUser);

        TextView textView;
        textView = (TextView)mNameView.findViewById(R.id.tv_content);
        textView.setText(mUser.getNickName());

        textView = (TextView)mSexView.findViewById(R.id.tv_content);
        textView.setText(getGenderLabel());

        textView = (TextView)mAddressView.findViewById(R.id.tv_content);
        textView.setText(mUser.getAddress());

        textView = (TextView)mPhoneView.findViewById(R.id.tv_content);
        textView.setText(mUser.getMobile());
    }

    private String getGenderLabel() {
        String[] labels = getResources().getStringArray(R.array.gender_labels);
        final int i = mUser.getGender();
        if (i < 0 || i >= labels.length) {
            return labels[0];
        }
        return labels[i];
    }

    private static final int REQUEST_CODE_CAMERA_WITH_DATA = 9001;
    private static final int REQUEST_CODE_PHOTO_PICKED_WITH_DATA = 9002;
    private File mCurrentPhotoFile;
    private Bitmap photo ;
    private void performAvatarUpdateTask() {
        String[] items = new String[] {
                getString(R.string.edit_profile_img_camera),
                getString(R.string.edit_profile_img_location) };
        DialogUtils.showItemsDialog(getActivity(), "", 0, items,
                chooseEditImageItemClickListener);
    }

    DialogInterface.OnClickListener chooseEditImageItemClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 0) {
                doTakePhoto();// from camera
            } else {
                doPickPhotoFromGallery();// from  gallery
            }
        }
    };

    private void doTakePhoto() {
        try {
            // Launch camera to take photo for selected contact
            mCurrentPhotoFile = QiupuHelper.getTempAvatarFile(getActivity());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUEST_CODE_CAMERA_WITH_DATA);
        }
        catch (ActivityNotFoundException e) {}
    }

    private void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("return-data", true);
            intent.putExtra("output", Uri.fromFile(QiupuHelper.getTempAvatarFile(getActivity())));
            intent.putExtra("outputFormat", "JPEG");
            startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKED_WITH_DATA);
        }
        catch (ActivityNotFoundException e) {}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_PHOTO_PICKED_WITH_DATA == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists()) {
                    mCurrentPhotoFile.delete();
                }
                photo = data.getParcelableExtra("data");
                mCurrentPhotoFile = QiupuHelper.getTempAvatarFile(getActivity());
                FileOutputStream fOut = null;
                try {
                    if (mCurrentPhotoFile.exists()) {
                        mCurrentPhotoFile.delete();
                    }
                    mCurrentPhotoFile.createNewFile();
                    fOut = new FileOutputStream(mCurrentPhotoFile);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    editProfileImage(mCurrentPhotoFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        } else if (REQUEST_CODE_CAMERA_WITH_DATA == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = Uri.fromFile(QiupuHelper.getTempAvatarFile(getActivity()));
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 256);
                intent.putExtra("outputY", 256);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKED_WITH_DATA);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void editProfileImage(File avatarFile) {

    }
}

