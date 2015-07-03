package com.galaxy.ishare.usercenter;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.me.CardICollectActivity;
import com.galaxy.ishare.me.CardIshareActivity;
import com.galaxy.ishare.me.CardRequestActivity;
import com.galaxy.ishare.utils.PhoneUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends Fragment {

    private View myself;
    private TextView nameTv;
    private CircleImageView avatarIV;

    private RelativeLayout cardIShareLayout;
    private RelativeLayout cardRequestLayout;
    private RelativeLayout cardICollectLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        LayoutInflater lf = LayoutInflater.from(getActivity());
        myself = lf.inflate(R.layout.activity_myself, container, false);

        initViews(myself);
        writeValueToWidget();

        final RelativeLayout myselfInfo = (RelativeLayout) myself.findViewById(R.id.myself_info_layout);
        View.OnClickListener myselfInfoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        };
        myselfInfo.setOnClickListener(myselfInfoListener);

        return myself;
    }

    private void initViews(View view) {
        nameTv = (TextView) view.findViewById(R.id.usercenter_nickname_tv);
        avatarIV = (CircleImageView) view.findViewById(R.id.usercenter_avatar_iv);
        cardICollectLayout = (RelativeLayout) view.findViewById(R.id.usercenter_i_collect_layout);
        cardIShareLayout = (RelativeLayout) view.findViewById(R.id.usercenter_i_share_layout);
        cardRequestLayout = (RelativeLayout) view.findViewById(R.id.usercenter_i_request_layout);

        cardICollectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardICollectActivity.class);
                startActivity(intent);
            }
        });

        cardIShareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardIshareActivity.class);
                startActivity(intent);
            }
        });

        cardRequestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void writeValueToWidget() {
        nameTv.setText(IShareContext.getInstance().getCurrentUser().getUserName());
        ImageLoader.getInstance().displayImage(IShareContext.getInstance().getCurrentUser().getAvatar(), avatarIV, null, null);

    }


}
