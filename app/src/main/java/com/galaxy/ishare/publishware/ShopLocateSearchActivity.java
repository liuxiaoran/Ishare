package com.galaxy.ishare.publishware;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/6/17.
 */
public class ShopLocateSearchActivity extends ActionBarActivity {

    private EditText contentEt;
    private FButton searchBtn;
    private TextView searchInMap;
    private ListView resultListView;
    private PoiSearch mPoiSearch;
    private List<PoiInfo> dataList;
    public static final int pageCapacity = 20;
    public static final String TAG = "shoplocate";
    OnGetPoiSearchResultListener poiListenr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_shop_locate_search_activity);

        android.support.v7.app.ActionBar actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.main_search_actionbar, true);

        searchBtn = (FButton) actionBar.getCustomView().findViewById(R.id.search_btn);
        searchBtn.setVisibility(View.INVISIBLE);
        contentEt = (EditText) actionBar.getCustomView().findViewById(R.id.search_et);

        searchInMap = (TextView) findViewById(R.id.publish_shop_search_map_tv);
        resultListView = (ListView) findViewById(R.id.publish_shop_location_search_result_listview);

        searchInMap.setEnabled(false);

        dataList = new ArrayList<>();
        final ResultAdapter resultAdapter = new ResultAdapter(this);
        resultListView.setAdapter(resultAdapter);

        mPoiSearch = PoiSearch.newInstance();

        poiListenr = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null
                        || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Toast.makeText(ShopLocateSearchActivity.this, "未找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                    // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (CityInfo cityInfo : result.getSuggestCityList()) {
                        strInfo += cityInfo.city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(ShopLocateSearchActivity.this, strInfo, Toast.LENGTH_LONG)
                            .show();
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {

                    dataList = result.getAllPoi();
                    resultAdapter.notifyDataSetChanged();
                    resultListView.setSelection(0);

                    if (dataList.size() > 0) {
                        searchInMap.setEnabled(true);
                        searchInMap.setTextColor(getResources().getColor(R.color.dark_secondary_text));
                    }

                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListenr);

        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0 && !"".equals(s.toString())) {
                    Log.v(TAG, "content: " + s.toString());
                    searchPoi(s.toString());
                }

            }
        });

        searchInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopLocateSearchActivity.this, PoiSearchActivity.class);
                intent.putExtra(PoiSearchActivity.PARAMETER_SEARCH_SHOP_NAEM, contentEt.getText().toString());
                startActivityForResult(intent, 0);
            }
        });
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent retIntent = new Intent(ShopLocateSearchActivity.this, PublishItemActivity.class);
                retIntent.putExtra(PoiSearchActivity.PARAMETER_SHOP_LATITUDE, dataList.get(position).location.latitude);
                retIntent.putExtra(PoiSearchActivity.PARAMETER_SHOP_LONGITUDE, dataList.get(position).location.longitude);
                retIntent.putExtra(PoiSearchActivity.PARAMETER_SHOP_ADDR, dataList.get(position).address);
                retIntent.putExtra(PoiSearchActivity.PARAMETER_SHOP_NAME, dataList.get(position).name);
                setResult(PublishItemActivity.PARAMETER_SHOP_LOCATION_RESULT_CODE, retIntent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            data.setClass(ShopLocateSearchActivity.this, PublishItemActivity.class);
            setResult(PublishItemActivity.PARAMETER_SHOP_LOCATION_RESULT_CODE, data);
            finish();
        }
    }

    private void searchPoi(String keyword) {
        if (mPoiSearch != null) {
            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .city(IShareContext.getInstance().getUserLocation().getCity())
                    .keyword(keyword)
                    .pageNum(0)
                    .pageCapacity(pageCapacity));
        } else {
            mPoiSearch = PoiSearch.newInstance();
            mPoiSearch.setOnGetPoiSearchResultListener(poiListenr);
        }
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "shop locate destory");
        super.onDestroy();
        mPoiSearch.destroy();
    }

    class ResultAdapter extends BaseAdapter {

        LayoutInflater mLayoutInflater;

        public ResultAdapter(Context context) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.publish_shop_result_listview_item, null);
                holder = new ViewHolder();
                holder.nameTv = (TextView) convertView.findViewById(R.id.publish_shop_item_shop_name_tv);
                holder.addrTv = (TextView) convertView.findViewById(R.id.publish_shop_item_shop_addr_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.nameTv.setText(dataList.get(position).name);
            holder.addrTv.setText(dataList.get(position).address);
            return convertView;
        }

        class ViewHolder {
            public TextView nameTv;
            public TextView addrTv;
        }
    }
}
