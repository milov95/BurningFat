package com.milov.fat.util;

/**
 * Created by Milov on 2015/9/25.
 */
public class DataManager {

    /**
     * 用于获取应用数据
     */
    private static DataManager dataManager;
    /**
     * 设置用户信息及任务时的数据项
     */
    public String[] heights,weights,goals;

    private DataManager(){
        heights = new String[51];
        for(int i = 0; i <= 50;i++){
            heights[i]=i+150+"cm";
            if(i==0)
                heights[0] = "≤150cm";
            else if(i==50)
                heights[50] = "≥200cm";
        }
        weights = new String[61];
        for(int i = 0; i <= 60;i++){
            weights[i]=i+40+"kg";
            if(i==0)
                weights[0] = "≤40kg";
            else if(i==60)
                weights[60] = "≥100kg";
        }
        goals = new String[20];
        for(int i = 0; i <= 19;i++){
            goals[i]="-"+ i + "kg";
        }
    }

    /**
     * 单一实例
     */
    public static DataManager getInstance(){
        if(dataManager == null )
            dataManager = new DataManager();
        return dataManager;
    }



}

