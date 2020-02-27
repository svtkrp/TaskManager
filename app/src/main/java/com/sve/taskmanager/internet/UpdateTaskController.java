package com.sve.taskmanager.internet;

import com.sve.taskmanager.model.Task;
import retrofit2.Call;
import retrofit2.Callback;

public class UpdateTaskController {

    public void start(Task task, Callback<Task> callback) {
        Call<Task> call = ApiImpl.getTaskApi().updateTask(task);
        call.enqueue(callback);
    }
}