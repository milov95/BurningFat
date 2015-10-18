package com.milov.fat.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.test.InstrumentationTestSuite;
import android.util.Log;
import com.huawei.huaweiwearable.callback.IDeviceConnectStatusCallback;
import com.huawei.huaweiwearable.callback.IResultReportCallback;
import com.huawei.huaweiwearable.constant.DeviceConnectionState;
import com.huawei.huaweiwearable.constant.DeviceType;
import com.huawei.huaweiwearable.data.DataHealthData;
import com.huawei.huaweiwearable.data.DataRawSportData;
import com.huawei.huaweiwearable.data.DataUserInfo;
import com.huawei.huaweiwearableApi.HuaweiWearableManager;
import com.milov.fat.activity.HomeActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


/**
 * Created by Milov on 2015/8/23.
 */
public class HuaweiWearableHelper implements HomeActivity.ActivitiCallback{
    /**
     * 用于获取所有可穿戴数据
     */
    public HuaweiWearableManager manager;
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
    public Handler handler;
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
     * 得到的时间段健康数据
     */
    public static final int TIME_HEALTH = 3;
    /**
     * 获得任务数据完成标识
     */
    public static final int MISSION_DATA = 4;
    /**
     * 是否在读取月数据，用于控制manager在读取成功一个之后再读取下一个
     */
    public boolean gettingTimeHealth = false;
    /**
     * 数据管理器
     */
    private DataManager dataManager;
    /**
     * 获取设备连接状态
     */
    public int getDeviceStatus(){
        deviceStatus=manager.getConnectStatus(DeviceType.HUAWEI_TALKBAND_B2);
        Log.i("获取设备状态", "状态为 " + deviceStatus);
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
        manager.getHealthDataCurrentDay(DeviceType.HUAWEI_TALKBAND_B2, new IResultReportCallback() {
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

    public void cleanMonthData(){
        ArrayList<String> list = getMonthEachDayTime();
        for(int i = 0;i<30;i++) {
            String date = list.get(i + 1);
            dataManager.cleanMonthData(date);
        }
    }

    /**
     * 获取上个月同一天开始的DataHealthData数组，共30个数据
     */
    public void getMonthHealthData(){
        ArrayList<String> list = getMonthEachDayTime();
        for(int i = 0;i<30;i++){
            Log.i("get",i+"");
            final int j = i;
            final String date = list.get(i+1);
            while (gettingTimeHealth) /*Log.i("getting","getting")*/;
            if(dataManager.getMonthData(date)!=-1){
                Message msg = handler.obtainMessage(TIME_HEALTH, j, 1, dataManager.getMonthData(date));
                handler.handleMessage(msg);
                Log.i("onSucess", "读取指定时间段数据成功:" + dataManager.getMonthData(date) );
                continue;
            }
            manager.getHealthDataByTime(DeviceType.HUAWEI_TALKBAND_B2, list.get(i+1),list.get(i), new IResultReportCallback() {
                @Override
                public void onSuccess(Object object) {
                    //得到数据
                    DataHealthData data = (DataHealthData) object;
                    ArrayList<DataRawSportData> dataList = (ArrayList<DataRawSportData>) data.getDataRawSportDatas();
                    int cal = 0;
                    if(dataList.size()!=0)
                        cal = dataList.get(dataList.size()-1).getTotalCalorie();
                    //存储
                    dataManager.saveMonthData(date,cal);
                    if(cal!=0){
                    //添加到日均卡路里计算数据
                        dataManager.saveAverageCal(dataManager.getTotalCal()+cal,dataManager.getTotalDays()+1);
                    }
                    Message msg = handler.obtainMessage(TIME_HEALTH, j, 0, cal);
                    handler.handleMessage(msg);
                    Log.i("onSucess", "获取指定时间段数据成功:" + cal);
                    gettingTimeHealth = false;
                }

                @Override
                public void onFailure(int err_code, String err_msg) {
                    Log.i("onFailure", "获取指定时间段数据失败!");
                    gettingTimeHealth = false;
                }

            });
            gettingTimeHealth = true;
        }
    }

    /**
     * 获取上个月每天零点的时间，长度为31，用来获取30组DataHealthData数据，其中第0位为今天凌晨的时间，
     */
    private ArrayList<String> getMonthEachDayTime(){
        ArrayList <String> list= new ArrayList<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd000000");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        for(int i = 30 ; i >= 0 ; i-- ){
            String time = format.format(c.getTime());
            Log.i("获取的日期",time);
            list.add(time);
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        return list;
    }

    /**
     * 获取任务进度条数据
     */
    public void getMissionProgress(){
        ArrayList<String> list = getMissionEachDayTime();
        dataManager.cleanReachDays();
        for(int i = 0;i<list.size()-1;i++){
            final int j = i;
            final String date = list.get(i+1);
            while (gettingTimeHealth) Log.i("getting", "getting");
            if(dataManager.getMonthData(date)!=-1){
                if( dataManager.getMonthData(date)/1000>=dataManager.getMissonData(DataManager.DAILY_GOAL)) {
                    dataManager.addReachDays();
                    Log.i("onSucess", "任务达标天数+1 " + dataManager.getMonthData(date) / 1000);
                }
                continue;
            }
            manager.getHealthDataByTime(DeviceType.HUAWEI_TALKBAND_B2, list.get(i + 1), list.get(i), new IResultReportCallback() {
                @Override
                public void onSuccess(Object object) {
                    //得到数据
                    DataHealthData data = (DataHealthData) object;
                    ArrayList<DataRawSportData> dataList = (ArrayList<DataRawSportData>) data.getDataRawSportDatas();
                    int cal = 0;
                    if (dataList.size() != 0)
                        cal = dataList.get(dataList.size() - 1).getTotalCalorie();
                    //存储
                    dataManager.saveMonthData(date, cal);
                    if (cal / 1000 >= dataManager.getMissonData(DataManager.DAILY_GOAL)) {
                        dataManager.addReachDays();
                        Log.i("onSucess", "任务达标天数+1 " + dataManager.getMonthData(date) / 1000);
                    }
                    gettingTimeHealth = false;
                }

                @Override
                public void onFailure(int err_code, String err_msg) {
                    Log.i("onFailure", "获取任务达标天数失败!");
                    gettingTimeHealth = false;
                }
            });
            gettingTimeHealth = true;
        }
        //获取任务进度数据完毕，通知绘制
        Log.i("获取任务达标天数", "获取完毕");
        Message msg = handler.obtainMessage(MISSION_DATA);
        handler.handleMessage(msg);
    }

    /**
     * 获取任务开始以来每天零点的时间，其中第0位为今天凌晨的时间，
     */
    private ArrayList<String> getMissionEachDayTime(){
        ArrayList <String> list= new ArrayList<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd000000");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        int comleteDays = dataManager.getMissonData(DataManager.COMPLETE_DAYS);
        for(int i = comleteDays ; i >= 0 ; i-- ){
            String time = format.format(c.getTime());
            Log.i("获取的日期",time);
            list.add(time);
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        return list;
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
    public void onActivityStarted(Context context, Handler handler){
        //获取HuaweiWearableManager单一实例
        manager = HuaweiWearableManager.getInstance(context);
        if(manager!=null) {
            //设备连接状态回调类的实例
            deviceConnectStatusCallback = new MyDeviceConnectStatusCallback();
            //向manager注册设备连接状态回调类
            manager.registerConnectStateCallback(deviceConnectStatusCallback);
            //获得HomeActivity的Handler
            this.handler = handler;
            //获得数据管理器单一实例
            dataManager = DataManager.getInstance(context);
        }
    }

}
