package com.galaxy.ishare.sharedcard;

import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.cloud.BaseCloudSearchInfo;
import com.galaxy.ishare.Global;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpGetExt;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemListFragment extends Fragment implements AbsListView.OnScrollListener {

    private View mRoot;
    private LinearLayout categoryLayout, discountLayout, distanceLayout, defaultLayout, topSelectorLayout;
    private MyClickListener myClickListener;
    private int topSelectorLayoutWidth;
    private TextView categoryTv;
    private ListView cardListView;
    private static final String TAG = "ItemListFragment";
    private HttpInteract httpInteract;
    private int pageIndex;
    private ArrayList<CardItem> dataList ;
    private static final int DISTANCE_LOAD_TYPE=1;
    private static final int DISCOUNT_LOAD_TYPE =2;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = LayoutInflater.from(getActivity());
        mRoot = lf.inflate(R.layout.share_item_fragment, container, false);
        initViews(mRoot);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        topSelectorLayout.measure(w, h);
        topSelectorLayoutWidth = topSelectorLayout.getMeasuredWidth();
        Log.v(TAG, topSelectorLayoutWidth + "layout width");
        Log.v(TAG, Global.screenWidth + "");


        myClickListener = new MyClickListener();
        categoryLayout.setOnClickListener(myClickListener);
        discountLayout.setOnClickListener(myClickListener);
        distanceLayout.setOnClickListener(myClickListener);
        defaultLayout.setOnClickListener(myClickListener);

        httpInteract = new HttpInteract();
        dataList = new ArrayList<>();





        return mRoot;
    }


    public void initViews(View view) {

        categoryLayout = (LinearLayout) view.findViewById(R.id.share_item_category_layout);
        discountLayout = (LinearLayout) view.findViewById(R.id.share_item_discount_layout);
        distanceLayout = (LinearLayout) view.findViewById(R.id.share_item_distance_layout);
        defaultLayout = (LinearLayout) view.findViewById(R.id.share_item_default_layout);
        topSelectorLayout = (LinearLayout) view.findViewById(R.id.share_item_top_layout);

        categoryTv = (TextView) view.findViewById(R.id.share_item_category_tv);

        cardListView = (ListView) view.findViewById(R.id.share_item_card_listview);

        cardListView.setAdapter(new CardListItemAdapter(dataList,getActivity()));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.share_item_category_layout) {

                View popUpWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.share_item_popup_window, null);

                // popupwindow 中的listview
                ListView listViewInPopUpWindow = (ListView) popUpWindowView.findViewById(R.id.share_item_popupwindow_listview);

                final String[] wareItems = getResources().getStringArray(R.array.trade_items);
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, wareItems);
                listViewInPopUpWindow.setAdapter(adapter);

                final PopupWindow popupWindow = new PopupWindow(popUpWindowView, topSelectorLayoutWidth, Global.screenHeight * 2 / 3, false);
                popupWindow.showAsDropDown(v, 0, 0);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                listViewInPopUpWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        categoryTv.setText(wareItems[position]);
                        //TODO: 点击了某个类别
                        popupWindow.dismiss();

                    }
                });


            } else if (v.getId() == R.id.share_item_discount_layout) {

            } else if (v.getId() == R.id.share_item_distance_layout) {

            } else if (v.getId() == R.id.share_item_default_layout) {

            }
        }
    }


    class HttpInteract {

        public void loadDataByDiscount(int loadType,int tradeType, double longitude,double latitude, int pageNumber, int pageSize ) {

            List<NameValuePair> paramsList  = new ArrayList<>();
            paramsList.add(new BasicNameValuePair("trade_type", tradeType+""));
            paramsList.add(new BasicNameValuePair("page_num",pageNumber+""));
            paramsList.add(new BasicNameValuePair("page_size", pageSize + ""));
            String url=null;
            if (loadType == DISCOUNT_LOAD_TYPE){
                url = URLConstant.GET_DISCOUNT_CARD_LIST;
            }else if (loadType == DISTANCE_LOAD_TYPE){
                url = URLConstant.GET_DISTANCE_CARD_LIST;
            }
            HttpTask.startAsyncDataGetRequset(url, paramsList, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {

                    try {

                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status==0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject card = jsonArray.getJSONObject(i);
                                CardItem cardItem = new CardItem(card.getInt("id"), card.getString("owner"), card.getString("shop_name"), card.getInt("ware_type"), card.getDouble("discount"),
                                        card.getInt("trade_type"), card.getString("shop_location"), card.getDouble("shop_longitude"), card.getDouble("shop_latitude"),
                                        card.getString("description"), card.getString("img"), card.getString("time"), card.getDouble("longitude"), card.getDouble("latitude"),
                                        card.getString("location"), card.getInt("distance"));
                                dataList.add(cardItem);
                            }
                        }else {
                            Log.v(TAG,"status is "+status);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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
