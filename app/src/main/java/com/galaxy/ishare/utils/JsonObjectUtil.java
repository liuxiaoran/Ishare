package com.galaxy.ishare.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by liuxiaoran on 15/5/13.
 */
public class JsonObjectUtil {

    public static JSONArray parseListToJsonArray(ArrayList _list) {

        JSONArray jsonArray = new JSONArray();
        if (_list.get(0) instanceof HashMap) {
            ArrayList<HashMap<String, String>> list = _list;
            for (HashMap<String, String> singleHashMap : list) {

                JSONObject singleJSONObject = new JSONObject();

                // 遍历hashMap
                Iterator iterator = singleHashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String _key = iterator.next().toString();
                    String _value = singleHashMap.get(_key);
                    try {
                        singleJSONObject.put(_key, _value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                jsonArray.put(singleJSONObject);

            }
        } else if (_list.get(0) instanceof String) {
            for (int i = 0; i < _list.size(); i++) {
                jsonArray.put(_list.get(i).toString());
            }

        }


        return jsonArray;


    }

}
