package com.milov.fat.util;

import android.content.Context;
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
     * 用户信息
     */
    private DataUserInfo userInfo;

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
            public void onSuccess(Object object)  {
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
    public DataUserInfo getUserInfo(){
        manager.getUserInfo(DeviceType.HUAWEI_TALKBAND_B2,new IResultReportCallback(){
            @Override
            public void onSuccess(Object object) {
                Log.i("onSucess", "获取用户信息成功!");
                userInfo = (DataUserInfo) object;
            }

            @Override
            public void onFailure(int err_code, String err_msg) {
                Log.i("onFailure", "获取用户信息失败!");
            }
        });
        return userInfo;
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
                    setUserInfo(180,60,1);
                    Log.i("connected",getUserInfo().getHeight()+"");
                    break;
                case 3://连接失败
                    deviceStatus=DeviceConnectionState.DEVICE_CONNECT_FAILED;
                    break;
                case 5://断开连接
                    deviceStatus=DeviceConnectionState.DEVICE_DISCONNECTED;
                    break;
                default:
                    deviceStatus=DeviceConnectionState.DEVICE_UNKNOWN;
                    break;
            }
        }
    }


    @Override
    public void onActivityStart(Context context){
        //获取HuaweiWearableManager单一实例
        manager = HuaweiWearableManager.getInstance(context);
        //设备连接状态回调类的实例
        deviceConnectStatusCallback = new MyDeviceConnectStatusCallback();
        //向manager注册设备连接状态回调类
        manager.registerConnectStateCallback(deviceConnectStatusCallback);
    }

}
