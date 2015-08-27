package com.milov.fat.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.milov.fat.activity.HomeActivity;

import java.lang.reflect.Field;

/**
 * Created by Milov on 2015/8/21.
 * 屏幕显示工具类
 */
public class DisplayUtil {
    /**
     * 单一实例
     */
    public static DisplayUtil displayUtil=null;
    /**
     * 屏幕宽(px)
     */
    public static float SCREEN_WIDTH;
    /**
     * 屏幕高(px)
     */
    public static float SCREEN_HEIGHT;
    /**
     * 状态栏高度
     */
    public static float statusBarHeight;
    /**
     * 标题栏高度
     */
    public static float titleBarHeight;
    /**
     * 分辨率密度
     */
    private static float scale;
    /**
     * 上下文关系
     */
    private Context context;

    private DisplayUtil(Context context){
        this.context=context;
        DisplayMetrics dpMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dpMetrics);
        SCREEN_WIDTH = dpMetrics.widthPixels;
        SCREEN_HEIGHT = dpMetrics.heightPixels;
        scale = dpMetrics.density;
        statusBarHeight = getStatusBarHeight();
        titleBarHeight = dp2px(52);
    }

    /**
     * 获取单一实例
     * @param context
     * @return
     */
    public static DisplayUtil getInstance(Context context){
        if(displayUtil==null)
            displayUtil = new DisplayUtil(context);
        return displayUtil;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * dp转px
     * @param dp
     * @return
     */
    public int dp2px(int dp){
        return (int)(dp*scale+0.5f);
    }
}
