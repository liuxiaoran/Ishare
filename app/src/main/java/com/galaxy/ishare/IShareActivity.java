package com.galaxy.ishare;

import android.support.v7.app.ActionBarActivity;

import com.galaxy.ishare.utils.IShareUnCaughtExceptionHandler;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by liuxiaoran on 15/7/10.
 * 所有activity的父类
 * 集成友盟统计（session的统计）
 */
public class IShareActivity extends ActionBarActivity {
//    public IShareActivity() {
//        IShareUnCaughtExceptionHandler.getInstance().init(this);
//    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 当Activity中有fragment时候，需要统计fragment session时候，需要先禁用Activity的统计
     */
    public void forbidActivityStatistics() {
        MobclickAgent.openActivityDurationTrack(false);
    }
}
