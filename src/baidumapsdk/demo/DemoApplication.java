package baidumapsdk.demo;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.funyoung.quickrepair.SettingsActivity;
import com.funyoung.quickrepair.model.User;

/**
 * Created by yangfeng on 13-7-22.
 */

public class DemoApplication extends Application {

    private static DemoApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    public static final String strKey = "12B6045EA86554079304011DCDD2F0CDEBBDA00F";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initEngineManager(this);
        mUser = SettingsActivity.getLoginUser(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mUser = null;
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(DemoApplication.getInstance().getApplicationContext(),
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }

    public static DemoApplication getInstance() {
        return mInstance;
    }

    public void checkToInit() {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            mBMapManager.init(strKey,new MyGeneralListener());
        }
    }


    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                        Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(),
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                DemoApplication.getInstance().m_bKeyRight = false;
            }
        }
    }

    private static User mUser;
    public void setLoginUser(User user) {
        SettingsActivity.setLoginUser(this, user);
        mUser = user;
    }
    public User getLoginUser() {
        if (null == mUser) {
            mUser = SettingsActivity.getLoginUser(this);
        }
        return mUser;
    }
}
