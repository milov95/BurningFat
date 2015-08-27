package com.milov.fat.activity;

import android.app.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.huawei.huaweiwearable.constant.DeviceConnectionState;
import com.huawei.huaweiwearable.data.DataTodayTotalMotion;
import com.huawei.huaweiwearable.data.DataTotalMotion;
import com.milov.fat.R;
import com.milov.fat.fragment.HomeFragment;
import com.milov.fat.fragment.MissionFragment;
import com.milov.fat.fragment.PersonalFragment;
import com.milov.fat.util.HuaweiWearableHelper;

import java.util.List;

public class HomeActivity extends Activity implements HomeFragment.HomeFragClickListener,
        PersonalFragment.PersonFragClickListener,MissionFragment.MissionFragClickListener{

    /**
     * 首页
     */
    private HomeFragment homeFragment;
    /**
     * 个人中心
     */
    private PersonalFragment personalFragment;
    /**
     * 任务中心
     */
    private MissionFragment missionFragment;
    /**
     * 用于管理Fragment
     */
    private FragmentManager fragmentManager;
    /**
     * 华为工具类的实例，用于获取可穿戴数据
     */
    private HuaweiWearableHelper huawei;
    /**
     * 用于响应HuaweiWearableHelper状态回调的Handler
     */
    public MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        handler = new MyHandler();

        //加载HomeFragment
        //Activity在旋屏或从后台切回时有时会重新启动，这时原有的Fragment也会重启，同时又会执行一遍onCreat
        //原来的Fragment会存储在savedInstanceState里，为了避免过多的Fragment产生，这里执行一次判断
        if(savedInstanceState == null){
            homeFragment = new HomeFragment();
            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.home_content,homeFragment,null);
            fragmentTransaction.commit();
        }
        //实例化HuaweiWearleHelper工具类
        if(huawei == null )
            huawei = new HuaweiWearableHelper();
        //向HuaweiWearableHelper类传递Context
        huawei.onActivityCreated(this, handler);

    }

    @Override
    public void onStart(){
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                homeFragment.deviceStatusText.setText(huawei.getDeviceStatus() + "");
                refresh();
            }
        }, 500);
    }

    @Override
    public void onHomeFragClick(View view){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()){
            //打开PersonalFragment并加入回退栈
            case R.id.personal_TextView:
                if(personalFragment == null){
                    personalFragment = new PersonalFragment();
                }
                fragmentTransaction.hide(homeFragment);
                fragmentTransaction.add(R.id.home_content,personalFragment,null);
                fragmentTransaction.addToBackStack(null);
                break;
            //打开MissionFragment并加入回退栈
            case R.id.mission_TextView:
                if(missionFragment == null){
                    missionFragment = new MissionFragment();
                }
                fragmentTransaction.hide(homeFragment);
                fragmentTransaction.add(R.id.home_content, missionFragment, null);
                fragmentTransaction.addToBackStack(null);
                break;
            //打开华为穿戴APP
            case R.id.openApp_button:
                openApp();
                break;
            //刷新
            case R.id.refresh_image:
                if(huawei.getDeviceStatus() != DeviceConnectionState.DEVICE_CONNECTED){
                    Toast.makeText(getApplicationContext(),"设备连接失败",Toast.LENGTH_SHORT).show();
                }
                refresh();
        }
        fragmentTransaction.commit();

    }

    @Override
    public void onPersonFragClick(View view){
        switch (view.getId()){
            //回到HomeFragment
            case R.id.back_personal_image:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(personalFragment);
                fragmentTransaction.show(homeFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onMissionFragClick(View view){
        switch (view.getId()){
            //回到HomeFragment
            case R.id.back_mission_image:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(missionFragment);
                fragmentTransaction.show(homeFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    /**
     * 处理HuawerWearableHelper状态回调数据
     */
    public class MyHandler extends Handler {
        public MyHandler() {
            super();
        }

        @Override
        public void handleMessage(Message message) {
            final Message msg = message;
            //我也不知道这TM是什么鬼导致我的handler成了非主线程的了！！！
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (msg.what) {
                        case HuaweiWearableHelper.CONNECTION_STATUS:
                            showChangedStatus(msg.obj);
                            break;
                        case HuaweiWearableHelper.TODAY_HEALTH:
                            setTodayHealthData(msg.obj);
                            break;
                        case HuaweiWearableHelper.USER_INFO:
                            break;
                    }
                }
            });
        }
    }

    /**
     * （handler内使用）获取DataTodayTotalMotion成功后调用
     * @param obj 存储DataTodayTotalMotion
     */
    private void setTodayHealthData(Object obj){
        int steps = 0, calorie = 0;
        if (obj != null) {
            calorie = ((DataTodayTotalMotion) obj).getTotalCalorie();
            List<DataTotalMotion> list = ((DataTodayTotalMotion) obj).getDataTotalMotions();
            for (DataTotalMotion motion : list) {
                steps += motion.getStep();
            }
        }
        homeFragment.calStillText.setText(calorie + "");
        homeFragment.stepText.setText(steps+"步");
        homeFragment.calText.setText(calorie+"千卡");
        Toast.makeText(getApplicationContext(),"数据同步成功",Toast.LENGTH_SHORT).show();
    }

    /**
     * （handler内使用）设备连接状态改变时调用
     * @param obj 存储DeviceConnectionState
     */
    private void showChangedStatus(Object obj){
        int status = (int) obj;
        switch (status){
            case DeviceConnectionState.DEVICE_CONNECTED:
                homeFragment.deviceStatusText.setText("设备已连接");
                huawei.getTodayHealthData();
                showFailed(false);
                showOpenApp(false);
                showTodayHealthData(true);
                break;
            case DeviceConnectionState.DEVICE_CONNECT_FAILED:
                homeFragment.deviceStatusText.setText("设备连接失败");
                showFailed(true);
                showOpenApp(false);
                showTodayHealthData(false);
                break;
            case DeviceConnectionState.DEVICE_CONNECTING:
                homeFragment.deviceStatusText.setText("正在连接");
                Toast.makeText(getApplicationContext(),"正在连接...",Toast.LENGTH_SHORT).show();
                break;
            case DeviceConnectionState.DEVICE_DISCONNECTED:
                homeFragment.deviceStatusText.setText("设备未连接");
                showFailed(false);
                showOpenApp(true);
                showTodayHealthData(false);
                break;
            case DeviceConnectionState.DEVICE_DISCONNECTING:
                homeFragment.deviceStatusText.setText("正在断开连接");
                Toast.makeText(getApplicationContext(), "正在断开连接...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 显示连接失败的提示
     * @param gonnaShow
     */
    private void showFailed(boolean gonnaShow){
        if(gonnaShow){
            homeFragment.failedText.setVisibility(View.VISIBLE);
        } else {
            homeFragment.failedText.setVisibility(View.GONE);
        }
    }
    /**
     * 显示打开华为穿戴App的提示
     * @param gonnaShow
     */
    private void showOpenApp(boolean gonnaShow){
        if(gonnaShow){
            homeFragment.openAppText.setVisibility(View.VISIBLE);
            homeFragment.openAppButton.setVisibility(View.VISIBLE);
        } else {
            homeFragment.openAppText.setVisibility(View.GONE);
            homeFragment.openAppButton.setVisibility(View.GONE);
        }
    }
    /**
     * 显示主页运动数据
     * @param gonnaShow
     */
    private void showTodayHealthData(boolean gonnaShow){
        if(gonnaShow){
            homeFragment.calStillText.setVisibility(View.VISIBLE);
            homeFragment.unitText.setVisibility(View.VISIBLE);
            homeFragment.manBar.setVisibility(View.VISIBLE);
        } else {
            homeFragment.calStillText.setVisibility(View.GONE);
            homeFragment.unitText.setVisibility(View.GONE);
            homeFragment.manBar.setVisibility(View.GONE);
        }
    }

    /**
     * 打开华为穿戴APP
     */
    private void openApp(){
        try {
            PackageManager packageManager = getPackageManager();
            Intent intent= new Intent();
            intent = packageManager.getLaunchIntentForPackage("com.huawei.bone");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"您还没有安装华为穿戴APP",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 刷新
     */
    public void refresh(){
        if (huawei.getDeviceStatus() != DeviceConnectionState.DEVICE_CONNECTED) {
            if (huawei.getDeviceStatus() == DeviceConnectionState.DEVICE_CONNECT_FAILED) {
                showFailed(true);
                showOpenApp(false);
                showTodayHealthData(false);
            } else {
                showFailed(false);
                showOpenApp(true);
                showTodayHealthData(false);
            }
        } else {
            showFailed(false);
            showOpenApp(false);
            showTodayHealthData(true);
            huawei.getTodayHealthData();
        }
    }
    /**
     * 用于传递HomeActivity的Context和handler的回调接口
     */
    public interface ActivitiCallback{
        void onActivityCreated(Context context,Handler handler);
    }
}
