package com.sve.taskmanager.internet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sve.taskmanager.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadUsersController implements Callback<List<User>> {

    static final String BASE_URL = Constants.BASE_URL;

    public void start() {
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
            //usersList.forEach(user -> System.out.println(user.getLogin()));
        } else {
            //System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        t.printStackTrace();
    }
}
