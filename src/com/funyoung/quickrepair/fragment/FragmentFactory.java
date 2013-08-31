package com.funyoung.quickrepair.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.model.User;

/**
 * Created by yangfeng on 13-8-10.
 */
public class FragmentFactory {
    private static final String FRAGMENT_DEFAULT = "FRAGMENT_DEFAULT";
    private static final String FRAGMENT_LOGIN = "FRAGMENT_LOGIN";
    private static final String FRAGMENT_MAP = "FRAGMENT_MAP";
    private static final String FRAGMENT_PROFILE = "FRAGMENT_PROFILE";
    private static final String FRAGMENT_POST = "FRAGMENT_POST";
    private static final String FRAGMENT_POST_LIST = "FRAGMENT_POST_LIST";

    private String mCurrentFragment;
    public void gotoLoinFragment() {
        if (mCurrentFragment != FRAGMENT_LOGIN) {
            mCurrentFragment = FRAGMENT_LOGIN;
            gotoFragmentView(new SignUpFragment(), FRAGMENT_LOGIN, FRAGMENT_DEFAULT);
        }

    }

    public void gotoLocationFragment() {
        if (mCurrentFragment != FRAGMENT_MAP) {
            mCurrentFragment = FRAGMENT_MAP;
            gotoFragmentView(new LocationOverlayFragment(), FRAGMENT_MAP, FRAGMENT_DEFAULT);
        }
    }
    public  void gotoDefaultView() {
        if (mCurrentFragment != FRAGMENT_DEFAULT) {
            mCurrentFragment = FRAGMENT_DEFAULT;
            gotoFragmentView(new CategoryGridFragment(), FRAGMENT_DEFAULT, null);
        }
    }

    public void gotoProfileFragment(User user) {
        if (mCurrentFragment != FRAGMENT_PROFILE) {
            Bundle args = user.toBundle();
            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(args);
            mCurrentFragment = FRAGMENT_PROFILE;
            gotoFragmentView(profileFragment,  FRAGMENT_PROFILE,  FRAGMENT_DEFAULT);
        } else {
            ((ProfileFragment)getCurrentFragment()).updateProfile(user);
        }
    }

    public void gotoPostFragment(User user, int mainId, int subId) {

        if (mCurrentFragment != FRAGMENT_POST) {
            mCurrentFragment = FRAGMENT_POST;
            PostCreateFragment postFragment = new PostCreateFragment();
            postFragment = new PostCreateFragment();
            gotoFragmentView(postFragment, FRAGMENT_POST,  FRAGMENT_DEFAULT);
        }
        ((PostCreateFragment)getCurrentFragment()).updateCategory(mainId, subId);
    }
    private void gotoFragmentView(Fragment fragment, String name, String stackName) {
        FragmentTransaction ft = _fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(name)) {
            ft.replace(R.id.content_frame, fragment);
        } else {
            ft.replace(R.id.content_frame, fragment, name);
        }
        if (false || !TextUtils.isEmpty(stackName)) {
            ft.addToBackStack(stackName);
        }
        ft.commit();
    }

    private static FragmentFactory _instance;
    private static FragmentManager _fragmentManager;
    public static FragmentFactory getInstance(Activity context) {
        if (null == _instance) {
            _instance = new FragmentFactory();
        }

        if (null == _fragmentManager) {
            _fragmentManager = context.getFragmentManager();
        }

        return _instance;
    }

    public boolean isDefaultFragment() {
        return mCurrentFragment == FRAGMENT_DEFAULT;
    }

    public void releaseCache() {
        if (TextUtils.isEmpty(mCurrentFragment)) {
            FragmentTransaction ft = _fragmentManager.beginTransaction();
            ft.remove(getCurrentFragment());
            mCurrentFragment = null;
            ft.commit();
        }
    }

    public void onDestroy() {
        releaseCache();
        _instance = null;
    }

    public void gotoPostListView() {
        if (mCurrentFragment != FRAGMENT_POST_LIST) {
            mCurrentFragment = FRAGMENT_POST_LIST;
            gotoFragmentView(new PostListFragment(), FRAGMENT_POST_LIST,  FRAGMENT_DEFAULT);
        }
    }
    private Fragment getCurrentFragment() {
        return _fragmentManager.findFragmentByTag(mCurrentFragment);
    }
}
