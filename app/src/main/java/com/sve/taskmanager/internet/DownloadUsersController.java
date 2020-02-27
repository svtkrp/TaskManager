package com.sve.taskmanager.internet;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sve.taskmanager.model.User;
import com.sve.taskmanager.model.UserLab;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadUsersController implements Callback<List<User>> {

    private static final String BASE_URL = Constants.BASE_URL;

    private Context mContext;

    public void start(Context context) {
        mContext = context;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserApi userApi = retrofit.create(UserApi.class);

        Call<List<User>> call = userApi.downloadUsers();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if (response.isSuccessful()) {
            List<User> usersList = response.body();
            Toast.makeText(mContext, "users were received", Toast.LENGTH_LONG).show();
            UserLab.get(mContext).addAll(usersList);
        } else {
            Toast.makeText(mContext, "error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        Toast.makeText(mContext, "failed", Toast.LENGTH_LONG).show();
    }
}
