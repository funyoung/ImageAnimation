package com.funyoung.quickrepair.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.utils.MockServer;

import java.util.List;

import baidumapsdk.demo.BMapUtil;
import baidumapsdk.demo.DemoApplication;
import baidumapsdk.demo.common.MyLocationMapView;

/**
 * Created by yangfeng on 13-7-24.
 */
public class LocationOverlayFragment extends Fragment {
    // 定位相关
    LocationClient mLocClient;
    LocationData myLocData = null;
    public MyLocationListener myListener = new MyLocationListener();

    //定位图层
    LocationOverlay myLocationOverlay = null;
    //弹出泡泡图层
    private PopupOverlay pop  = null;//弹出泡泡图层，浏览节点时使用
    private TextView popupText = null;//泡泡view
    private View viewCache = null;

    //地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
    //如果不处理touch事件，则无需继承，直接使用MapView即可
    MyLocationMapView mMapView = null;	// 地图View
    private MapController mMapController = null;

    //UI相关
//    RadioGroup.OnCheckedChangeListener radioButtonListener = null;
//    Button requestLocButton = null;
    boolean isRequest = false;//是否手动触发请求定位
    boolean isFirstLoc = true;//是否首次定位
    boolean isLocationClientStop = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        DemoApplication app = (DemoApplication)getActivity().getApplication();
        app.checkToInit();
        /**
         * 由于MapView在inflate()中初始化,所以它需要在BMapManager初始化之后
         */
        View rootView = inflater.inflate(R.layout.fragment_locationoverlay,
                container, false);
//        setContentView(R.layout.activity_locationoverlay);
        getActivity().setTitle(R.string.title_main_mapview);
//        requestLocButton = (Button)rootView.findViewById(R.id.button1);
//        View.OnClickListener btnClickListener = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                //手动定位请求
//                requestLocClick();
//            }
//        };
//        requestLocButton.setOnClickListener(btnClickListener);

//        RadioGroup group = (RadioGroup)rootView.findViewById(R.id.radioGroup);
//        radioButtonListener = new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // TODO Auto-generated method stub
//                //
//                if (checkedId == R.id.defaulticon){
//                    //传入null则，恢复默认图标
//                    modifyLocationOverlayIcon(null);
//                }
//                if (checkedId == R.id.customicon){
//                    //修改为自定义marker
//                    modifyLocationOverlayIcon(getResources().getDrawable(R.drawable.icon_geo));
//                }
//            }
//        };
//        group.setOnCheckedChangeListener(radioButtonListener);

        //地图初始化
        mMapView = (MyLocationMapView)rootView.findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        //创建 弹出泡泡图层
        createPaopao();
        mMapView.PopupOverlay(pop);

        //定位初始化
        mLocClient = new LocationClient(getActivity());
        myLocData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        //定位图层初始化
        myLocationOverlay = new LocationOverlay(mMapView);
        //设置定位数据
        myLocationOverlay.setData(myLocData);
        //添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        //修改定位数据后刷新图层生效
        modifyLocationOverlayIcon(getResources().getDrawable(R.drawable.ic_map_my_location));
        mMapView.refresh();

        return rootView;
    }

    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick(){
        isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(getActivity(), "正在定位…", Toast.LENGTH_SHORT).show();
    }
    /**
     * 修改位置图标
     * @param marker
     */
    public void modifyLocationOverlayIcon(Drawable marker){
        //当传入marker为null时，使用默认图标绘制
        myLocationOverlay.setMarker(marker);
        //修改图层，需要刷新MapView生效
        mMapView.refresh();
    }
    /**
     * 创建弹出泡泡图层
     */
    public void createPaopao(){
        viewCache = getActivity().getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
            @Override
            public void onClickedPopup(int index) {
                Log.v("click", "clickapoapo");
            }
        };
        pop = new PopupOverlay(mMapView,popListener);
//        MyLocationMapView.pop = pop;
    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || isLocationClientStop)
                return ;
            myLocData.latitude = location.getLatitude();
            myLocData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            myLocData.accuracy = location.getRadius();
            myLocData.direction = location.getDerect();
            //更新定位数据
            myLocationOverlay.setData(myLocData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();

            initOverlay();

            //是手动触发请求或首次定位时，移动到定位点
            if (isRequest || isFirstLoc){
                //移动地图到定位点
                mMapController.animateTo(new GeoPoint((int)(myLocData.latitude* 1e6), (int)(myLocData.longitude *  1e6)));
                isRequest = false;
            }
            //首次定位完成
            isFirstLoc = false;
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }

    //继承MyLocationOverlay重写dispatchTap实现点击处理
    public class LocationOverlay extends MyLocationOverlay {

        public LocationOverlay(MapView mapView) {
            super(mapView);
            // TODO Auto-generated constructor stub
        }
        @Override
        protected boolean dispatchTap() {
            // TODO Auto-generated method stub
            //处理点击事件,弹出泡泡
            popupText.setBackgroundResource(R.drawable.popup);
            popupText.setText(R.string.popup_text_my_location);
            pop.showPopup(BMapUtil.getBitmapFromView(popupText),
                    new GeoPoint((int)(myLocData.latitude*1e6),
                            (int)(myLocData.longitude*1e6)),
                    8);
            return true;
        }

    }

    @Override
    public void onPause() {
        isLocationClientStop = true;
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        isLocationClientStop = false;
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        //退出时销毁定位
        if (mLocClient != null)
            mLocClient.stop();
        isLocationClientStop = true;
        mMapView.destroy();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    // todo: check and map this two method.
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mMapView.onRestoreInstanceState(savedInstanceState);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
    // todo: end


    // merge from overlap demo
//    private ArrayList<OverlayItem>  mItems = null;
    private MyOverlay mOverlay = null;
    private View popupInfo = null;
    private View popupLeft = null;
    private View popupRight = null;
//    private Button button = null;
    private MapView.LayoutParams layoutParam = null;
    private OverlayItem mCurItem = null;
    /**
     * overlay 位置坐标
     */
//    double mLon1 = 116.400244 ;
//    double mLat1 = 39.963175 ;
//    double mLon2 = 116.369199;
//    double mLat2 = 39.942821;
//    double mLon3 = 116.425541;
//    double mLat3 = 39.939723;
//    double mLon4 = 116.401394;
//    double mLat4 = 39.906965;
//    double mLon5 = 116.402096;
//    double mLat5 = 39.942057;

    /**
     * 清除所有Overlay
     * @param view
     */
    private void clearOverlay(View view) {
        if (null == mOverlay) return;
        mOverlay.removeAll();
        if (pop != null){
            pop.hidePop();
        }
        mMapView.refresh();
    }

    public void initOverlay(){
        if (null != mOverlay) {
            if (!isFirstLoc) {
                clearOverlay(null);
            }
            List<OverlayItem> items = MockServer.queryItemList(getActivity(), mMapView.getMapCenter());
            if (null != items && !items.isEmpty()) {
                mOverlay.addItem(items);
                mMapView.refresh();
            }
            return;
        }
        /**
         * 创建自定义overlay
         */
        mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.ic_map_location_nomal),mMapView);

        /**
         * 将item 添加到overlay中
         * 注意： 同一个itme只能add一次
         */
//        mOverlay.addItem(item1);
//        mOverlay.addItem(item2);
//        mOverlay.addItem(item3);
//        mOverlay.addItem(item4);
        mOverlay.addItem(MockServer.queryItemList(getActivity(), mMapView.getMapCenter()));
//        mOverlay.addItem(item5);
        /**
         * 保存所有item，以便overlay在reset后重新添加
         */
//        mItems = new ArrayList<OverlayItem>();
//        mItems.addAll(mOverlay.getAllItem());
        /**
         * 将overlay 添加至MapView中
         */
        mMapView.getOverlays().add(mOverlay);
        /**
         * 刷新地图
         */
        mMapView.refresh();

        /**
         * 向地图添加自定义View.
         */


        viewCache = getActivity().getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupInfo = (View) viewCache.findViewById(R.id.popinfo);
        popupLeft = (View) viewCache.findViewById(R.id.popleft);
        popupRight = (View) viewCache.findViewById(R.id.popright);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);

//        button = new Button(getActivity());
//        button.setBackgroundResource(R.drawable.popup);

        /**
         * 创建一个popupoverlay
         */
        PopupClickListener popListener = new PopupClickListener(){
            @Override
            public void onClickedPopup(int index) {
                if ( index == 0){
                    //更新item位置
                    pop.hidePop();
                    GeoPoint p = new GeoPoint(mCurItem.getPoint().getLatitudeE6()+5000,
                            mCurItem.getPoint().getLongitudeE6()+5000);
                    mCurItem.setGeoPoint(p);
                    mOverlay.updateItem(mCurItem);
                    mMapView.refresh();
                }
                else if(index == 2){
                    //更新图标
                    mCurItem.setMarker(getResources().getDrawable(R.drawable.nav_turn_via_1));
                    mOverlay.updateItem(mCurItem);
                    mMapView.refresh();
                }
            }
        };
        pop = new PopupOverlay(mMapView,popListener);
    }

    public class MyOverlay extends ItemizedOverlay {

        public MyOverlay(Drawable defaultMarker, MapView mapView) {
            super(defaultMarker, mapView);
        }


        @Override
        public boolean onTap(int index){
            OverlayItem item = getItem(index);
            mCurItem = item ;
//            if (index == 4){
//                button.setText("这是一个系统控件");
//                GeoPoint pt = new GeoPoint ((int)(mLat5*1E6),(int)(mLon5*1E6));
//                //创建布局参数
//                layoutParam  = new MapView.LayoutParams(
//                        //控件宽,继承自ViewGroup.LayoutParams
//                        MapView.LayoutParams.WRAP_CONTENT,
//                        //控件高,继承自ViewGroup.LayoutParams
//                        MapView.LayoutParams.WRAP_CONTENT,
//                        //使控件固定在某个地理位置
//                        pt,
//                        0,
//                        -32,
//                        //控件对齐方式
//                        MapView.LayoutParams.BOTTOM_CENTER);
//                //添加View到MapView中
//                mMapView.addView(button,layoutParam);
//            } else {
                popupText.setText(getItem(index).getTitle());
                Bitmap[] bitMaps={
                        BMapUtil.getBitmapFromView(popupLeft),
                        BMapUtil.getBitmapFromView(popupInfo),
                        BMapUtil.getBitmapFromView(popupRight)
                };
                pop.showPopup(bitMaps,item.getPoint(),32);
//            }
            return true;
        }

        @Override
        public boolean onTap(GeoPoint pt , MapView mMapView){
            if (pop != null){
                pop.hidePop();
//                mMapView.removeView(button);
            }
            return false;
        }

    }

}
