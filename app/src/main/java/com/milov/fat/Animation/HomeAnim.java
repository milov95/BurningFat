package com.milov.fat.Animation;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.milov.fat.view.HomeCurveView;

/**
 * 用来配置整个应用的动画
 * Created by Milov on 2015/8/25.
 */
public class HomeAnim {
    /**
     * 放大动画
     */
    public ValueAnimator enlargeAnim;
    /**
     * 缩小动画
     */
    public ValueAnimator reduceAnim;
    /**
     * 首页曲线变直
     */
    public ValueAnimator curveStraightenAnim;
    /**
     * 首页曲线变弯
     */
    public ValueAnimator curveBentAnim;
    /**
     * 颜色由蓝变绿
     */
    private ValueAnimator blue2greenAnim;
    /**
     * 颜色由绿变蓝
     */
    private ValueAnimator green2blueAnim;
    /**
     * 用于应用缩放动画的View
     */
    private View scaleView;
    /**
     * 用于应用曲线动画的View
     */
    private View curveView;

    public HomeAnim(View scaleView,View curveView){
        this.scaleView = scaleView ;
        this.curveView = curveView ;
        initAnim();
    }

    /**
     * 缩放动画更新监听器
     */
    private class ScaleAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener{
        @Override
        public void onAnimationUpdate(ValueAnimator animator){
            Float value = Float.parseFloat(animator.getAnimatedValue().toString());
            scaleView.setScaleX(value);
            scaleView.setScaleY(value);
            ((TextView)scaleView).getTextColors().withAlpha((int)(2*(value-0.5)*255));
        }
    }
    /**
     * 首页曲线动画更新监听器
     */
    private class CurveAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener{
        @Override
        public void onAnimationUpdate(ValueAnimator animator){
            Float value = Float.parseFloat(animator.getAnimatedValue().toString());
            ((HomeCurveView) curveView).applyChange(value);

        }
    }
    /**
     * 首页颜色动画更新监听器
     */
    private class ColorAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener{
        @Override
        public void onAnimationUpdate(ValueAnimator animator){
            int argb = (Integer) animator.getAnimatedValue();
            ((HomeCurveView) curveView).setArgb(argb);

        }
    }
    /**
     * 初始化动画
     */
    private void initAnim(){
        enlargeAnim = ValueAnimator.ofFloat(0.5f,1f);
        enlargeAnim.setInterpolator(new DecelerateInterpolator());
        enlargeAnim.setDuration(100);
        enlargeAnim.addUpdateListener(new ScaleAnimUpdateListener());

        reduceAnim = ValueAnimator.ofFloat(1,0.5f);
        reduceAnim.setInterpolator(new DecelerateInterpolator());
        reduceAnim.setDuration(100);
        reduceAnim.addUpdateListener(new ScaleAnimUpdateListener());

        curveStraightenAnim = ValueAnimator.ofFloat(-1,0);
        curveStraightenAnim.setInterpolator(new DecelerateInterpolator());
        curveStraightenAnim.setDuration(100);
        curveStraightenAnim.addUpdateListener(new CurveAnimUpdateListener());

        curveBentAnim = ValueAnimator.ofFloat(0,-1);
        curveBentAnim.setInterpolator(new DecelerateInterpolator());
        curveBentAnim.setDuration(100);
        curveBentAnim.addUpdateListener(new CurveAnimUpdateListener());

        blue2greenAnim = ValueAnimator.ofObject(new ArgbEvaluator(), 0xff00aaf9, 0xff4d8801);
        blue2greenAnim.setInterpolator(new LinearInterpolator());
        blue2greenAnim.setDuration(700);
        blue2greenAnim.addUpdateListener(new ColorAnimUpdateListener());

        green2blueAnim = ValueAnimator.ofObject(new ArgbEvaluator(), 0xff4d8801, 0xff00aaf9);
        green2blueAnim.setInterpolator(new LinearInterpolator());
        green2blueAnim.setDuration(700);
        green2blueAnim.addUpdateListener(new ColorAnimUpdateListener());

    }

    /**
     * 启动曲线动画1
     */
    public void startCurveAnim1(boolean gonnaStraighten){
        if(gonnaStraighten)
            curveStraightenAnim.start();
        else{
            curveBentAnim.start();
            scaleView.setVisibility(View.VISIBLE);
            startCircleAnim(true);
        }
    }
    /**
     * 启动曲线动画2
     */
    public void startCurveAnim2(boolean gonnaFill){
        final boolean fill=gonnaFill;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(fill) {
                    while (curveStraightenAnim.isRunning()) ;
                    CurveFillTask fillTask = new CurveFillTask();
                    fillTask.execute(1l);
                }
            }
        }).start();
        if(fill) blue2greenAnim.start();
        else {
            while (blue2greenAnim.isRunning()) ;
            CurveOpenTask openTask = new CurveOpenTask();
            openTask.execute(1l);
            green2blueAnim.start();
        }
    }
    /**
     * 启动缩放动画
     */
    public void startCircleAnim(boolean gonnaLargen){
        if(gonnaLargen){
            enlargeAnim.start();
        }
        else
            reduceAnim.start();
    }

    /**
     * param 为long型的刷新间隔的时长
     */
    private class CurveFillTask extends AsyncTask<Long,Float,Float>{
        @Override
        protected Float doInBackground(Long... params) {
            float i = 0;
            while( i < 2 ){
                if(i<0.5) i+=0.01;
                else if(i<0.6) i+=0.01;
                else if(i<0.7) i+=0.006;
                else if(i<0.8) i+=0.0040;
                else if(i<1) i+=0.002;
                else i+=0.007;
                publishProgress(i);
                try{
                    Thread.sleep(params[0]);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return 2f;
        }

        @Override
        protected void onProgressUpdate(Float... progress) {
            ((HomeCurveView) curveView).applyChange(progress[0]);
        }
        @Override
        protected void onPostExecute(Float result) {
            super.onPostExecute(result);
            ((HomeCurveView) curveView).setValue(result);
        }
    }

    /**
     * param 为long型的刷新间隔的时长
     */
    private class CurveOpenTask extends AsyncTask<Long,Float,Float>{
        @Override
        protected Float doInBackground(Long... params) {
            float i = 2;
            while( i >0 ){
                if(i>1) i-=0.007;
                else if(i>0.8) i-=0.002;
                else if(i>0.7) i-=0.004;
                else if(i>0.6) i-=0.006;
                else if(i>0.5) i-=0.01;
                else i-=0.01;

                publishProgress(i);
                try{
                    Thread.sleep(params[0]);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return 0f;
        }

        @Override
        protected void onProgressUpdate(Float... progress) {
            ((HomeCurveView) curveView).applyChange(progress[0]);
        }
        @Override
        protected void onPostExecute(Float result) {
            super.onPostExecute(result);
            ((HomeCurveView) curveView).setValue(result);
            startCurveAnim1(false);
        }
    }

}
