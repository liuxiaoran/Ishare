package com.galaxy.ishare.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by lxr_pc on 2015/3/14.
 * <p/>
 * 得到屏幕宽高和px、dp单位转化
 */

public class DisplayUtil {

    /**
     * @param context
     * @param pxValue
     * @param
     * @return int
     * @方法名称: px2dip
     * @描述: 将px值转换为dip或dp值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param context
     * @param dipValue
     * @param
     * @return int
     * @方法名称: dip2px
     * @描述: 将dip或dp值转换为px值
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        int mScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
        return mScreenWidth;
    }

    /**
     * @param context
     * @param pxValue
     * @param
     * @方法名称: px2sp
     * @描述: 将px值转换为sp值
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * @param context
     * @param spValue
     * @param
     * @return int
     * @方法名称: sp2px
     * @描述: 将sp值转换为px值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据电话号码生成联系人的颜色
     *
     * @param phone 联系人的颜色值
     * @return
     */
    public static int getRandomMaterialColor(String phone) {
        int index = 0;


        for (int i = 0; i < phone.length(); i++) {
            index += phone.charAt(i);
        }
        index = index % 10;
        Log.e("DisplayUtil", index + " " + phone);

        String colorCode;
        switch (index) {
            case 0:
                colorCode = "#F44336";
                break;
            case 1:
                colorCode = "#E91E63";
                break;
            case 2:
                colorCode = "#9C27B0";
                break;
            case 3:
                colorCode = "#673AB7";
                break;
            case 4:
                colorCode = "#3F51B5";
                break;
            case 5:
                colorCode = "#009688";
                break;
            case 6:
                colorCode = "#03A9F4";
                break;
            case 7:
                colorCode = "#00BCD4";
                break;
            case 8:
                colorCode = "#009688";
                break;
            case 9:
                colorCode = "#9E9E9E";
                break;
            default:
                colorCode = "#FFEB3B";
        }


        return Color.parseColor(colorCode);
    }

}
