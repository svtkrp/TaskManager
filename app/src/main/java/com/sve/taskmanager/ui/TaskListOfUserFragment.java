package com.sve.taskmanager.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.sve.taskmanager.R;
import com.sve.taskmanager.Task;
import com.sve.taskmanager.TaskLab;
import com.sve.taskmanager.User;
import com.sve.taskmanager.UserLab;

import java.util.List;

public class TaskListOfUserFragment extends TaskListFragment {
    public static final String TAG = "com.sve.taskmanager.ui.TaskListOfUserFragment";
    private static final String ARG_USER_LOGIN = "user_login";

    private User mUser;

    public static Bundle newBundle(String userLogin) {
        Bundle args = new Bundle();
        args.putString(ARG_USER_LOGIN, userLogin);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String login = getArguments().getString(ARG_USER_LOGIN);
            mUser = UserLab.get(getActivity()).getUser(login);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        String title = getString(R.string.tasks_of_user_title, mUser.getName());
        activity.getSupportActionBar().setTitle(title);
    }

    @Override
    protected void updateSubtitle() {
        int taskCountAsCustomer = TaskLab.get(getActivity()).getTaskCountOfCustomer(mUser);
        int taskCountAsExecutor = TaskLab.get(getActivity()).getTaskCountOfExecutor(mUser);

        String subtitle = getString(R.string.common_task_count,
                taskCountAsCustomer, taskCountAsExecutor);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //fixme: divide as customer & as executor tasks
    @Override
    protected void updateUI() {
        List<Task> tasks = TaskLab.get(getActivity()).getTasksOfCustomer(mUser);
        tasks.addAll(TaskLab.get(getActivity()).getTasksOfExecutor(mUser));

        if (mAdapter == null) {
            mAdapter = new TaskAdapter(tasks);
            mTaskRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTasks(tasks);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();

        if (tasks.size() == 0) {
            mNewTaskButton.setVisibility(View.VISIBLE);
        } else {
            mNewTaskButton.setVisibility(View.GONE);
        }
    }
}
