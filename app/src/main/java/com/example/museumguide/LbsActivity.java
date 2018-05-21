package com.example.museumguide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LbsActivity extends AppCompatActivity {

    public LocationClient mLocationClient;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate=true;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_lbs);
        mapView = findViewById(R.id.mapview);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        positionText = (TextView) findViewById(R.id.position_view);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LbsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LbsActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LbsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LbsActivity.this,permissions,1);
        } else{
            requestLocation();
        }
    }

    private void requestLocation(){
        //开始定位
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);//每5秒更新一次位置
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected  void onDestroy(){//停止定位服务，防止耗电
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[]permissions,
                                           int[]grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

//    private void navigateTo(BDLocation location){
//        if(isFirstLocate){
//            LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
//            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
//            baiduMap.animateMapStatus(update);//缩放功能
//            update=MapStatusUpdateFactory.zoomTo(16f);
//            isFirstLocate=false;
//        }
//        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
//        locationBuilder.latitude(location.getLatitude());
//        locationBuilder.longitude(location.getLongitude());
//        MyLocationData locationData=locationBuilder.build();
//        baiduMap.setMyLocationData(locationData);
//    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation( BDLocation location){
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);
            if (isFirstLocate) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 16);//设置地图中心及缩放级别
                baiduMap.animateMapStatus(update);
                isFirstLocate = false;
                Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
            }
//            positionText.setText(currentPosition);
        }
        public void onConnectHotSpotMessage(String s,int i){

        }
    }

}