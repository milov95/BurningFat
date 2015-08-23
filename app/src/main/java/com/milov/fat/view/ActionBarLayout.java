package com.milov.fat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.milov.fat.util.DisplayUtil;

/**
 * Created by Milov on 2015/8/22.
 * 自定义通用格式的ActionBar，子控件边距与间距的配置在该类实现，使用时只需在XML下添加宽高均为0dp的子控件即可（其中第二个控件，也就是TextView的宽必须为wrap_content）
 * 左边是图标，中间是标题，右边是图标（可以没有）
 */
public class ActionBarLayout extends ViewGroup {

    /**
     * 屏幕宽度
     */
    private static float SCREEN_WIDTH;
    /**
     * ActionBarLayout高度
     */
    private static float BAR_HEIGHT;
    /**
     * 屏幕尺寸工具类
     */
    private static DisplayUtil displayUtil;

    public ActionBarLayout(Context context){
        this(context,null);
    }

    public ActionBarLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        //获取屏幕尺寸工具类
        displayUtil = DisplayUtil.getInstance(context);
        //获取屏幕宽度
        SCREEN_WIDTH = displayUtil.SCREEN_WIDTH;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){
        //获取标题栏高度
        BAR_HEIGHT=getHeight();
        //子控件的外边距
        final int margin = displayUtil.dp2px(0);
        //子控件的高度以及左右两个图标的宽度
        final int square = (int) BAR_HEIGHT - (2*margin);
        //部署控件
        //左边的图标
        getChildAt(0).layout(margin, margin, margin + square, margin + square);
        Log.i("actionBar--layout", getChildAt(0).toString() + "-----"+BAR_HEIGHT+"|"+margin);
        //中间的文字
        getChildAt(1).layout(margin + square, margin, (int) SCREEN_WIDTH - margin - square,margin+square);
        //如果右边有图标，则部署
        if(getChildAt(2) != null){
            getChildAt(2).layout((int) SCREEN_WIDTH - margin - square,margin,(int) SCREEN_WIDTH - margin,margin+square);
        }
    }
}
