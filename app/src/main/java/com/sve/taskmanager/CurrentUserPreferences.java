package com.sve.taskmanager;

import android.content.Context;
import android.preference.PreferenceManager;

public class CurrentUserPreferences {

    private static final String PREF_USER_NAME = "user_name";

    public static String getStoredUsername(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USER_NAME, null);
    }

    public static void setStoredUsername(Context context, String username) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_USER_NAME, username)
                .apply();
    }
}
