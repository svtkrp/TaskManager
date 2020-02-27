package com.sve.taskmanager.internet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sve.taskmanager.model.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateTaskController implements Callback<Task> {
    static final String BASE_URL = Constants.BASE_URL;

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TaskApi taskApi = retrofit.create(TaskApi.class);

        Call<Task> call = taskApi.createTask();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Task> call, Response<Task> response) {
        if (response.isSuccessful()) {
            Task task = response.body();
            /*System.out.println("id:");
            System.out.println(task.getId());
            System.out.println("title:");
            System.out.println(task.getTitle());
            System.out.println("date:");
            System.out.println(task.getDate());*/
        } else {
            //System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<Task> call, Throwable t) {
        t.printStackTrace();
    }
}