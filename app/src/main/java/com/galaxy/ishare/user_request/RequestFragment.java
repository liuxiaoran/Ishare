package com.galaxy.ishare.user_request;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.sharedcard.CardDetailActivity;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by liuxiaoran on 15/6/2.
 */
public class RequestFragment extends Fragment {

    private FrameLayout container;
    private PullToRefreshListView refreshListView;
    private FloatingActionButton addFloatingBtn;
    private ListView requestListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.request_fragment, null);

        initViews(view);
        initPullRefreshListView(refreshListView);


        return view;
    }

    private void initViews(View view) {
        container = (FrameLayout) view.findViewById(R.id.request_framelayout);
        refreshListView = new PullToRefreshListView(getActivity());
        addFloatingBtn = (FloatingActionButton) view.findViewById(R.id.request_plus_floating_btn);
        addFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 转跳到发新的发请求的界面
            }
        });
    }

    private void initPullRefreshListView(PullToRefreshListView pullToRefreshListView) {
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setScrollLoadEnabled(true);
        requestListView = pullToRefreshListView.getRefreshableView();
        requestListView.setDivider(null);// 设置不显示分割线

    }
}
