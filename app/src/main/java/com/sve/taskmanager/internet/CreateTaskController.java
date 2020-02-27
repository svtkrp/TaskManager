package com.sve.taskmanager.internet;

import com.sve.taskmanager.model.Task;
import retrofit2.Call;
import retrofit2.Callback;

public class CreateTaskController {

    public void start(Callback<Task> callback) {
        Call<Task> call = ApiImpl.getTaskApi().createTask();
        call.enqueue(callback);
    }
}