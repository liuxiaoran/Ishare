package com.galaxy.ishare.utils.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.R;

/**
 * Created by liuxiaoran on 15/5/25.
 * 全屏的加载dialog
 */
public class WaitingDialogUtil {

    private  static WaitingDialogUtil instance;
    private Context context;
    private AlertDialog alertDialog;


    public WaitingDialogUtil(Context context  ){
           this.context=context;
    }

    public static  WaitingDialogUtil getInstance (Context context ){
        if (instance ==null){
            instance  = new WaitingDialogUtil(context);
        }
        return instance;
    }

    public AlertDialog createWaitingDialog (String content){
        LayoutInflater layoutInflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  dialogView  =  layoutInflater.inflate(R.layout.waiting_dialog,null);
        TextView contentTv= (TextView) dialogView.findViewById(R.id.loading_tv);
        contentTv.setText(content);
        alertDialog  = new AlertDialog.Builder(context).setView(dialogView).create();
        return alertDialog;
    }
    public AlertDialog showFullScreenWaitingDialog(String content){

        AlertDialog  alertDialog = createWaitingDialog(content);

        alertDialog.show();

        // 设置成全屏
        WindowManager.LayoutParams params =
                alertDialog.getWindow().getAttributes();
        params.width = Global.screenWidth;
        params.height = Global.screenHeight ;
        alertDialog.getWindow().setAttributes(params);

        return alertDialog;


    }

    public AlertDialog showWaitingDialog (String content, int width,int height){
        AlertDialog alertDialog = createWaitingDialog(content);
        alertDialog.show();

        WindowManager.LayoutParams params  = alertDialog.getWindow().getAttributes();
        params.width =width;
        params.height=height;
        alertDialog.getWindow().setAttributes(params);

        return alertDialog;
    }

    public void dimissWaitingDialog (){
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
    }




}
