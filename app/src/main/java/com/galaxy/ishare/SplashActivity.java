package com.galaxy.ishare;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.login.LoginActivity;
import com.galaxy.ishare.main.MainActivity;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/4/28.
 */
public class SplashActivity extends Activity {


    public static final String TAG = "splashactivity";
    private ViewPager viewPager;

    /**装分页显示的view的数组*/
    private ArrayList<View> pageViews;
    private ImageView imageView;

    /**将小圆点的图片用数组表示*/
    private ImageView[] imageViews;

    //包裹滑动图片的LinearLayout
    private ViewGroup viewPics;

    //包裹小圆点的LinearLayout
    private ViewGroup viewPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        if (IShareContext.getInstance().getCurrentUser()==null){
            // 第一次进入系统
            LayoutInflater inflater = getLayoutInflater();
            pageViews = new ArrayList<View>();
            pageViews.add(inflater.inflate(R.layout.viewpager_page1, null));
            pageViews.add(inflater.inflate(R.layout.viewpager_page2, null));
            imageViews = new ImageView[pageViews.size()];
            //从指定的XML文件加载视图
            viewPics = (ViewGroup) inflater.inflate(R.layout.activity_guide, null);
            viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
            viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);

            for(int i=0;i<pageViews.size();i++){
                imageView = new ImageView(SplashActivity.this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(20,20));
                imageView.setPadding(20, 0, 20, 0);
                imageViews[i] = imageView;

                if(i==0){
                    imageViews[i].setBackgroundResource(R.drawable.dot2_w);
                }else{
                    imageViews[i].setBackgroundResource(R.drawable.dot1_w);
                }
                viewPoints.addView(imageViews[i]);
            }

            setContentView(viewPics);

            //设置viewpager的适配器和监听事件
            viewPager.setAdapter(new GuidePageAdapter());
            viewPager.setOnPageChangeListener(new GuidePageChangeListener());
            viewPager.setPageTransformer(true,new DepthPageTransformer());

        }else {
            Intent intent2 = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent2);
            SplashActivity.this.finish();
        }
    }
    private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            setGuided();

            //跳转
            Intent mIntent = new Intent();
            mIntent.setClass(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(mIntent);
            SplashActivity.this.finish();
        }
    };

    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }


    class GuidePageAdapter extends PagerAdapter {

        //销毁position位置的界面
        @Override
        public void destroyItem(View v, int position, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager)v).removeView(pageViews.get(position));

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        //获取当前窗体界面数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pageViews.size();
        }

        //初始化position位置的界面
        @Override
        public Object instantiateItem(View v, int position) {
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(pageViews.get(position));

            // 测试页卡1内的按钮事件
            if (position == 1) {
                Button btn = (Button) v.findViewById(R.id.btn_guide);
                btn.setOnClickListener(Button_OnClickListener);
            }

            return pageViews.get(position);
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }



        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }


    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<imageViews.length;i++){
                imageViews[position].setBackgroundResource(R.drawable.dot2_w);
                //不是当前选中的page，其小圆点设置为未选中的状态
                if(position !=i){
                    imageViews[i].setBackgroundResource(R.drawable.dot1_w);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    public class DepthPageTransformer implements ViewPager.PageTransformer{
        private static final float MIN_SCALE=0.75f;
        @Override
        public void transformPage(View view, float position) {
            int pageWidth=view.getWidth();

            if(position<-1){
                view.setAlpha(0);
            }else if (position<=0){
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            }else if (position<=1){
                view.setAlpha(1-position);
                view.setTranslationX(pageWidth*-position);
                float scaleFactor=MIN_SCALE+(1-MIN_SCALE)*(1-Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }else {
                view.setAlpha(0);
            }

        }
    }
}
