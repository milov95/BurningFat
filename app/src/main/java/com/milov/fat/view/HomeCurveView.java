package com.milov.fat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.widget.SlidingPaneLayout;
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
     * 曲线画笔
     */
    private Paint curvePaint;
    /**
     * 圆形画笔
     */
    private Paint circlePaint;
    /**
     * 按钮画笔
     */
    private Paint buttonPaint;
    /**
     * 用于变化颜色
     */
    public int argb ;
    /**
     * 用于控制变换；[-1,0]为曲线变直线，(0,1]为闭合动画
     */
    private float value;

    /**
     * 按钮缩小后的的半径
     */
    private float r;
    /**
     * 按钮圆心坐标
     */
    private float x,y;
    /**
     * 控制点的参数
     */
    private float a,b;
    private float k;

    public HomeCurveView(Context context){
        this(context, null);
    }

    public HomeCurveView(Context context, AttributeSet attrs){

        super(context,attrs);

        //得到屏幕尺寸工具类的单一实例
        displayUtil = DisplayUtil.getInstance(context);
        SCREEN_HEIGHT = displayUtil.SCREEN_HEIGHT;
        SCREEN_WIDTH = displayUtil.SCREEN_WIDTH;

        value=-1;
        argb = 0xff00aaf9;
        r = displayUtil.dp2px(75/2);
        x = SCREEN_WIDTH/2;
        y = SCREEN_HEIGHT-displayUtil.dp2px(135)-displayUtil.statusBarHeight;

        //配置画笔
        curvePaint = new Paint();
        curvePaint.setAntiAlias(true);
        curvePaint.setColor(0xff00aaf9);
        curvePaint.setStyle(Paint.Style.FILL);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(0xffffffff);
        circlePaint.setStyle(Paint.Style.FILL);

        buttonPaint = new Paint();
        buttonPaint.setAntiAlias(true);
        buttonPaint.setColor(0xffffffff);
        buttonPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int defaultSize, int measureSpec){
        super.onMeasure(defaultSize , measureSpec);
    }

    @Override
    public void onDraw(Canvas canvas){
        if(curvePaint!=null)
            curvePaint.setColor(argb);
        if(value<=0){
            Path curvePath = new Path();
            float sideY = -value*2*SCREEN_HEIGHT/20+10*SCREEN_HEIGHT/20;
            //画弧线
            curvePath.moveTo(0,sideY);
            curvePath.quadTo(SCREEN_WIDTH/2,10*SCREEN_HEIGHT/20,SCREEN_WIDTH,sideY);
            //画弧线下方的矩形区域
            curvePath.lineTo(SCREEN_WIDTH,SCREEN_HEIGHT);
            curvePath.lineTo(0,SCREEN_HEIGHT);
            curvePath.lineTo(0,sideY);
            curvePath.close();
            canvas.drawPath(curvePath,curvePaint);

            //画可以变形的按钮
            Path buttonPath = new Path();
            buttonPath.addRoundRect(new RectF(x - r, y - r, x + r, y + r), r, r, Path.Direction.CCW);
            canvas.drawPath(buttonPath,buttonPaint);

        } else {
            //画背景矩形
            canvas.drawRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, curvePaint);
            //先是圆的底部高度不变，再后保持圆心高度不变
            if((1-value)*999>SCREEN_HEIGHT/5)
                canvas.drawCircle(SCREEN_WIDTH/2,10*SCREEN_HEIGHT/20-(1-value)*999,(1-value)*999,circlePaint);
            else
                canvas.drawCircle(SCREEN_WIDTH/2,10*SCREEN_HEIGHT/20-SCREEN_HEIGHT/5,(1-value)*999,circlePaint);
            //画可以变形的按钮
            //画可以变形的按钮
            Path buttonPath = new Path();
            buttonPath.addRoundRect(new RectF(x - r, y - r, x + r, y + r), r, r, Path.Direction.CCW);
            canvas.drawPath(buttonPath,buttonPaint);
        }
    }

    /**
     * 应用变换
     * @param value [-1,0]为曲线变直线，(0,1]为闭合动画
     */
    public void applyChange(float value){
        this.value = value;
        invalidate();
    }

    public void setValue(float value){
        this.value =value;
        this.invalidate();
    }

    public float getValue(){
        return value;
    }

    public void setArgb(int argb){
        this.argb = argb;
        invalidate();
    }

    public float getArgb(){
        return argb;
    }
}
