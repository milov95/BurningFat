package com.milov.fat.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.huawei.huaweiwearable.callback.IDeviceConnectStatusCallback;
import com.huawei.huaweiwearable.callback.IResultReportCallback;
import com.huawei.huaweiwearable.constant.DeviceConnectionState;
import com.huawei.huaweiwearable.constant.DeviceType;
import com.huawei.huaweiwearable.data.DataUserInfo;
import com.huawei.huaweiwearableApi.HuaweiWearableManager;
import com.milov.fat.activity.HomeActivity;



/**
 * Created by Milov on 2015/8/23.
 */
public class HuaweiWearableHelper implements HomeActivity.ActivitiCallback{
    /**
     * 用于获取所有可穿戴数据
     */
    private HuaweiWearableManager manager;
    /**
     * 设备连接状态的回调
     */
    private MyDeviceConnectStatusCallback deviceConnectStatusCallback;
    /**
     * 设备连接状态
     */
    private int deviceStatus = DeviceConnectionState.DEVICE_UNKNOWN;
    /**
     * 与HomeActivity通信的handler
     */
    Handler handler;
    /**
     * 设备状态标识
     */
    public static final int CONNECTION_STATUS = 0;
    /**
     * 得到的用户信息标识
     */
    public static final int USER_INFO = 1;
    /**
     * 得到的步数标识
     */
    public static final int TODAY_HEALTH = 2;

    /**
     * 获取设备连接状态
     */
    public int getDeviceStatus(){
        deviceStatus=manager.getConnectStatus(DeviceType.HUAWEI_TALKBAND_B2);
        Log.i("获取设备状态","状态为 "+deviceStatus);
        return deviceStatus;
    }

    /**
     * 设置用户信息
     * @param heightCM
     * @param weightKG
     * @param genderMan1Woman2
     */
    public void setUserInfo (int heightCM,int weightKG,int genderMan1Woman2){
        DataUserInfo info = new DataUserInfo( 99, heightCM, weightKG, genderMan1Woman2, (int)(0.3*heightCM), (int)(0.45*heightCM),0);
        manager.setUserInfo(DeviceType.HUAWEI_TALKBAND_B2, info, new IResultReportCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.i("onSucess", "设置用户信息成功!");

            }

            @Override
            public void onFailure(int err_code, String err_msg) {
                Log.i("onFailure", "设置用户信息失败!");
            }
        });
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(){
        manager.getUserInfo(DeviceType.HUAWEI_TALKBAND_B2, new IResultReportCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.i("onSucess", "获取用户信息成功!");
                Message msg = handler.obtainMessage(USER_INFO, object);
                handler.handleMessage(msg);
            }

            @Override
            public void onFailure(int err_code, String err_msg) {
                Log.i("onFailure", "获取用户信息失败!");
                Message msg = handler.obtainMessage(USER_INFO, null);
                handler.handleMessage(msg);
            }
        });
    }

    /**
     * 获取当天运动数据
     */
    public void getTodayHealthData(){
        manager.getHealthDataCurrentDay(DeviceType.HUAWEI_TALKBAND_B2,new IResultReportCallback(){
            @Override
            public void onSuccess(Object object) {
                Log.i("onSucess", "获取当日数据成功!");
                Message msg = handler.obtainMessage(TODAY_HEALTH, object);
                handler.handleMessage(msg);
            }

            @Override
            public void onFailure(int err_code, String err_msg) {
                Log.i("onFailure", "获取当日数据失败!");
                Message msg = handler.obtainMessage(TODAY_HEALTH, null);
                handler.handleMessage(msg);
            }
        });
    }

    /**
     * 设备连接状态的回调类
     */
    private class MyDeviceConnectStatusCallback extends IDeviceConnectStatusCallback{
        @Override
        public void onConnectStatusChange(int deviceType, String macAddress, int status, int err_code) {
            switch (status){
                case 2://连接成功
                    deviceStatus=DeviceConnectionState.DEVICE_CONNECTED;
                    Log.i("statusChanged","connected");
                    break;
                case 3://连接失败
                    deviceStatus=DeviceConnectionState.DEVICE_CONNECT_FAILED;
                    Log.i("statusChanged","failed");
                    break;
                case 5://断开连接
                    deviceStatus=DeviceConnectionState.DEVICE_DISCONNECTED;
                    Log.i("statusChanged","disConnected");
                    break;
                default:
                    deviceStatus=DeviceConnectionState.DEVICE_UNKNOWN;
                    Log.i("statusChanged","default");
                    break;
            }
            Message msg = handler.obtainMessage(CONNECTION_STATUS, status);
            handler.handleMessage(msg);
        }
    }


    @Override
    public void onActivityCreated(Context context,Handler handler){
        //获取HuaweiWearableManager单一实例
        manager = HuaweiWearableManager.getInstance(context);
        //设备连接状态回调类的实例
        deviceConnectStatusCallback = new MyDeviceConnectStatusCallback();
        //向manager注册设备连接状态回调类
        manager.registerConnectStateCallback(deviceConnectStatusCallback);
        //获得HomeActivity的Handler
        this.handler = handler;
    }

}
