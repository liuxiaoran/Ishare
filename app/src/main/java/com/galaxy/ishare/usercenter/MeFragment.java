package com.galaxy.ishare.usercenter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

public class MeFragment extends Fragment {

    private View myself;
    private static User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String phone = IShareContext.getInstance().getCurrentUser().getUserPhone();
        final String key = IShareContext.getInstance().getCurrentUser().getKey();

        LayoutInflater lf = LayoutInflater.from(getActivity());
        myself = lf.inflate(R.layout.activity_myself, container, false);

        final LinearLayout myselfInfo = (LinearLayout) myself.findViewById(R.id.myself_info);
        View.OnClickListener myselfInfoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyselfInfoActivity.class);
                startActivity(intent);
            }
        };
        myselfInfo.setOnClickListener(myselfInfoListener);
        TextView myphone = (TextView) myself.findViewById(R.id.myself_info_phone);
        getUser();
        myphone.setText(phone);
        return myself;
    }

    private void getUser() {
        HttpTask.startAsyncDataGetRequset(URLConstant.QUERY_USER, null, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
                User userInfo = new User();
                int status = 0;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");
                    if (status == 0) {
                        JSONObject tmp = jsonObject.getJSONObject("data");
                        userInfo.setUserName(tmp.getString("nickname"));
                        userInfo.setAvatar(tmp.getString("avatar"));
                        userInfo.setUserPhone(tmp.getString("phone"));
                        userInfo.setGender(tmp.getString("gender"));
                        userInfo.setUserId(tmp.getString("open_id"));
                    }
                    final TextView myname = (TextView) myself.findViewById(R.id.myself_info_name);
                    myname.setText(userInfo.getUserName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.e(this.getClass().getName(), retCode.toString());
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
