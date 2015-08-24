package com.milov.fat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.milov.fat.util.DisplayUtil;

/**
 * Created by Milov on 2015/8/21.
 * 首页的曲线背景
 */
public class HomeCurveView extends View {
    /**
     * 屏幕高度
     */
    private static float SCREEN_HEIGHT;
    /**
     * 屏幕宽度
     */
    private static float SCREEN_WIDTH;
    /**
     * 屏幕尺寸工具类
     */
    private static DisplayUtil displayUtil;
    /**
     * 画笔
     */
    private Paint curvePaint;

    public HomeCurveView(Context context){
        this(context,null);
    }

    public HomeCurveView(Context context, AttributeSet attrs){
        super(context,attrs);

        //得到屏幕尺寸工具类的单一实例
        displayUtil = DisplayUtil.getInstance(context);
        SCREEN_HEIGHT = displayUtil.SCREEN_HEIGHT;
        SCREEN_WIDTH = displayUtil.SCREEN_WIDTH;

        //配置画笔
        curvePaint = new Paint();
        curvePaint.setAntiAlias(true);
        curvePaint.setColor(0xff00aaf9);
        curvePaint.setStyle(Paint.Style.FILL);
        curvePaint.setStrokeWidth(10);
    }

    @Override
    protected void onMeasure(int defaultSize, int measureSpec){
        super.onMeasure(defaultSize , measureSpec);
    }

    @Override
    public void onDraw(Canvas canvas){

        Path curvePath = new Path();
        curvePath.moveTo(0,SCREEN_HEIGHT/20);
        //弧线高度为二十分之一的屏幕高度
        curvePath.quadTo(SCREEN_WIDTH/2,-SCREEN_HEIGHT/20,SCREEN_WIDTH,SCREEN_HEIGHT/20);
        //画弧线下方的矩形区域
        curvePath.rLineTo(0,7*SCREEN_HEIGHT/20);
        curvePath.rLineTo(-SCREEN_WIDTH,0);
        curvePath.rLineTo(0,7*SCREEN_HEIGHT/15);
        curvePath.close();
        canvas.drawPath(curvePath,curvePaint);
    }


}
