package amap.com.service;

import amap.com.Client.RecordClient;
import amap.com.database.DbAdapter;
import amap.com.record.TraceRecordDTO;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;

import java.util.Date;

public class LocationService extends Service implements LocationSource,
        AMapLocationListener {

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    // 当服务第一次创建的时候调用
    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }
    // 每次调用startService都会执行下面的方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service", "onStartCommand!");
        init();
        mLocationClient.startLocation();
        intervalSetThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //获取最近3s内精度最高的一次定位结果：
            mLocationOption.setOnceLocationLatest(true);
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(interval);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();

        }
    }

    /**
     * 定时修改interval
     */
    private static long interval = 120000;
    Thread intervalSetThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                long intervalNew = RecordClient.getInterval();
                if (intervalNew != interval) {
                    interval = intervalNew;
                    mLocationClient.stopLocation();
                    mLocationOption.setInterval(interval);
                    mLocationClient.setLocationOption(mLocationOption);
                    mLocationClient.startLocation();
                }
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    protected void saveRecord(AMapLocation aMapLocation) {
        try {
            if (aMapLocation != null) {
                String point = amapLocationToString(aMapLocation);
                TraceRecordDTO traceRecordDTO = DbAdapter.createrecord(point, new Date());
                traceRecordDTO.setImei(getIMEI());
                traceRecordDTO.setUserInfo(getDeviceName());

                RecordService.addTraceRecord(traceRecordDTO);
            } else {
                Toast.makeText(this, "没有记录到路径", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String getIMEI() {
        String android_id = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    private String getDeviceName() {
        String deviceName = Build.BRAND + "-" + Build.MODEL;
        try {
            deviceName = BluetoothAdapter.getDefaultAdapter().getName();
        } catch (Exception e) {
        }
        return deviceName;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("service", "onStartCommand!");
        init();
        mLocationClient.startLocation();
        return null;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
    }

    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();

        }
        mLocationClient = null;
    }

    /**
     * 定位结果回调
     *
     * @param amapLocation 位置信息类
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                saveRecord(amapLocation);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }
}
