package com.galaxy.ishare.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.utils.JPushUtil;

import cn.jpush.android.service.DownloadService;
import cn.jpush.android.service.PushService;

/**
 * Created by Zhan on 2015/5/25.
 */

public class JPushStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        JPushUtil.getInstance(context).setAlias(IShareContext.getInstance().getCurrentUser().getUserPhone());

        Intent i = new Intent();
        i.setClass(context, DownloadService.class);
        // 启动service
        // 多次调用startService并不会启动多个service 而是会多次调用onStart
        context.startService(i);

        i = new Intent();
        i.setClass(context, PushService.class);
        // 启动service
        // 多次调用startService并不会启动多个service 而是会多次调用onStart
        context.startService(i);
    }
}