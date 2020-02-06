package com.sve.taskmanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.sve.taskmanager.R;
import com.sve.taskmanager.TaskLab;
import com.sve.taskmanager.User;
import com.sve.taskmanager.UserLab;

import java.util.List;

public class UserListFragment extends Fragment {

    private static final String DIALOG_USER = "DialogUser";
    private static final int REQUEST_USER = 0;

    private RecyclerView mUserRecyclerView;
    private UserAdapter mAdapter;

    private Button mNewUserButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_with_adding_new, container, false);
        mUserRecyclerView = view.findViewById(R.id.item_recycler_view);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNewUserButton = view.findViewById(R.id.new_item_button);
        mNewUserButton.setText(R.string.add_new_user_button);
        mNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewUser();
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
        inflater.inflate(R.menu.fragment_user_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_user:
                addNewUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNewUser() {
        FragmentManager fragmentManager = getFragmentManager();
        UserCreaterFragment dialog = UserCreaterFragment.newInstance();
        dialog.setTargetFragment(UserListFragment.this, REQUEST_USER);
        dialog.show(fragmentManager, DIALOG_USER);
    }

    private void deleteUser(User user) {
        UserLab.get(getActivity()).deleteUser(user, TaskLab.get(getActivity()));
        updateUI();
    }

    private void updateSubtitle() {
        int userCount = UserLab.get(getActivity()).getUserCount();
        String subtitle = getResources().getQuantityString
                (R.plurals.user_count_plural, userCount, userCount);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        List<User> users = UserLab.get(getActivity()).getUsers();

        mAdapter = new UserAdapter(users);
        mUserRecyclerView.setAdapter(mAdapter);

        updateSubtitle();

        if (users.size() == 0) {
            mNewUserButton.setVisibility(View.VISIBLE);
        } else {
            mNewUserButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_USER) {
            String name = (String) data.getSerializableExtra(UserCreaterFragment.EXTRA_USER_NAME);
            if ((name != null)&&(!name.equals(""))) {
                User user = new User();
                user.setName(name);
                UserLab.get(getActivity()).addUser(user);
                updateUI();
            }

        }
    }

    private class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User mUser;
        private TextView mNameTextView;
        private TextView mTaskCountAsCustomerTextView;
        private TextView mTaskCountAsExecutorTextView;
        private ImageButton mDeleteUserButton;

        public UserHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_user, parent, false));

            itemView.setOnClickListener(this);

            mNameTextView = itemView.findViewById(R.id.user_name);
            mTaskCountAsCustomerTextView = itemView.findViewById(R.id.customer_task_count);
            mTaskCountAsExecutorTextView = itemView.findViewById(R.id.executor_task_count);
            mDeleteUserButton = itemView.findViewById(R.id.delete_user);
            mDeleteUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUser(mUser);
                }
            });
        }

        public void bind(User user) {
            mUser = user;
            mNameTextView.setText(mUser.getName());

            int taskCountAsCustomer = TaskLab.get(getActivity()).getTaskCountOfCustomer(mUser);
            String taskCountAsCustomerText = getString(R.string.task_count_as_customer_text,
                    getResources().getQuantityString(R.plurals.task_count_plural,
                            taskCountAsCustomer, taskCountAsCustomer));
            mTaskCountAsCustomerTextView.setText(taskCountAsCustomerText);

            if (mUser.equals(UserLab.ADMIN)) {
                mDeleteUserButton.setClickable(false);
                mDeleteUserButton.setVisibility(View.INVISIBLE);
                mTaskCountAsExecutorTextView.setVisibility(View.GONE);
            } else {
                int taskCountAsExecutor = TaskLab.get(getActivity()).getTaskCountOfExecutor(mUser);
                String taskCountAsExecutorText = getString(R.string.task_count_as_executor_text,
                        getResources().getQuantityString(R.plurals.task_count_plural,
                                taskCountAsExecutor, taskCountAsExecutor));
                mTaskCountAsExecutorTextView.setText(taskCountAsExecutorText);
            }
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = TaskListOfUserFragment.newBundle(mUser.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.action_nav_users_to_nav_tasks_of_user, bundle);
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UserHolder> {

        private List<User> mUsers;

        public UserAdapter(List<User> users) {
            mUsers = users;
        }

        @Override
        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new UserHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(UserHolder holder, int position) {
            User user = mUsers.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }
    }
}
