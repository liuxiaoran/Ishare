package com.galaxy.ishare.publishware;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.galaxy.ishare.R;
import com.galaxy.ishare.utils.ImageParseUtil;

/**
 * Created by liuxiaoran on 15/5/24.
 */
public class PreviewPictureActivity extends ActionBarActivity {

    public static final int PUBLISH_TO_PREVIEW_REQUEST_CODE=1;

    public static final String  PARAMETER_POSITION = "PARAMETER_POSITION";
    public static final String PARAMENTER_PIC_URI_STRING ="PARAMENTER_PIC_URI";

    ImageView previewIv;
    int position;
    public static final String TAG="preview";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_preview_card_img);

        Log.v(TAG,Uri.parse(getIntent().getStringExtra(PARAMENTER_PIC_URI_STRING))+"");
        Bitmap bitmap = ImageParseUtil.getBitmapFromUri(Uri.parse(getIntent().getStringExtra(PARAMENTER_PIC_URI_STRING)), getApplicationContext());
        position = getIntent().getIntExtra(PARAMETER_POSITION,0);

        previewIv = (ImageView)findViewById(R.id.publishware_preview_card_iv);
        previewIv.setImageBitmap(bitmap);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_delete){
            Intent  retIntent = new Intent(this, PublishItemActivity.class);
            retIntent.putExtra(PublishItemActivity.PARETER_DELETE_POSITION,position);
            setResult(PublishItemActivity.PARAMETER_PREVIEW_DELETE_RESULT_CODE,retIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
