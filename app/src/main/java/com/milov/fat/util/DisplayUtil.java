package com.milov.fat.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by milov on 2015/8/21.
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
     * 分辨率密度
     */
    private static float scale;

    private DisplayUtil(Context context){
        DisplayMetrics dpMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dpMetrics);
        SCREEN_WIDTH = dpMetrics.widthPixels;
        SCREEN_HEIGHT = dpMetrics.heightPixels;
        scale = dpMetrics.density;
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

    /**
     * dp转px
     * @param dp
     * @return
     */
    public int dp2px(int dp){
        return (int)(dp*scale+0.5f);
    }
}
