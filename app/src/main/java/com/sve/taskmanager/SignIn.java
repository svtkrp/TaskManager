package com.sve.taskmanager;

import android.content.Context;

public class SignIn {

    public static boolean isCorrect(Context context, String login) {
        UserLab userLab = UserLab.get(context);
        User user = userLab.getUser(login);
        return (user != null);
    }
}
