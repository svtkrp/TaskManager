package com.sve.taskmanager.internet;

import retrofit2.Call;
import retrofit2.Callback;

public class DeleteTaskController {

    public void start(Long id, Callback<Void> callback) {
        Call<Void> call = ApiImpl.getTaskApi().deleteTaskFromDb(id);
        call.enqueue(callback);
    }
}