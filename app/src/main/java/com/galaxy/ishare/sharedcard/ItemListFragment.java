package com.galaxy.ishare.sharedcard;

import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.R;

public class ItemListFragment extends Fragment implements AbsListView.OnScrollListener {

    private View mRoot;
    private LinearLayout categoryLayout, discountLayout, distanceLayout, defaultLayout, topSelectorLayout;
    private MyClickListener myClickListener;
    private int topSelectorLayoutWidth;
    private TextView categoryTv;
    private ListView cardListView;
    private SwipeRefreshLayout listSwipeRefreshLayout;
    private static final String TAG = "ItemListFragment";
    private HttpInteract httpInteract;
    private int pageIndex;




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
        listSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });





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
        listSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.share_item_swiperefresh);

//        cardListView.setAdapter(new CardListItemAdapter());
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

                final String[] wareItems = getResources().getStringArray(R.array.ware_items);
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

        public void loadData() {

        }
    }

}
