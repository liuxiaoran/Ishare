package com.galaxy.ishare;

import android.app.Fragment;
import android.widget.FrameLayout;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by liuxiaoran on 15/7/10.
 */
public class IShareFragment extends Fragment {

    public String fragmentName = "unassigned";

    public void init(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(fragmentName);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(fragmentName);
    }


}
