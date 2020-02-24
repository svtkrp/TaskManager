package com.sve.taskmanager;

import android.content.Context;

import com.sve.taskmanager.model.User;
import com.sve.taskmanager.model.UserLab;

public class SignIn {

    public static boolean isCorrect(Context context, String login) {
        UserLab userLab = UserLab.get(context);
        User user = userLab.getUser(login);
        return (user != null);
    }
}
