package com.milov.fat.fragment;


import android.animation.AnimatorInflater;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.percent.PercentRelativeLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.milov.fat.Animation.HomeAnim;
import com.milov.fat.R;
import com.milov.fat.view.ActionBarLayout;
import com.milov.fat.view.HomeCurveView;
import com.milov.fat.view.MissionProgressView;

/**
 * Created by Milov on 2015/8/22.
 */
public class HomeFragment extends Fragment implements OnClickListener,View.OnTouchListener {
    private PercentRelativeLayout view;
    private TextView personalText,missionText,recipeText,breakfastText,lunchText,supperText;
    public TextView circleText,deviceStatusText,openAppText,openAppButton,calStillText,unitText,failedText,calText,stepText;
    public LinearLayout manBar,breakfastLayout,lunchLayout,supperLayout,morningTitle,noonTitle,afternoonTitle;
    public RelativeLayout bottomLayout;
    public Handler handler;
    public HomeAnim homeAnim;
    public HomeCurveView curveView;
    public MissionProgressView progressView;
    private ActionBarLayout recipeBar;
    private float startX,startY;
    private boolean hasCancle,isHome;
    public ImageView shareImage,refreshImage;
    private LayoutTransition disApperTransition,apperTransition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = ( PercentRelativeLayout) inflater.inflate(R.layout.home_fragment_layout,container,false);
        initView();
        initEvent();
        initLayoutAnim();

        isHome = true ;
        handler = new Handler(Looper.getMainLooper());
        homeAnim = new HomeAnim(circleText,curveView);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof HomeFragClickListener ){
            ((HomeFragClickListener) getActivity()).onHomeFragClick(v);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                down(v,event);
                break;
            case MotionEvent.ACTION_MOVE:
                move(v,event);
                break;
            case MotionEvent.ACTION_UP:
                up(v,event);
                break;
        }
        return true;
    }
    /**
     * ACTION_DOWN触发的方法，记录起始坐标
     */
    private void down(View v,MotionEvent event){
        hasCancle = false ;
        startX = event.getRawX();
        startY = event.getRawY();
        circleSelected(v, true);
    }
    /**
     * ACTION_MOVE触发的方法，判断是否cancle
     */
    private void move(View v,MotionEvent event){
        if(Math.abs(event.getRawX() - startX)>100 || Math.abs(event.getRawY() - startY)>100 ){
            if(!hasCancle){
                circleSelected(v, false);
                hasCancle = true;
            }
        }
    }
    /**
     * ACTION_UP触发的方法，判断是否加载recipe视图
     */
    private void up(View v,MotionEvent event){
        if(!hasCancle){
            homeAnim.startCurveAnim2(true);
            hideHomeView();
            circleText.setEnabled(false);
            //在执行LayoutTransition动画时会闪烁，所以先让他的透明度为零
            recipeText.setAlpha(0);
            recipeText.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isHome = false;
                    curveView.setEnabled(true);
                    showRecipe(true);
                }
            }, 1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRecipe(true);
                }
            },200);
        }
    }

    /**
     * 播放主页跟随circle按钮变化的动画
     * @param view
     * @param isSelected
     */
    private void circleSelected(View view, boolean isSelected){
        if(isSelected){
            homeAnim.startCircleAnim(false);
            homeAnim.startCurveAnim1(true);
        } else {
            homeAnim.startCircleAnim(true);
            homeAnim.startCurveAnim1(false);
        }
    }

    /**
     * 初始化视图
     */
    private void initView(){
        personalText = (TextView) view.findViewById(R.id.personal_TextView);
        missionText = (TextView) view.findViewById(R.id.mission_TextView);
        circleText = (TextView) view.findViewById(R.id.circle_TextView);
        deviceStatusText = (TextView) view.findViewById(R.id.test_device_state);
        openAppText = (TextView) view.findViewById(R.id.openApp_text);
        openAppButton = (Button) view.findViewById(R.id.openApp_button);
        calStillText = (TextView) view.findViewById(R.id.cal_still_text);
        unitText = (TextView) view .findViewById(R.id.unit_text);
        failedText = (TextView) view.findViewById(R.id.failed_text);
        manBar = (LinearLayout) view.findViewById(R.id.man_bar);
        calText = (TextView) view.findViewById(R.id.cal_text);
        stepText = (TextView) view.findViewById(R.id.step_text);
        curveView = (HomeCurveView) view.findViewById(R.id.homeCurveView);
        refreshImage = (ImageView) view.findViewById(R.id.refresh_image);
        bottomLayout = (RelativeLayout) view.findViewById(R.id.home_bottom_layout);
        recipeBar = (ActionBarLayout) view.findViewById(R.id.recipe_bar);
        recipeText = (TextView) view.findViewById(R.id.recipe_text);
        breakfastLayout = (LinearLayout) view.findViewById(R.id.breakfast_layout);
        lunchLayout = (LinearLayout) view.findViewById(R.id.lunch_layout);
        supperLayout = (LinearLayout) view.findViewById(R.id.supper_layout);
        morningTitle = (LinearLayout) view.findViewById(R.id.morning_title);
        noonTitle = (LinearLayout) view.findViewById(R.id.noon_title);
        afternoonTitle = (LinearLayout) view.findViewById(R.id.afternoon_title);
        breakfastText = (TextView) view.findViewById(R.id.breakfast_text);
        lunchText = (TextView) view.findViewById(R.id.lunch_text);
        supperText = (TextView) view.findViewById(R.id.supper_text);
        progressView = (MissionProgressView) view.findViewById(R.id.mission_progress);
    }
    /**
     * 初始化事件
     */
    private void initEvent(){
        personalText.setOnClickListener(this);
        missionText.setOnClickListener(this);
        openAppButton.setOnClickListener(this);
        refreshImage.setOnClickListener(this);
        circleText.setOnTouchListener(this);
        curveView.setOnTouchListener(new backTouchListener());
    }
    /**
     * 初始化布局动画
     */
    private void initLayoutAnim(){

        PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        PropertyValuesHolder alphaDown = PropertyValuesHolder.ofFloat("alpha", 1, 0);
        ObjectAnimator apperAnim = ObjectAnimator.ofPropertyValuesHolder(this, alphaUp ).setDuration(500);
        ObjectAnimator disapperAnim = ObjectAnimator.ofPropertyValuesHolder(this, alphaDown ).setDuration(500);

        disApperTransition = new LayoutTransition();
        disApperTransition.setAnimator(LayoutTransition.DISAPPEARING, disapperAnim);
        apperTransition = new LayoutTransition();
        apperTransition.setAnimator(LayoutTransition.APPEARING, apperAnim);

        recipeBar.setLayoutTransition(disApperTransition);
        bottomLayout.setLayoutTransition(disApperTransition);
        bottomLayout.setLayoutTransition(apperTransition);
        recipeBar.setLayoutTransition(apperTransition);
        breakfastLayout.setLayoutTransition(apperTransition);
        lunchLayout.setLayoutTransition(apperTransition);
        supperLayout.setLayoutTransition(apperTransition);
    }
    /**
     * 隐藏视图
     */
    private void hideHomeView(){
        circleText.setVisibility(View.GONE);
        personalText.setVisibility(View.GONE);
        missionText.setVisibility(View.GONE);
    }
    /**
     * 显示视图
     */
    public void showHomeView(){
        personalText.setAlpha(0);
        missionText.setAlpha(0);
        personalText.setVisibility(View.VISIBLE);
        missionText.setVisibility(View.VISIBLE);
    }
    /**
     * 显示食谱
     */
    private void showRecipe(boolean gonnaShow){
        if(gonnaShow){
            morningTitle.setVisibility(View.VISIBLE);
            noonTitle.setVisibility(View.VISIBLE);
            afternoonTitle.setVisibility(View.VISIBLE);
            breakfastText.setVisibility(View.VISIBLE);
            lunchText.setVisibility(View.VISIBLE);
            supperText.setVisibility(View.VISIBLE);
        } else {
            morningTitle.setVisibility(View.GONE);
            noonTitle.setVisibility(View.GONE);
            afternoonTitle.setVisibility(View.GONE);
            breakfastText.setVisibility(View.GONE);
            lunchText.setVisibility(View.GONE);
            supperText.setVisibility(View.GONE);
        }
    }
    /**
     * 打开食谱后的返回触摸监听
     */
    private class backTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent event){
            if(!isHome){
                //自动执行CurveAnim1
                homeAnim.startCurveAnim2(false);
                curveView.setEnabled(false);
                recipeText.setVisibility(View.INVISIBLE);
                showRecipe(false);
                showHomeView();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isHome = true;
                        circleText.setEnabled(true);
                    }
                }, 1000);
            }
            return false;
        }
    }

    /**
     * 所有的onClick事件统一由Activity负责响应操作
     */
    public interface HomeFragClickListener{
        void onHomeFragClick(View view);
    }


}
