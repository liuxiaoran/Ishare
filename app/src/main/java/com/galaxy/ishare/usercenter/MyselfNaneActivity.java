package com.galaxy.ishare.usercenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

/**
 * Created by YangJunLin on 2015/5/23.
 */
public class MyselfNaneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = getIntent().getStringExtra("name");
        setContentView(R.layout.activity_myself_nickname);
        ImageButton nameBackButton = (ImageButton) findViewById(R.id.myself_info_name_back_image);
        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        nameBackButton.setOnClickListener(backListener);

        final EditText nameEdit = (EditText) findViewById(R.id.myself_info_name_edit);
        final Button nameSave = (Button) findViewById(R.id.myself_info_nickname_save);
        nameEdit.setText(name);
        TextWatcher nameEditWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        nameEdit.addTextChangedListener(nameEditWatcher);

        View.OnClickListener nameSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserUtils.updateUserInfo(IShareContext.getInstance().getCurrentUser().getUserPhone(), nameEdit.getText().toString(), null, null);
                finish();
            }
        };
        nameSave.setOnClickListener(nameSaveListener);

    }
}
