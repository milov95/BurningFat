package com.milov.fat.Animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.milov.fat.fragment.HomeFragment;

/**
 * Created by Milov on 2015/8/25.
 */
public class HomeAnim implements ValueAnimator.AnimatorUpdateListener{

    public ValueAnimator circleForwardAnim,circleBackAnim;
    private View view;

    public HomeAnim(View v){
        view = v ;
        circleForwardAnim = ValueAnimator.ofFloat(1,0.5f);
        circleForwardAnim.setInterpolator(new DecelerateInterpolator());
        circleForwardAnim.setDuration(150);
        circleForwardAnim.addUpdateListener(this);

        circleBackAnim = ValueAnimator.ofFloat(0.5f,1);
        circleBackAnim.setInterpolator(new DecelerateInterpolator());
        circleBackAnim.setDuration(150);
        circleBackAnim.addUpdateListener(this);

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator){
        Float value =Float.parseFloat(animator.getAnimatedValue().toString());
        view.setScaleX(value);
        view.setScaleY(value);
    }

    public void startCircleForward(){
        circleForwardAnim.start();
    }

    public void startCircleBack(){
        circleBackAnim.start();
    }

}
