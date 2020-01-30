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
import java.util.UUID;

public class TaskListOfUserFragment extends TaskListFragment {
    public static final String TAG = "com.sve.taskmanager.ui.TaskListOfUserFragment";
    private static final String ARG_USER_ID = "user_id";

    private User mUser;

    public static Bundle newBundle(UUID userId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            UUID userId = (UUID) getArguments().getSerializable(ARG_USER_ID);
            mUser = UserLab.get(getActivity()).getUser(userId);
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
        int taskCount = TaskLab.get(getActivity()).getTaskCountOfUser(mUser);

        String subtitle = getResources().getQuantityString
                (R.plurals.subtitle_plural, taskCount, taskCount);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    protected void updateUI() {
        List<Task> tasks = TaskLab.get(getActivity()).getTasksOfUser(mUser);

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
