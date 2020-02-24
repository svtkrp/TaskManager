package com.sve.taskmanager.internet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sve.taskmanager.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadTasksController implements Callback<List<Task>> {
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

        Call<List<Task>> call = taskApi.downloadTasks();
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
        if (response.isSuccessful()) {
            List<Task> tasksList = response.body();
            //tasksList.forEach(task -> System.out.println(task.getDate()));
        } else {
            //System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Task>> call, Throwable t) {
        t.printStackTrace();
    }
}
