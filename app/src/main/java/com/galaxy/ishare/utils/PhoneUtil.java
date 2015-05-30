package com.galaxy.ishare.utils;

import android.os.Environment;

/**
 * Created by liuxiaoran on 15/5/23.
 * 针对手机
 */
public class PhoneUtil {

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
