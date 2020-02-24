package com.sve.taskmanager.internet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeleteUserController implements Callback<Void> {

    static final String BASE_URL = Constants.BASE_URL;

    public void start(String login) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserApi userApi = retrofit.create(UserApi.class);

        Call<Void> call = userApi.deleteUserFromDb(login);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
            //System.out.println("deleted");
        } else {
            //System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        t.printStackTrace();
    }
}
