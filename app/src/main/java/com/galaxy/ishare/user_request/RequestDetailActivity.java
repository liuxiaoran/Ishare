package com.galaxy.ishare.user_request;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.chat.ChatManager;
import com.galaxy.ishare.constant.PicConstant;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/6/12.
 */
public class RequestDetailActivity extends IShareActivity {

    public static final String PARAMETER_REQUEST = "PARAMETER_REQUEST";
    CardRequest cardRequest;
    int picNumber;
    // viewpager 中的ImageView
    private ImageView[] picIvs;

    private ArrayList<View> pagerList;
    private ViewPager cardPager;
    private TextView shopNameTv, shopDistanceTv, cardTypeTv, shopAddrTv, requesterNameTv, requesterDistanceTv, descriptionTv, timeTv;
    private FButton contactBtn;
    private CircleImageView avatarIv;
    //    private FloatingActionButton mapBtn;
    private LinearLayout requestViewPagerDotsLayout;
    private ImageView[] dotsIvs;
    private int lastChooseDot;
    private ImageView genderIconIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_detail_activity);
        IShareContext.getInstance().createActionbar(this, true, "请求详情");
        cardRequest = (CardRequest) getIntent().getSerializableExtra(PARAMETER_REQUEST);
//        if (cardItem.cardImgs != null) {
//            picNumber = cardItem.cardImgs.length;
//        }

        picIvs = new ImageView[picNumber > 1 ? picNumber : 1];
        dotsIvs = new ImageView[picNumber > 1 ? picNumber : 1];
        pagerList = new ArrayList<>();
        initViews();

        initCardPager();

        writeValueToViews();

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳到聊天
                User currentUser = IShareContext.getInstance().getCurrentUser();
                ChatManager.getInstance().startActivityFromRequest(cardRequest.id, cardRequest.requesterId, cardRequest.requesterGender,
                        currentUser.getUserId());
            }
        });
        // 创建viewpager dots  imgeviews 加入layout
        for (int i = 0; i < picIvs.length; i++) {
            dotsIvs[i] = new ImageView(this);
            LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(this, 6), DisplayUtil.dip2px(this, 6));
            dotsIvs[i].setLayoutParams(imageViewParams);
            dotsIvs[i].setImageResource(R.drawable.white_dot_transparent);
            requestViewPagerDotsLayout.addView(dotsIvs[i]);
        }
        dotsIvs[0].setImageResource(R.drawable.white_dot);

        if (IShareContext.getInstance().getCurrentUser().getUserId().equals(cardRequest.requesterId)) {
            contactBtn.setVisibility(View.INVISIBLE);
        }
        cardPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dotsIvs[lastChooseDot].setImageResource(R.drawable.white_dot_transparent);
                lastChooseDot = position;
                dotsIvs[position].setImageResource(R.drawable.white_dot);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViews() {
        cardPager = (ViewPager) findViewById(R.id.request_detail_viewpager);
        shopNameTv = (TextView) findViewById(R.id.request_detail_shop_name_tv);
        shopDistanceTv = (TextView) findViewById(R.id.request_detail_shop_distance_tv);
        cardTypeTv = (TextView) findViewById(R.id.request_detail_card_type_tv);
        shopAddrTv = (TextView) findViewById(R.id.request_detail_shop_addr_tv);
        avatarIv = (CircleImageView) findViewById(R.id.request_detail_requester_avatar_iv);
        requesterNameTv = (TextView) findViewById(R.id.request_detail_requester_name_tv);
        requesterDistanceTv = (TextView) findViewById(R.id.request_detail_requester_distance_tv);
        descriptionTv = (TextView) findViewById(R.id.request_detail_description_tv);
        timeTv = (TextView) findViewById(R.id.request_detail_time_tv);
        contactBtn = (FButton) findViewById(R.id.request_detail_contact_btn);
//        mapBtn = (FloatingActionButton) findViewById(R.id.request_detail_map_floating_btn);
        requestViewPagerDotsLayout = (LinearLayout) findViewById(R.id.request_detail_viewpager_dots_layout);
        genderIconIv = (ImageView) findViewById(R.id.request_detail_requester_gender_iv);
    }

    private void writeValueToViews() {
        shopNameTv.setText(cardRequest.shopName);
        shopDistanceTv.setText(cardRequest.shopDistance + "");
        String[] cardTypes = getResources().getStringArray(R.array.trade_items);
        cardTypeTv.setText(cardTypes[cardRequest.tradeType]);
        shopAddrTv.setText(cardRequest.shopLocation);

        ImageLoader.getInstance().displayImage(QiniuUtil.getInstance().getFileThumbnailUrl(cardRequest.requesterAvatar, 60, 60), avatarIv);
        requesterNameTv.setText(cardRequest.requesterName);
        requesterDistanceTv.setText(cardRequest.requesterDistance + "");
        descriptionTv.setText(cardRequest.description);
        if (cardRequest.description.equals("")) {
            descriptionTv.setText("他很懒，没有留下描述");
        }
        if (cardRequest.requesterGender.equals("男")) {
            genderIconIv.setImageResource(R.drawable.icon_male);
        }
        timeTv.setText(cardRequest.publishTime.split(" ")[0]);
    }

    private void initCardPager() {
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < picNumber; i++) {
            View view = inflater.inflate(R.layout.share_item_detail_viewpager, null);
            picIvs[i] = (ImageView) view.findViewById(R.id.share_item_detail_card_pager_iv);

            final int finalI = i;
//            ImageLoader.getInstance().displayImage(cardItem.cardImgs[i], picIvs[finalI], IShareApplication.defaultOptions);
            pagerList.add(view);
        }
        if (picNumber == 0) {
            View view = inflater.inflate(R.layout.share_item_detail_viewpager, null);
            picIvs[0] = (ImageView) view.findViewById(R.id.share_item_detail_card_pager_iv);

            final int finalI = 0;
            ImageLoader.getInstance().displayImage(PicConstant.defaultPic, picIvs[finalI], IShareApplication.defaultOptions);
            pagerList.add(view);
        }

        MyPagerAdapter pagerAdapter = new MyPagerAdapter();
        cardPager.setAdapter(pagerAdapter);


    }

    public class MyPagerAdapter extends android.support.v4.view.PagerAdapter {

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerList.get(position), 0);//添加页卡
            return pagerList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(pagerList.get(position));
        }
    }

}
