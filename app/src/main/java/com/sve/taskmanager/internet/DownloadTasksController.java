package com.sve.taskmanager.internet;

import com.sve.taskmanager.model.Task;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class DownloadTasksController {

    public void start(Callback<List<Task>> callback) {
        Call<List<Task>> call = ApiImpl.getTaskApi().downloadTasks();
        call.enqueue(callback);
    }
}