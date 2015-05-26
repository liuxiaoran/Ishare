package com.galaxy.ishare.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liuxiaoran on 15/5/24.
 */
public class ImageParseUtil {

    public static Bitmap getBitmapFromUri(Uri uri,Context context){
        Bitmap  bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            Log.v("preview",e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }

    public static  void saveBitmapToFile(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public  static Bitmap getBitmapFromResource (Context context,int res){
        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), res);
        return bitmap;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
               bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
              return baos.toByteArray();
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }




}
