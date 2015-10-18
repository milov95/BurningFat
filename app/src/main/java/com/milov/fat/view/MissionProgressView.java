package com.milov.fat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.milov.fat.R;
import com.milov.fat.util.DataManager;
import com.milov.fat.util.DisplayUtil;

/**
 * Created by Milov on 2015/9/28.
 */
public class MissionProgressView extends View {
    private Paint framePaint,bluePaint,darkBluePaint,textPaint;
    private float totalDays=30,completeDays=18,reachDays=12;
    private DataManager dataManager;
    private DisplayUtil displayUtil;
    private int length,height,strokeWidth;
    public MissionProgressView(Context context){
        this(context, null );
    }

    public MissionProgressView(Context context,AttributeSet attrs){
        super(context,attrs);

        dataManager = DataManager.getInstance(context);
        displayUtil = DisplayUtil.getInstance(context);

        length = (int) (DisplayUtil.SCREEN_WIDTH*(3f/5));
        height = displayUtil.dp2px(15);
        strokeWidth = 5 ;

        framePaint = new Paint();
        framePaint.setColor(getResources().getColor(R.color.blue));
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(strokeWidth);
        framePaint.setAntiAlias(true);

        bluePaint = new Paint();
        bluePaint.setColor(getResources().getColor(R.color.blue));
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setAntiAlias(true);

        darkBluePaint = new Paint();
        darkBluePaint.setColor(getResources().getColor(R.color.dark_blue));
        darkBluePaint.setStyle(Paint.Style.FILL);
        darkBluePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.gray));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(20);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        setMeasuredDimension( length,height+ 40);
    }

    @Override
    public void onDraw(Canvas canvas){
        drawProgress(canvas);
        drawFrame(canvas);
    }

    private void drawFrame(Canvas canvas){
        canvas.drawRect(strokeWidth, strokeWidth, length-strokeWidth, height, framePaint);
    }

    private void drawProgress(Canvas canvas){
        //画深色区域
        canvas.drawRect(strokeWidth,strokeWidth,(length-strokeWidth)*(completeDays/totalDays),height,darkBluePaint);
        //画浅色区域,覆盖一部分深色区域
        canvas.drawRect(strokeWidth,strokeWidth,(length-strokeWidth)*(reachDays/totalDays),height,bluePaint);
        //画文字
        canvas.drawText(
                "达标"+(int)reachDays+"天 / 未达标"+(int)(completeDays-reachDays)+"天 / 剩余"+(int)(totalDays-completeDays)+"天",
                length/2,
                height*2,
                textPaint);
    }

    public void loadMissionData(int totalDays,int completeDays,int reachDays){
        this.totalDays = totalDays;
        this.completeDays = completeDays;
        this.reachDays = reachDays;
        invalidate();
    }

}
