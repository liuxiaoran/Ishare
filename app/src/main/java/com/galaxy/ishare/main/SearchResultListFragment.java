package com.galaxy.ishare.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.sharedcard.CardDetailActivity;
import com.galaxy.ishare.sharedcard.CardListItemAdapter;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/26.
 */
public class SearchResultListFragment extends Fragment {

    private static final int REFRESH_GESTURE=2;
    private static final int LOAD_MORE_GESTURE=3;
    int pageNumber;

    private FrameLayout resultFrameLayout;

    private PullToRefreshListView resultRefreshListView;

    private ListView resultListView;

    private CardListItemAdapter itemAdapter;

    private LinkedList<CardItem> resultDataList;

    private int gestureType;

    private int pageSize=6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View  resultView  =  inflater.inflate(R.layout.main_search_result_fragment,null);



        resultFrameLayout = (FrameLayout) resultView.findViewById(R.id.main_search_result_framelayout);

        resultDataList = new LinkedList();
        itemAdapter  = new CardListItemAdapter(resultDataList,getActivity());

        resultRefreshListView = new PullToRefreshListView(getActivity());
        initPullToRefreshListView(resultRefreshListView);



        return super.onCreateView(inflater, container, savedInstanceState);


    }

    public void initPullToRefreshListView(PullToRefreshListView resultRefreshListView){
        resultRefreshListView.setPullLoadEnabled(false);
        resultRefreshListView.setScrollLoadEnabled(true);
        resultListView = resultRefreshListView.getRefreshableView();
        resultListView.setAdapter(itemAdapter);

        // listview 条目点击
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, resultDataList.get(position));
                startActivity(intent);
            }
        });

        resultRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
//                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
//                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                gestureType = LOAD_MORE_GESTURE;
                pageNumber++;
//                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
//                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);

            }
        });
    }

    class HttpInteract {

        public void search(String keyWord){

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("keyword",keyWord));
            params.add(new BasicNameValuePair("trade_type",-1+""));
            params.add(new BasicNameValuePair("longitude",IShareContext.getInstance().getUserLocation().getLongitude()+""));
            params.add(new BasicNameValuePair("latitude",IShareContext.getInstance().getUserLocation().getLatitude()+""));
            params.add(new BasicNameValuePair("page_num",pageNumber+""));
            params.add(new BasicNameValuePair("page_size",pageSize+""));
            HttpTask.startAsyncDataGetRequset(URLConstant.GET_DISTANCE_CARD_LIST, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });
        }


    }


}
