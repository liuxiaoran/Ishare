package com.galaxy.ishare.utils;

import android.os.Handler;
import android.os.Message;
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
import com.galaxy.ishare.publishware.PoiSearchActivity;

import java.util.List;


/**
 * Created by liuxiaoran on 15/5/26.
 */
public class PoiSearchUtil {

    public static final int POI_WHAT =1;

    private static PoiSearch poiSearch;
    public static void  searchPoiInCity (String key,final Handler handler,int pageIndex){
        if (poiSearch==null){
            poiSearch = PoiSearch.newInstance();
        }

        OnGetPoiSearchResultListener poiListenr = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null
                        || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {

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

                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                     // handler 进行activity 间消息通知
                    Message m =new Message ();
                    m.what = POI_WHAT;
                    m.arg1 = result.getAllPoi().size();
                    handler.sendMessage(m);

                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        };
        poiSearch.setOnGetPoiSearchResultListener(poiListenr);
        poiSearch.searchInCity((new PoiCitySearchOption())
                .city(IShareContext.getInstance().getUserLocation().getCity())
                .keyword(key)
                .pageNum(pageIndex));
    }

    public static  void destroyPoiSearch(){
        if (poiSearch!=null){
            poiSearch.destroy();
            poiSearch = null;
        }
    }


}
