/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.funyoung.quickrepair;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.push.Utils;
import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.fragment.BaseFragment;
import com.funyoung.quickrepair.fragment.FragmentFactory;
import com.funyoung.quickrepair.model.User;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import org.json.JSONException;
import org.json.JSONObject;

import baidumapsdk.demo.BMapApiDemoMain;
import baidumapsdk.demo.DemoApplication;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
//    private ListView listView;
//    private TextView mContent;

    private ActionBarHelper mActionBar;

    private ActionBarDrawerToggle mDrawerToggle;

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    /**
     * Create a compatible helper that will manipulate the action bar if
     * available.
     */
    private ActionBarHelper createActionBarHelper() {
        return new ActionBarHelper();
    }

    /**
     * A drawer listener can be used to respond to drawer events such as
     * becoming fully opened or closed. You should always prefer to perform
     * expensive operations such as drastic relayout when no animation is
     * currently in progress, either before or after the drawer animates.
     *
     * When using ActionBarDrawerToggle, all DrawerLayout listener methods
     * should be forwarded if the ActionBarDrawerToggle is not used as the
     * DrawerLayout listener directly.
     */
    private class DemoDrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
            mActionBar.onDrawerOpened();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
            mActionBar.onDrawerClosed();
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    private class ActionBarHelper {
        private final ActionBar mActionBar;
        private CharSequence mDrawerTitle;
        private CharSequence mTitle;

        private ActionBarHelper() {
            mActionBar = getActionBar();
        }

        public void init() {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mTitle = mDrawerTitle = getTitle();
        }

        /**
         * When the drawer is closed we restore the action bar state reflecting
         * the specific contents in view.
         */
        public void onDrawerClosed() {
            mActionBar.setTitle(mTitle);
            refreshOptionsMenu();
        }

        /**
         * When the drawer is open we set the action bar to a generic title. The
         * action bar should only contain data relevant at the top level of the
         * nav hierarchy represented by the drawer, as the rest of your content
         * will be dimmed down and non-interactive.
         */
        public void onDrawerOpened() {
            mActionBar.setTitle(mDrawerTitle);
            refreshOptionsMenu();
        }

        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }

//    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
//    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter mDrawerAdapter;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this);
        MobclickAgent.updateOnlineConfig(this);

        setContentView(R.layout.activity_main_home);

        schedule();

        // The attacher should always be created in the Activity's onCreate
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        mTitle = mDrawerTitle = getTitle();
//        mPlanetTitles = getResources().getStringArray(R.array.planets_array);

        User user = getLoginUser();
        if (null == user) {
            mPlanetTitles = getResources().getStringArray(R.array.qp_navigation_title_array);
        } else {
            mPlanetTitles = getResources().getStringArray(R.array.qp_navigation_title_login_array);
            mPlanetTitles[0] = user.getNickName();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerListener(new DemoDrawerListener());
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mPlanetTitles);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setCacheColorHint(0);
        mDrawerList.setScrollingCacheEnabled(false);
        mDrawerList.setScrollContainer(false);
        mDrawerList.setFastScrollEnabled(true);
        mDrawerList.setSmoothScrollbarEnabled(true);

        mActionBar = createActionBarHelper();
        mActionBar.init();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            selectNavigateItem(1);
        }
    }

    private Menu mOptionMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mOptionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    // If the nav drawer is open, hide action items related to the content view
    private void refreshOptionsMenu() {
        if (null != mOptionMenu) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            mOptionMenu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
            mOptionMenu.findItem(R.id.action_toggle).setVisible(!drawerOpen);
        }
    }
    private void updateToggleMenuItem() {
        if (null != mOptionMenu) {
            mOptionMenu.findItem(R.id.action_toggle).setIcon(isDefaultFragment() ?
                R.drawable.ic_actionbar_map : R.drawable.ic_actionbar_list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_toggle:
                toggleView();
                return true;
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_demos:
                shootStartActivity(BMapApiDemoMain.class);
                return true;
            case R.id.menu_sliding:
//                shootStartActivity(com.example.android.slidingfragments.SlidingFragments.class);
                return true;
            case R.id.menu_feedback:
                gotoFeedbackView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shootStartActivity(Class<?> cls) {
        Intent i = new Intent(getApplicationContext(), cls);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        } else {
            Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
        }
    }

    private void gotoFeedbackView() {
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.startFeedbackActivity();
    }

    private void toggleView() {
        if (isDefaultFragment()) {
            gotoLocationFragment();
        } else {
            gotoDefaultView();
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectNavigateItem(position);
        }
    }

    private void selectNavigateItem(int position) {
        mActionBar.setTitle(getNavigationTitle(position));
        mDrawerLayout.closeDrawer(mDrawerList);

        switch (position) {
            case 0: // register or login
                gotoLoinFragment();
                break;
            case 1: // category
                gotoDefaultView();
                break;
            case 2: // Post
                gotoPostListView();
                break;
            case 3: // Settings
                gotoSettingView();
                break;
            case 4: // Feedback
                gotoFeedbackView();
                break;
            default:
                return;
        }
        updateToggleMenuItem();
    }

    private FragmentFactory mFactory;
    private FragmentFactory getFragmentFactory() {
        if (null == mFactory) {
            mFactory = FragmentFactory.getInstance(this);
        }
        return mFactory;
    }
    private boolean isDefaultFragment() {
        return getFragmentFactory().isDefaultFragment();
    }

    private void gotoLoinFragment() {
        User user = ((DemoApplication)getApplication()).getLoginUser();
        if (null == user) {
            getFragmentFactory().gotoLoinFragment();
        } else {
            getFragmentFactory().gotoProfileFragment(user);
        }
    }

    private void gotoDefaultView() {
        getFragmentFactory().gotoDefaultView();
    }

    private void gotoPostListView() {
        getFragmentFactory().gotoPostListView();
    }

    private void gotoSettingView() {
        SettingsActivity.show(this);
    }

    private void gotoLocationFragment() {
        getFragmentFactory().gotoLocationFragment();
        updateToggleMenuItem();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // todo: login/logout user end
    public static void invoke(Context context, BaseFragment.FragmentSession session,
                              Object o, Exception exception) {
        // todo: callback from login fragment after login close

    }

    private User getLoginUser() {
        return ((DemoApplication)getApplication()).getLoginUser();
    }

    public void finishLogin() {
        User user = getLoginUser();
        if (null != user) {
            mPlanetTitles = getResources().getStringArray(R.array.qp_navigation_title_login_array);
            mPlanetTitles[0] = user.getNickName();
            mDrawerAdapter.notifyDataSetChanged();
        }
        gotoDefaultView();
    }

    public void startPost(int mainId, int subId, String mainLabel, String subLabel) {
        User user = ((DemoApplication)getApplication()).getLoginUser();
        if (null == user) {
            getFragmentFactory().gotoLoinFragment();
        } else {
            setTitle(mainLabel + ">" + subLabel);
            getFragmentFactory().gotoPostFragment(user, mainId, subId);
        }
        updateToggleMenuItem();
    }

    private CharSequence getNavigationTitle(int position) {
        if (position == 0 && SettingsActivity.hasLoginUser(this)) {
            return getString(R.string.app_title_profile);
        }
        return mPlanetTitles[position];
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        showChannelIds();
    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentFactory.getInstance(this).releaseCache();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentFactory.getInstance(this).onDestroy();
    }

    private void schedule() {
        //SharedPreferences preferences = getSharedPreferences(Preferences.NAME, MODE_PRIVATE);
        //if (!preferences.getBoolean(Preferences.KEY_ALARM_SCHEDULED, false)) {
        CheckUpdateService.schedule(this);
        //    preferences.edit().putBoolean(Preferences.KEY_ALARM_SCHEDULED, true).commit();
        //}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 如果要统计Push引起的用户使用应用情况，请实现本方法，且加上这一个语句
        setIntent(intent);

        handleIntent(intent);
    }

    /**
     * 处理Intent
     *
     * @param intent
     *            intent
     */
    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (Utils.ACTION_RESPONSE.equals(action)) {

            String method = intent.getStringExtra(Utils.RESPONSE_METHOD);

            if (PushConstants.METHOD_BIND.equals(method)) {
                String toastStr = "";
                int errorCode = intent.getIntExtra(Utils.RESPONSE_ERRCODE, 0);
                if (errorCode == 0) {
                    String content = intent
                            .getStringExtra(Utils.RESPONSE_CONTENT);
                    String appid = "";
                    String channelid = "";
                    String userid = "";

                    try {
                        JSONObject jsonContent = new JSONObject(content);
                        JSONObject params = jsonContent
                                .getJSONObject("response_params");
                        appid = params.getString("appid");
                        channelid = params.getString("channel_id");
                        userid = params.getString("user_id");
                    } catch (JSONException e) {
                        Log.e(Utils.TAG, "Parse bind json infos error: " + e);
                    }

                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("appid", appid);
                    editor.putString("channel_id", channelid);
                    editor.putString("user_id", userid);
                    editor.commit();

                    showChannelIds();

                    toastStr = "Bind Success";
                } else {
                    toastStr = "Bind Fail, Error Code: " + errorCode;
                    if (errorCode == 30607) {
                        Log.d("Bind Fail", "update channel token-----!");
                    }
                }

                Toast.makeText(this, toastStr, Toast.LENGTH_LONG).show();
            }
//        } else if (Utils.ACTION_LOGIN.equals(action)) {
//            String accessToken = intent
//                    .getStringExtra(Utils.EXTRA_ACCESS_TOKEN);
//            PushManager.startWork(getApplicationContext(),
//                    PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
//            isLogin = true;
//            initButton.setText("更换百度账号初始化Channel");
        } else if (Utils.ACTION_MESSAGE.equals(action)) {
            String message = intent.getStringExtra(Utils.EXTRA_MESSAGE);
            String summary = "Receive message from server:\n\t";
            Log.e(Utils.TAG, summary + message);
            JSONObject contentJson = null;
            String contentStr = message;
            try {
                contentJson = new JSONObject(message);
                contentStr = contentJson.toString(4);
            } catch (JSONException e) {
                Log.d(Utils.TAG, "Parse message json exception.");
            }
            summary += contentStr;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(summary);
            builder.setCancelable(true);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            Log.i(Utils.TAG, "Activity normally start!");
        }
    }

    private void showChannelIds() {
        final String content = SettingsActivity.getChannelIdsContent(this);
        Log.d(TAG, "showChannelIds, show channel ids content = " + content);
//        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
//        Resources resource = this.getResources();
//        String pkgName = this.getPackageName();
//        infoText = (TextView) findViewById(resource.getIdentifier("text", "id", pkgName));
//        if (infoText != null) {
//            infoText.setText(content);
//            infoText.invalidate();
//        }
    }
}