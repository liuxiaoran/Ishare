package com.galaxy.ishare.utils;

import android.content.SharedPreferences;

import com.galaxy.ishare.AppConst;
import com.galaxy.ishare.Global;

public class SPUtil {

	private static SharedPreferences sp = Global.mContext.getSharedPreferences(AppConst.SP_FILE, 0);
	
	public static boolean getNewInstall() {
		return sp.getBoolean(AppConst.SP_KEY_NEW_INSTALL, true);
	}
	
	public static void setNewInstall(boolean value) {
		sp.edit().putBoolean(AppConst.SP_KEY_NEW_INSTALL, value).commit();
	}

	
	public static boolean getChooseFav() {
		return sp.getBoolean(AppConst.SP_KEY_CHOOSE_FAV, false);
	}
	
	public static void setChooseFav(boolean value) {
		sp.edit().putBoolean(AppConst.SP_KEY_CHOOSE_FAV, value).commit();
	}

	public static void setMobileNo(String mobile) {
		sp.edit().putString(AppConst.SP_KEY_MOIBLE_NO, mobile).commit();
	}
	public static String getMobileNo() {
		return sp.getString(AppConst.SP_KEY_MOIBLE_NO, "");
	}

}
