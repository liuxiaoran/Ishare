package com.galaxy.util.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuxiaoran on 15/4/25.
 */
public class CheckInfoValidity {

    private static CheckInfoValidity instance;

    public static CheckInfoValidity getInstance() {

        if (instance == null) {
            instance = new CheckInfoValidity();
        }
        return instance;
    }

    public boolean phonePatternMatch(String str) {

        if (str == null) {
            return false;
        }
        boolean ret = false;
        Pattern pattern = Pattern.compile("[0-9]{11}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public boolean pwPatternMatch(String str) {
        if (str == null) {
            return false;
        }

        boolean ret = true;
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{6,16}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            ret = false;
        }
        return ret;

    }
}
