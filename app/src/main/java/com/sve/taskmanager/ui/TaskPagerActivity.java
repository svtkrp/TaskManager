package com.sve.taskmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sve.taskmanager.R;
import com.sve.taskmanager.Task;
import com.sve.taskmanager.TaskLab;

import java.util.List;
import java.util.UUID;

public class TaskPagerActivity extends AppCompatActivity {
    private static final String EXTRA_TASK_ID = "task_id";

    private ViewPager mViewPager;
    private List<Task> mTasks;

    private Button mFirstTaskButton;
    private Button mLastTaskButton;

    public static Intent newIntent (Context packageContext, UUID taskId) {
        Intent intent = new Intent(packageContext, TaskPagerActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_pager);

        UUID taskId = (UUID) getIntent().getSerializableExtra(EXTRA_TASK_ID);

        mViewPager = findViewById(R.id.task_view_pager);

        mTasks = TaskLab.get(this).getTasks();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Task task = mTasks.get(position);
                return TaskFragment.newInstance(task.getId());
            }

            @Override
            public int getCount() {
                return mTasks.size();
            }
        });

        mFirstTaskButton = findViewById(R.id.first_task_button);
        mFirstTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mLastTaskButton = findViewById(R.id.last_task_button);
        mLastTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mTasks.size() - 1);
            }
        });

        for (int position = 0; position < mTasks.size(); position++) {
            if (mTasks.get(position).getId().equals(taskId)) {
                mViewPager.setCurrentItem(position);
                if (position == 0) {
                    mFirstTaskButton.setEnabled(false);
                }
                if (position == mTasks.size() - 1) {
                    mLastTaskButton.setEnabled(false);
                }
                break;
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    mFirstTaskButton.setEnabled(false);
                } else {
                    mFirstTaskButton.setEnabled(true);
                }
                if (position == mTasks.size() - 1) {
                    mLastTaskButton.setEnabled(false);
                } else {
                    mLastTaskButton.setEnabled(true);
                }
            }
        });
    }

}
