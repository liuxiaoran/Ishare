//package com.galaxy.ishare.mapLBS;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import com.baidu.lbsapi.BMapManager;
//import com.baidu.lbsapi.panoramaview.PanoramaView;
//import com.galaxy.ishare.R;
//
//
//public class PanoramActivity extends Activity {
//    private static final String LTAG = "PanoramActivity";
//    private PanoramaView mPanoView;
//    private LinearLayout mTopLayout;
//    private LinearLayout mLonlatLayout;
//    private TextView mTitleText;
//    private EditText mEdit;
//    private Button mSwitchBtn;
//    private EditText mEditLon;
//    private EditText mEditLat;
//    Double latitude;
//    Double longitude;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_card_panorama);
//        latitude = getIntent().getExtras().getDouble("lat");
//        longitude = getIntent().getExtras().getDouble("lon");
//        DemoApplication app = (DemoApplication) this.getApplication();
//        if (app.mBMapManager == null) {
//            app.mBMapManager = new BMapManager(app);
//
//            app.mBMapManager.init(new DemoApplication.MyGeneralListener());
//        }
//        mPanoView.setPanorama(longitude, latitude);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mPanoView.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mPanoView.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        mPanoView.destroy();
//        super.onDestroy();
//    }
//}
