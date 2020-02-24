package com.sve.taskmanager.internet;

import com.sve.taskmanager.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskApi {

    @GET("tasks/")
    Call<List<Task>> downloadTasks();

    @POST("tasks/")
    Call<Task> createTask();

    @PUT("tasks/")
    Call<Task> updateTask(@Body Task task);

    @DELETE("tasks/{id}/")
    Call<Void> deleteTaskFromDb(@Path("id") Long id);
}
