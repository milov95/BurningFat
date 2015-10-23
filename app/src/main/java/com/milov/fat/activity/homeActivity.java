package com.milov.fat.activity;

import android.app.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.huawei.huaweiwearable.constant.DeviceConnectionState;
import com.huawei.huaweiwearable.data.DataTodayTotalMotion;
import com.huawei.huaweiwearable.data.DataTotalMotion;
import com.milov.fat.R;
import com.milov.fat.fragment.FirstSetFragment;
import com.milov.fat.fragment.HomeFragment;
import com.milov.fat.fragment.MissionFragment;
import com.milov.fat.fragment.PersonalFragment;
import com.milov.fat.util.DataManager;
import com.milov.fat.util.DisplayUtil;
import com.milov.fat.util.HuaweiWearableHelper;
import com.milov.fat.view.MissionProgressView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends Activity implements HomeFragment.HomeFragClickListener,
        PersonalFragment.PersonFragClickListener,MissionFragment.MissionFragClickListener,
        FirstSetFragment.FirstSelfInfoSetFragClickListener {

    /**
     * 首页
     */
    private HomeFragment homeFragment;
    /**
     * 第一次打开应用的设置页
     */
    private FirstSetFragment firsrSetFragment;
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
    /**
     * 数据管理器
     */
    private DataManager dataManager;
    /**
     * 微信开放平台注册的该应用的ID
     */
    private static final String APP_ID = "wx4b19dc736e58e5e7" ;
    /**
     * 微信openAPI接口
     */
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        regToWeChat();

        dataManager = DataManager.getInstance(getApplicationContext());
        handler = new MyHandler();

        //实例化HuaweiWearleHelper工具类
        if(huawei == null )
            huawei = new HuaweiWearableHelper();

        //Activity在旋屏或从后台切回时有时会重新启动，这时原有的Fragment也会重启，同时又会执行一遍onCreat
        //原来的Fragment会存储在savedInstanceState里，为了避免过多的Fragment产生，这里执行一次判断
        //一次启动程序，或者启动后还未设置个人信息时，打开firstSetFragment
        if(savedInstanceState == null && dataManager.getSelfData(DataManager.GENDER)==0){
            firsrSetFragment = new FirstSetFragment();
            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.home_content, firsrSetFragment, null);
            fragmentTransaction.commit();
        }
        //否则直接进入主页
        else if(savedInstanceState == null){
            homeFragment = new HomeFragment();
            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.home_content, homeFragment, null);
            fragmentTransaction.commit();

        }

    }

    @Override
    public void onStart(){
        super.onStart();

        if(firsrSetFragment!=null) return;

        //向HuaweiWearableHelper类传递Context
        huawei.onActivityStarted(this, handler);

    }

    @Override
    public void onResume(){
        super.onResume();

        if(firsrSetFragment!=null) return ;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 500);
    }

    @Override
    public void onFirstSelfInfoSetFragClick(View view){
        switch (view.getId()){
            case R.id.first_set_start:
                huawei.cleanMonthData();
                //进入HomeFragment
                homeFragment = new HomeFragment();
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.home_content, homeFragment)
                        .commit();

                firsrSetFragment=null;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (huawei.manager != null) {
                            loadMissionProgress();
                        }
                        refresh();
                    }
                }, 500);
        }
    }

    @Override
    public void onHomeFragClick(View view){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()){
            //打开PersonalFragment并加入回退栈
            case R.id.personal_TextView:
                //判断手环是否正常连接
                if (huawei.manager==null || huawei.getDeviceStatus() != DeviceConnectionState.DEVICE_CONNECTED){
                    Toast.makeText(this,"请确保手环正常连接",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(personalFragment == null){
                    personalFragment = new PersonalFragment();
                }
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.home_content, personalFragment, null);
                fragmentTransaction.hide(homeFragment);
                fragmentTransaction.addToBackStack(null);
                loadLineChart();
                break;
            //打开MissionFragment并加入回退栈
            case R.id.mission_TextView:
                //判断手环是否正常连接
                if (huawei.manager==null || huawei.getDeviceStatus() != DeviceConnectionState.DEVICE_CONNECTED){
                    Toast.makeText(this,"请确保手环正常连接",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(missionFragment == null){
                    missionFragment = new MissionFragment();
                }
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.home_content, missionFragment, null);
                fragmentTransaction.hide(homeFragment);
                fragmentTransaction.addToBackStack(null);
                break;
            //打开华为穿戴APP
            case R.id.openApp_button:
                if(huawei.manager==null){
                    huawei.onActivityStarted(this,handler);
                    break;
                }
                openApp();
                break;
            //刷新
            case R.id.refresh_image:
                if(huawei.manager ==null || huawei.getDeviceStatus() != DeviceConnectionState.DEVICE_CONNECTED){
                    Toast.makeText(getApplicationContext(),"请确保手环正常连接",Toast.LENGTH_SHORT).show();
                }
                refresh();
                break;
            case R.id.share_image:
                if(api!=null){
                    if(api.isWXAppInstalled()){
                        if(api.isWXAppSupportAPI()){
                            shareToWeChat("正在测试BurningFat朋友圈分享接口");
                        } else {
                            Toast.makeText(this,"微信版本太低，无法分享倒朋友圈",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this,"您还未安装微信，无法分享到朋友圈",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this,"注册api失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        fragmentTransaction.commit();

    }

    @Override
    public void onPersonFragClick(View view){
        switch (view.getId()){
            //回到HomeFragment
            case R.id.back_personal_image:
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .remove(personalFragment)
                        .show(homeFragment)
                        .commit();
                break;
            case R.id.reset_personal_text:
                firsrSetFragment = new FirstSetFragment();
                fragmentManager
                        .beginTransaction()
                        .remove(personalFragment)
                        .remove(homeFragment)
                        .add(R.id.home_content,firsrSetFragment,null)
                        .commit();
                break;
        }
    }

    @Override
    public void onMissionFragClick(View view){
        switch (view.getId()){
            //回到HomeFragment
            case R.id.back_mission_image:
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .remove(missionFragment)
                        .show(homeFragment)
                        .commit();
                break;
            //刷新
            case R.id.add_mission_button:
            case R.id.reset_mission_button:
            case R.id.delete_mission_button:
                refresh();
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
                        case HuaweiWearableHelper.TODAY_HEALTH:
                            if (msg.obj == null) /*失败*/ ;
                            else {
                                setTodayHealthData(msg.obj);
                            }
                            break;
                        case HuaweiWearableHelper.TIME_HEALTH:
                            if (msg.obj == null)
                                Toast.makeText(getApplicationContext(), "获取月数据失败", Toast.LENGTH_SHORT).show();
                            else {
                                if (msg.arg2 == 1) {
                                    //读取存储的健康数据
                                    Log.i("msg.obj", "读取存储的数据");
                                    personalFragment.chartView.loadHealthData(msg.arg1, (int) msg.obj);
                                    if (msg.arg1 == 29) {
                                        //读取月数据完毕
                                        int averageCal = dataManager.getAverageCal() / 1000;
                                        personalFragment.averageCal.setText(averageCal + "千卡");
                                        if(averageCal>800)
                                            personalFragment.assess.setText("运动爱好者");
                                        else if (averageCal>400)
                                            personalFragment.assess.setText("表现平庸");
                                        else
                                            personalFragment.assess.setText("缺乏锻炼");
                                        personalFragment.assess.invalidate();
                                        personalFragment.statusAnim.stop();
                                        personalFragment.status.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    //读取设备的健康数据
                                    Log.i("msg.obj", "读取设备的数据");
                                    personalFragment.chartView.loadHealthData(msg.arg1, (int) msg.obj);
                                    if (msg.arg1 == 29) {
                                        //读取月数据完毕
                                        personalFragment.averageCal.setText(dataManager.getAverageCal()/1000+"千卡");
                                        personalFragment.statusAnim.stop();
                                        personalFragment.status.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            break;
                        case HuaweiWearableHelper.MISSION_DATA:
                            Toast.makeText(getApplicationContext(),"数据同步成功",Toast.LENGTH_SHORT).show();
                            homeFragment.progressView.loadMissionData(
                                    dataManager.getMissonData(DataManager.PERIOD)
                                    , dataManager.getMissonData(DataManager.COMPLETE_DAYS)
                                    , dataManager.getMissonData(DataManager.REACH_DAYS));
                            homeFragment.rotateAnim.cancel();

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
        if(calorie>=dataManager.getMissonData(DataManager.DAILY_GOAL)){
            homeFragment.completeLayout.setVisibility(View.VISIBLE);
            homeFragment.calStillText.setVisibility(View.GONE);
            homeFragment.unitText.setVisibility(View.GONE);
        }
        else{
            homeFragment.completeLayout.setVisibility(View.GONE);
            homeFragment.calStillText.setText(dataManager.getMissonData(DataManager.DAILY_GOAL) - calorie + "");
            homeFragment.unitText.setVisibility(View.VISIBLE);
        }
        homeFragment.stepText.setText(steps + " 步");
        homeFragment.calText.setText(calorie + " 千卡");
    }

//    /**
//     * （handler内使用）设备连接状态改变时调用
//     * @param obj 存储DeviceConnectionState
//     */
//    private void showChangedStatus(Object obj){
//        int status = (int) obj;
//        switch (status){
//            case DeviceConnectionState.DEVICE_CONNECTED:
//                homeFragment.deviceStatusText.setText("设备已连接");
//                huawei.getTodayHealthData();
//                showFailed(false);
//                showOpenApp(false);
//                showTodayHealthData(true);
//                break;
//            case DeviceConnectionState.DEVICE_CONNECT_FAILED:
//                homeFragment.deviceStatusText.setText("设备连接失败");
//                showFailed(true);
//                showOpenApp(false);
//                showTodayHealthData(false);
//                break;
//            case DeviceConnectionState.DEVICE_CONNECTING:
//                homeFragment.deviceStatusText.setText("正在连接");
//                Toast.makeText(getApplicationContext(),"正在连接...",Toast.LENGTH_SHORT).show();
//                break;
//            case DeviceConnectionState.DEVICE_DISCONNECTED:
//                homeFragment.deviceStatusText.setText("设备未连接");
//                showFailed(false);
//                showOpenApp(true);
//                showTodayHealthData(false);
//                break;
//            case DeviceConnectionState.DEVICE_DISCONNECTING:
//                homeFragment.deviceStatusText.setText("正在断开连接");
//                Toast.makeText(getApplicationContext(), "正在断开连接...", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }

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
            homeFragment.openAppText.requestFocus();
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
            if(dataManager.getSelfData(DataManager.GOAL)!=-2){
                homeFragment.progressView.setVisibility(View.VISIBLE);
                homeFragment.noMission.setVisibility(View.GONE);
            } else {
                homeFragment.progressView.setVisibility(View.GONE);
                homeFragment.noMission.setVisibility(View.VISIBLE);
            }
        } else {
            homeFragment.calStillText.setVisibility(View.GONE);
            homeFragment.unitText.setVisibility(View.GONE);
            homeFragment.manBar.setVisibility(View.GONE);
            homeFragment.progressView.setVisibility(View.GONE);
            homeFragment.completeLayout.setVisibility(View.GONE);
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
        if(huawei.manager==null){
            showFailed(false);
            showOpenApp(true);
            showTodayHealthData(false);
        } else if (huawei.getDeviceStatus() != DeviceConnectionState.DEVICE_CONNECTED) {
                showFailed(false);
                showOpenApp(true);
                showTodayHealthData(false);
        } else {
            showFailed(false);
            showOpenApp(false);
            showTodayHealthData(true);
            huawei.getTodayHealthData();
            loadMissionProgress();
            homeFragment.refreshImage.startAnimation(homeFragment.rotateAnim);

        }
    }
    /**
     * 配置PersonFragment里的折线图
     */
    private void loadLineChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                huawei.getMonthHealthData();
            }
        }).start();
    }

    /**
     * 加载任务进度条
     */
    private void loadMissionProgress(){
        //-2表示任务已经被删除
        if(dataManager.getSelfData(DataManager.GOAL)!=-2){
            homeFragment.progressView.setVisibility(View.VISIBLE);
            new Thread(){
                @Override
                public void run(){
                    huawei.getMissionProgress();
                }
            }.start();
        }
        else {
            homeFragment.progressView.setVisibility(View.GONE);
        }
    }

    /**
     * 注册到微信
     */
    public void regToWeChat(){
        api = WXAPIFactory.createWXAPI(this,APP_ID,false);
        api.registerApp(APP_ID);
    }

    /**
     * 设置食谱数据
     */
    public void setRecipeData(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if( hour > 21 && hour < 0) {
            homeFragment.recipeImage.setImageResource(R.drawable.breakfast_1);
            homeFragment.sunImage.setImageResource(R.drawable.morning);
            homeFragment.recipeTypeText.setText("明天的早餐");
            homeFragment.recipeContent.setText(dataManager.getRecipeData(DataManager.BREAKFAST, DataManager.RECIPE));
            homeFragment.recipeScience.setText(dataManager.getRecipeData(DataManager.BREAKFAST, DataManager.SCIENCE));
        } else if( hour >= 0 && hour < 9){
            homeFragment.recipeImage.setImageResource(R.drawable.breakfast_1);
            homeFragment.sunImage.setImageResource(R.drawable.morning);
            homeFragment.recipeTypeText.setText("早餐");
            homeFragment.recipeContent.setText(dataManager.getRecipeData(DataManager.BREAKFAST, DataManager.RECIPE));
            homeFragment.recipeScience.setText(dataManager.getRecipeData(DataManager.BREAKFAST, DataManager.SCIENCE));
        } else if( hour >= 9 && hour < 14) {
            homeFragment.recipeImage.setImageResource(R.drawable.lunch_1);
            homeFragment.sunImage.setImageResource(R.drawable.noon);
            homeFragment.recipeTypeText.setText("午餐");
            homeFragment.recipeContent.setText(dataManager.getRecipeData(DataManager.LUNCH, DataManager.RECIPE));
            homeFragment.recipeScience.setText(dataManager.getRecipeData(DataManager.LUNCH, DataManager.SCIENCE));
        }
        else {
            homeFragment.recipeImage.setImageResource(R.drawable.supper_1);
            homeFragment.sunImage.setImageResource(R.drawable.afternoon);
            homeFragment.recipeTypeText.setText("晚餐");
            homeFragment.recipeContent.setText(dataManager.getRecipeData(DataManager.SUPPER, DataManager.RECIPE));
            homeFragment.recipeScience.setText(dataManager.getRecipeData(DataManager.SUPPER, DataManager.SCIENCE));
        }
    }

    /**
     * 分享至微信
     * @param text
     */
    private void shareToWeChat(String text){

        String s = "0";
        if(homeFragment.calText.getText().length()>3) {
            s = (String) homeFragment.calText.getText().subSequence(0,homeFragment.calText.getText().length() - 3);
        }

        int deltaCal = Integer.parseInt(s) - dataManager.getNormalCal();


        int remainDays =dataManager.getMissonData(DataManager.PERIOD)- dataManager.getMissonData(DataManager.COMPLETE_DAYS);

        Bitmap pic =  deltaCal > 0
                ? BitmapFactory.decodeResource(getResources(), R.drawable.shared_more)
                : BitmapFactory.decodeResource(getResources(), R.drawable.shared_less);

        Bitmap sharedPic = drawTextToBitmap(pic,String.valueOf(deltaCal) ,String.valueOf(remainDays));

        Bitmap thumbPic = Bitmap.createScaledBitmap(sharedPic, 150, 150, true);

        WXImageObject imageObject = new WXImageObject(sharedPic);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;
        msg.thumbData = bmpToByteArray(thumbPic);
        msg.description = text;
        msg.title = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        req.message = msg;

        api.sendReq(req);
    }

    /**
     * 在图片上写文字
     * @param bm 要分享的背景图片
     * @param gText 比正普通人多消耗的卡路里
     * @param gText2 任务剩余天数
     * @return
     */
    private Bitmap drawTextToBitmap(Bitmap bm,String gText,String gText2) {
        float scale = DisplayUtil.getScale(getApplicationContext());

        android.graphics.Bitmap.Config bitmapConfig = bm.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        Bitmap bitmap = bm.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(getResources().getColor(R.color.share_green));
        paint1.setTextSize((int) (28 * scale));
        paint2.setColor(getResources().getColor(R.color.share_green));
        paint2.setTextSize((int) (28 * scale));

        Rect bounds1 = new Rect();
        paint1.getTextBounds(gText, 0, gText.length(), bounds1);
        Rect bounds2 = new Rect();
        paint2.getTextBounds(gText2, 0, gText2.length(), bounds2);


        int x1 = (int)(bitmap.getWidth()*0.638375 - bounds1.width()/2);
        int y1 = (int)(bitmap.getHeight()*0.52185  + bounds1.height()/2);

        int x2 = (int)(bitmap.getWidth()*0.64335 - bounds2.width()/2);
        int y2 = (int)(bitmap.getHeight()*0.5853375 + bounds2.height()/2);

        canvas.drawText(gText, x1 , y1 , paint1);
        canvas.drawText(gText2, x2, y2, paint2);

        return bitmap;
    }

    /**
     * 得到Bitmap的byte
     * @author YOLANDA
     * @param bmp
     * @return
     */
    private static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 用于传递HomeActivity的Context和handler的回调接口
     */
    public interface ActivitiCallback{
        void onActivityStarted(Context context, Handler handler);
    }
}
