package com.sve.taskmanager;

import android.content.Context;

public class SignIn {

    public static boolean isCorrect(Context context, String username) {
        UserLab userLab = UserLab.get(context);
        User user = userLab.getUser(username);
        return (user != null);
    }
}
