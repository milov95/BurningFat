package com.milov.fat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.milov.fat.R;
import com.milov.fat.util.DisplayUtil;

/**
 * Created by Milov on 2015/8/28.
 */
public class LineChartView extends View {
    private Paint gridPaint,linePaint,fillPaint,textPaint;
    private Path gridPath,linePath,textPath;
    private DisplayUtil displayUtil;
    private float space,height;


    public LineChartView(Context context){
        this(context, null , null);
    }

    public LineChartView(Context context, AttributeSet attrs){
        this(context,attrs,null);
    }

    public LineChartView(Context context, AttributeSet attrs,Object obj){
        super(context,attrs);
        displayUtil = DisplayUtil.getInstance(getContext());
        space = displayUtil.dp2px(30);
        height = displayUtil.dp2px(300);

        gridPaint.setColor(Color.WHITE);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);
        gridPaint.setAntiAlias(true);

        fillPaint.setColor(getResources().getColor(R.color.light_red));
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(displayUtil.dp2px(10));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure( widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas){

    }
}
