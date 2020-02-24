package com.sve.taskmanager.internet;

import com.sve.taskmanager.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    @GET("users/")
    Call<List<User>> downloadUsers();

    @GET("users/exist/{login}/")
    Call<Boolean> checkUser(@Path("login") String login);

    @PUT("users/")
    Call<User> createUser(@Body User user);

    @DELETE("users/{login}/")
    Call<Void> deleteUserFromDb(@Path("login") String login);
}
