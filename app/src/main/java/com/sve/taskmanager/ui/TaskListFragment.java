package com.sve.taskmanager.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sve.taskmanager.R;
import com.sve.taskmanager.Task;
import com.sve.taskmanager.TaskLab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class TaskListFragment extends Fragment {

    public static final String TAG = "com.sve.taskmanager.ui.TaskListFragment";

    protected RecyclerView mTaskRecyclerView;
    protected TaskAdapter mAdapter;

    protected Button mNewTaskButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_with_adding_new, container, false);
        mTaskRecyclerView = view.findViewById(R.id.item_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNewTaskButton = view.findViewById(R.id.new_item_button);
        mNewTaskButton.setText(R.string.add_new_task_text);
        mNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndOpenNewTask();
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_list, menu);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_task:
                addAndOpenNewTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addAndOpenNewTask() {

        Toast.makeText(getActivity(),
                "createTask(example_company){POST(example_company, TASK) - return new Task (id, date)}",
                Toast.LENGTH_LONG).show();

        Task task = TaskLab.get(getActivity()).createAndAddEmptyTask();
        Intent intent = TaskPagerActivity.newIntent(getActivity(), task.getId());
        startActivity(intent);
    }

    protected void updateSubtitle() {
        int taskCount = TaskLab.get(getActivity()).getTaskCount();

        String subtitle = getResources().getQuantityString
                (R.plurals.task_count_plural, taskCount, taskCount);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    protected void updateUI() {
        List<Task> tasks = TaskLab.get(getActivity()).getTasks();

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

    protected class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Task mTask;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.task_title);
            mDateTextView = itemView.findViewById(R.id.task_date);
            mSolvedImageView = itemView.findViewById(R.id.task_solved);
        }

        public void bind(Task task) {
            mTask = task;
            mTitleTextView.setText(mTask.getTitle());
            mDateTextView.setText(DateFormat.format(getString(R.string.long_date_format)
                    + ", '" + getString(R.string.date_time_prep)
                    + "' " + getString(R.string.time_format), mTask.getDate()));
            mSolvedImageView.setVisibility(mTask.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Intent intent = TaskPagerActivity.newIntent(getActivity(), mTask.getId());
            startActivity(intent);
        }
    }

    protected class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

        private List<Task> mTasks;

        public TaskAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        public void setTasks(List<Task> tasks) {
            mTasks = tasks;
        }
    }
}


