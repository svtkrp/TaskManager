package com.sve.taskmanager.internet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiImpl {

    public static TaskApi getTaskApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(TaskApi.class);
    }

    public static UserApi getUserApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(UserApi.class);
    }
}