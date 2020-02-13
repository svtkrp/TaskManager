package com.sve.taskmanager;

import android.content.Context;
import android.preference.PreferenceManager;

public class CurrentUserPreferences {

    private static final String PREF_USER_LOGIN = "user_login";

    public static String getStoredUserLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USER_LOGIN, null);
    }

    public static void setStoredUserLogin(Context context, String login) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_USER_LOGIN, login)
                .apply();
    }
}
