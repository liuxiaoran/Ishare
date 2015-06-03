package com.galaxy.ishare.chat;

/**
 * Created by Zhan on 2015/6/2.
 */
public class CardTypeUtil {

    public static String getCardType(int type) {
        String result = null;
        switch(type) {
            case 0: result = "充值卡"; break;
            case 1: result = "会员卡"; break;
            case 2:
        }
        return result;
    }
}
