package com.sve.taskmanager.internet;

import com.sve.taskmanager.model.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class DownloadUsersController {

    public void start(Callback<List<User>> callback) {
        Call<List<User>> call = ApiImpl.getUserApi().downloadUsers();
        call.enqueue(callback);
    }
}