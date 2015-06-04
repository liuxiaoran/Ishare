package com.galaxy.ishare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.login.LoginActivity;
import com.galaxy.ishare.main.MainActivity;

import java.util.ArrayList;


/**
 * Created by liuxiaoran on 15/4/28.
 */
public class SplashActivity extends Activity {

    private  static final String TAG ="splashactivity";
    private ViewPager viewPager;
    //装分页显示的view的数组
    private ArrayList<View> pageViews;
    private ImageView imageView;
    //将小圆点的图片用数组表示
    private ImageView[] imageViews;
    //包裹滑动图片的LinearLayout
    private ViewGroup viewPics;
    //包裹小圆点的LinearLayout
    private ViewGroup viewPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (IShareContext.getInstance().getCurrentUser()==null){
            // 第一次进入系统
            //将要分页显示的View装入数组中
            setContentView(R.layout.activity_guide);
            LayoutInflater inflater=getLayoutInflater();
            pageViews=new ArrayList<>();
            pageViews.add(inflater.inflate(R.layout.viewpager_page1,null));
            pageViews.add(inflater.inflate(R.layout.viewpager_page2,null));
//            pageViews.add(inflater.inflate(R.layout.viewpager_page3,null));
            //创建imageview数组
            imageViews=new ImageView[pageViews.size()];
//            //从指定的布局文件中加载view
//            viewPics=(ViewGroup)inflater.inflate(, null);
            viewPoints= (ViewGroup) findViewById(R.id.viewGroup);
            viewPager= (ViewPager) findViewById(R.id.guidePages);
            for (int i=0;i<pageViews.size();i++){
                imageView=new ImageView(SplashActivity.this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(20,20));//new一个新布局
                imageView.setPadding(20,0,20,0);
                imageViews[i]=imageView;
                if(i==0){
                    imageViews[i].setBackgroundResource(R.drawable.dot1_w);
                }else{
                    imageViews[i].setBackgroundResource(R.drawable.dot2_w);
                }
                viewPoints.addView(imageViews[i]);
            }

            viewPager.setAdapter(new PagerAdapter() {
                @Override
                public Object instantiateItem(View v,int position){
//                    container.addView(pageViews.get(position));
//                    return pageViews.get(position);
                    ((ViewPager) v).addView(pageViews.get(position));
                    if(position==1){
                        Button btn=(Button)v.findViewById(R.id.btn_guide);
                        btn.setOnClickListener(Button_OnClickListener);
                    }
                    return pageViews.get(position);
                }

                @Override
                public int getCount() {
                    return pageViews.size();
                }

                @Override
                public void destroyItem(ViewGroup container,int position,Object object){
                    container.removeView(pageViews.get(position));
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view==object;
                }
            });
            viewPager.setOnPageChangeListener(new GuidePageChangeListener());

//            Intent  intent = new Intent (this, LoginActivity.class);
//            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //设置已经引导
            setGuided();

            //跳转
            Intent mIntent = new Intent();
            mIntent.setClass(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(mIntent);
            SplashActivity.this.finish();
//            Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
//            startActivity(intent);
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
                imageViews[position].setBackgroundResource(R.drawable.dot1_w);
                if(position !=i){
                    imageViews[i].setBackgroundResource(R.drawable.dot2_w);
                }
            }

        }
    }

}
