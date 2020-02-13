package com.sve.taskmanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sve.taskmanager.CurrentUserPreferences;
import com.sve.taskmanager.R;
import com.sve.taskmanager.Task;
import com.sve.taskmanager.TaskLab;
import com.sve.taskmanager.User;
import com.sve.taskmanager.UserLab;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class TaskFragment extends Fragment {

    private static final String ARG_TASK_ID = "task_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_USER_CUSTOMER = "DialogUserCustomer";
    private static final String DIALOG_USER_EXECUTOR = "DialogUserExecutor";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_USER_CUSTOMER = 2;
    private static final int REQUEST_USER_EXECUTOR = 3;

    private static final int[] MINUTES = {0, 8*60, 12*60, 15*60, 17*60};

    private String mCurrentUserLogin;

    private Task mTask;

    private String mTaskCustomerLogin;

    private EditText mTitleField;

    private Spinner mDateSpinner;
    private AdapterWithCustomItem mDateAdapter;
    private List<Date> mSomeDates;
    private String[] mSomeDateNames;

    private Spinner mTimeSpinner;
    private AdapterWithCustomItem mTimeAdapter;
    private String[] mTimes;

    private CheckBox mSolvedCheckBox;

    private UserLab mUserLab;

    private Spinner mCustomerSpinner;
    private AdapterWithCustomItem mCustomerAdapter;
    private List<User> mCustomers;
    private String[] mCustomerNames;
    private boolean mCustomerItemWasClicked = false;

    private Spinner mExecutorSpinner;
    private AdapterWithCustomItem mExecutorAdapter;
    private List<User> mExecutors;
    private String[] mExecutorNames;
    private boolean mExecutorItemWasClicked = false;

    private ImageButton mDeleteExecutorButton;

    private Button mReportButton;

    public static TaskFragment newInstance(UUID taskId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_ID, taskId);

        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();

        TaskLab.get(getActivity()).updateTask(mTask);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUserLogin = CurrentUserPreferences.getStoredUserLogin(getActivity());

        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        mTask = TaskLab.get(getActivity()).getTask(taskId);
        mTaskCustomerLogin = mTask.getCustomer();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        mTitleField = view.findViewById(R.id.task_title);
        mTitleField.setText(mTask.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateSpinner = view.findViewById(R.id.task_date_date);
        initDateView();

        mTimeSpinner = view.findViewById(R.id.task_date_time);
        initTimeView();

        mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // nothing happens
                } else if (position == mSomeDateNames.length - 1) {
                    FragmentManager fragmentManager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mTask.getDate());
                    dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                    dialog.show(fragmentManager, DIALOG_DATE);
                } else {
                    mTask.setDate(mergeFirstDateSecondTime(mSomeDates.get(position), mTask.getDate()));
                    updateDateView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // nothing happens
                } else if (position == mTimes.length - 1) {
                    FragmentManager fragmentManager = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mTask.getDate());
                    dialog.setTargetFragment(TaskFragment.this, REQUEST_TIME);
                    dialog.show(fragmentManager, DIALOG_TIME);
                } else {
                    mTask.setDate(changeTimeNotDate(mTask.getDate(), MINUTES[position] / 60,
                            MINUTES[position] % 60));
                    updateTimeView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSolvedCheckBox = view.findViewById(R.id.task_solved);
        mSolvedCheckBox.setChecked(mTask.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTask.setSolved(isChecked);
            }
        });

        mCustomerSpinner = view.findViewById(R.id.task_customer);
        mExecutorSpinner = view.findViewById(R.id.task_executor);
        updateCustomerExecutorView();

        mCustomerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCustomerItemWasClicked) {
                    if (position == mCustomerNames.length - 1) {
                        FragmentManager fragmentManager = getFragmentManager();
                        UserCreaterFragment dialog = UserCreaterFragment.newInstance();
                        dialog.setTargetFragment(TaskFragment.this, REQUEST_USER_CUSTOMER);
                        dialog.show(fragmentManager, DIALOG_USER_CUSTOMER);
                    } else {
                        mTask.setCustomer(mCustomers.get(position).getLogin());
                    }
                } else {
                    mCustomerItemWasClicked = true;
                }

                mCustomerAdapter.setCustomText(mUserLab.getUser(mTask.getCustomer()).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mExecutorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mExecutorItemWasClicked) {
                    if (position == 0) {
                        mTask.setExecutor(null);
                    } else if (position == mExecutorNames.length - 1) {
                        FragmentManager fragmentManager = getFragmentManager();
                        UserCreaterFragment dialog = UserCreaterFragment.newInstance();
                        dialog.setTargetFragment(TaskFragment.this, REQUEST_USER_EXECUTOR);
                        dialog.show(fragmentManager, DIALOG_USER_EXECUTOR);
                    } else {
                        mTask.setExecutor(mExecutors.get(position - 1).getLogin());
                    }
                } else {
                    mExecutorItemWasClicked = true;
                }

                if (mTask.getExecutor() != null) {
                    mDeleteExecutorButton.setVisibility(VISIBLE);
                    mExecutorAdapter.setCustomText(mUserLab.getUser(mTask.getExecutor()).getName());
                } else {
                    mDeleteExecutorButton.setVisibility(GONE);
                    mExecutorAdapter.setCustomText(getString(R.string.choose_task_executor_text));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDeleteExecutorButton = view.findViewById(R.id.task_executor_delete);
        mDeleteExecutorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutorSpinner.setSelection(0);
            }
        });

        mReportButton = view.findViewById(R.id.task_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getTaskReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.task_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        if (!mCurrentUserLogin.equals(UserLab.ADMIN_LOGIN)) {
            mCustomerSpinner.setEnabled(false);
            if (!mCurrentUserLogin.equals(mTaskCustomerLogin)) {
                mTitleField.setEnabled(false);
                mDateSpinner.setEnabled(false);
                mTimeSpinner.setEnabled(false);
                mSolvedCheckBox.setEnabled(false);
                mExecutorSpinner.setEnabled(false);
                mDeleteExecutorButton.setEnabled(false);
            }
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTask.setDate(date);
            updateDateView();

        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE);
            mTask.setDate(date);
            updateTimeView();

        } else if (requestCode == REQUEST_USER_CUSTOMER) {
            String login = (String) data.getSerializableExtra(UserCreaterFragment.EXTRA_USER_LOGIN);
            String name = (String) data.getSerializableExtra(UserCreaterFragment.EXTRA_USER_NAME);
            if ((login != null)&&(!login.equals(""))&&(name != null)&&(!name.equals(""))) {
                User user = new User(login, name);
                mUserLab.addUser(user);
                mTask.setCustomer(user.getLogin());

                updateCustomerExecutorView();
            }

        } else if (requestCode == REQUEST_USER_EXECUTOR) {
                String login = (String) data.getSerializableExtra(UserCreaterFragment.EXTRA_USER_LOGIN);
                String name = (String) data.getSerializableExtra(UserCreaterFragment.EXTRA_USER_NAME);
                if ((login != null)&&(!login.equals(""))&&(name != null)&&(!name.equals(""))) {
                    User user = new User(login, name);
                    mUserLab.addUser(user);
                    mTask.setExecutor(user.getLogin());

                    updateCustomerExecutorView();
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task, menu);

        MenuItem deleteTaskItem = menu.findItem(R.id.delete_task);
        if (!((mCurrentUserLogin.equals(UserLab.ADMIN_LOGIN))
                ||(mCurrentUserLogin.equals(mTaskCustomerLogin)))) {
            deleteTaskItem.setEnabled(false);
            deleteTaskItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_task:
                TaskLab.get(getActivity()).deleteTask(mTask);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDateView() {
        Date estDate = mTask.getDate();
        Date today = new Date();
        Date tomorrow = changeDateNotTimeBy(today, 1, 0, 0);
        Date weekLater = changeDateNotTimeBy(today, 7, 0, 0);
        Date monthLater = changeDateNotTimeBy(today, 0, 1, 0);
        mSomeDates = new ArrayList<>(5);
        mSomeDates.add(estDate);
        mSomeDates.add(today);
        mSomeDates.add(tomorrow);
        mSomeDates.add(weekLater);
        mSomeDates.add(monthLater);
        mSomeDateNames = getResources().getStringArray(R.array.dates);

        mDateAdapter = new AdapterWithCustomItem(getActivity(), mSomeDateNames);
        // spinner's find by id (in onCreateView now)
        mDateSpinner.setAdapter(mDateAdapter);
        mDateAdapter.setCustomText((String)DateFormat.format(getString(R.string.short_date_format),
                mTask.getDate()));
    }

    private void initTimeView() {
        mTimes = getResources().getStringArray(R.array.times);
        MINUTES[0] = convertHoursAndMinutesOfDateToMinutes(mTask.getDate());

        mTimeAdapter = new AdapterWithCustomItem(getActivity(), mTimes);
        // spinner's find by id (in onCreateView now)
        mTimeSpinner.setAdapter(mTimeAdapter);
        mTimeAdapter.setCustomText((String)DateFormat.format(getString(R.string.time_format),
                mTask.getDate()));
    }

    private void updateDateView() {
        mSomeDates.set(0, mTask.getDate());
        mDateSpinner.setSelection(0);
        mDateAdapter.setCustomText((String)DateFormat.format(getString(R.string.short_date_format),
                mTask.getDate()));
    }

    private void updateTimeView() {
        MINUTES[0] = convertHoursAndMinutesOfDateToMinutes(mTask.getDate());
        mTimeSpinner.setSelection(0);
        mTimeAdapter.setCustomText((String)DateFormat.format(getString(R.string.time_format),
                mTask.getDate()));
    }

    private Date changeDateNotTime(Date date, int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    private Date changeDateNotTimeBy(Date date, int days, int months, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        calendar.add(Calendar.MONTH, months);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    private Date mergeFirstDateSecondTime(Date firstDate, Date secondTime) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(firstDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(secondTime);
        int hour = calendar2.get(Calendar.HOUR_OF_DAY);
        int minute = calendar2.get(Calendar.MINUTE);
        calendar1.set(Calendar.HOUR_OF_DAY, hour);
        calendar1.set(Calendar.MINUTE, minute);
        return calendar1.getTime();
    }

    private Date changeTimeNotDate(Date date, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    private int convertHoursAndMinutesOfDateToMinutes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE);
    }

    private void updateCustomerExecutorView() {
        mUserLab = UserLab.get(getActivity());

        mCustomers = mUserLab.getUsers();
        User customer = mUserLab.getUser(mTask.getCustomer());
        mCustomerNames = new String[mCustomers.size() + 1];
        for (int i = 0; i < mCustomers.size(); i++) {
            mCustomerNames[i] = mCustomers.get(i).getName();
        }
        mCustomerNames[mCustomerNames.length - 1] = getString(R.string.add_new_user_text);

        mCustomerAdapter = new AdapterWithCustomItem(getActivity(), mCustomerNames);
        mCustomerSpinner.setAdapter(mCustomerAdapter);

        for (int j = 0; j < mCustomers.size(); j++) {
            if (customer.equals(mCustomers.get(j))) {
                mCustomerSpinner.setSelection(j);
                break;
            }
        }

        mExecutors = mUserLab.getUsersWithoutAdmin();
        User executor;
        if (mTask.getExecutor() == null) {
            executor = null;
        } else {
            executor = mUserLab.getUser(mTask.getExecutor());
        }

        mExecutorNames = new String[mExecutors.size() + 2];
        for (int i = 0; i < mExecutors.size(); i++) {
            mExecutorNames[i + 1] = mExecutors.get(i).getName();
        }
        mExecutorNames[0] = getString(R.string.no_task_executor_text);
        mExecutorNames[mExecutorNames.length - 1] = getString(R.string.add_new_user_text);

        mExecutorAdapter = new AdapterWithCustomItem(getActivity(), mExecutorNames);
        mExecutorSpinner.setAdapter(mExecutorAdapter);

        if (executor == null) {
            mExecutorSpinner.setSelection(0);
        } else {
            for (int j = 0; j < mExecutors.size(); j++) {
                if (executor.equals(mExecutors.get(j))) {
                    mExecutorSpinner.setSelection(j + 1);
                    break;
                }
            }
        }
    }

    private String getTaskReport() {

        String solvedString;
        if (mTask.isSolved()) {
            solvedString = getString(R.string.task_report_solved);
        } else {
            solvedString = getString(R.string.task_report_unsolved);
        }

        String dateString = DateFormat.format(getString(R.string.time_format)
                + ", " + getString(R.string.long_date_format), mTask.getDate()).toString();

        mUserLab = UserLab.get(getActivity());
        String customerName = mUserLab.getUser(mTask.getCustomer()).getName();
        String executorString;
        if (mTask.getExecutor() == null) {
            executorString = getString(R.string.task_report_no_executor);
        } else {
            executorString = getString(R.string.task_report_executor,
                    mUserLab.getUser(mTask.getExecutor()).getName());
        }

        String report = getString(R.string.task_report_text,
                mTask.getTitle(), dateString, solvedString, customerName, executorString);
        return report;
    }

    private class AdapterWithCustomItem extends ArrayAdapter<String>
    {
        private String[] mOptions;

        private String mCustomText = "";

        public AdapterWithCustomItem(Context context, String[] options) {
            super(context, android.R.layout.simple_spinner_dropdown_item, options);
            mOptions = options;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(mCustomText);

            return view;
        }

        public void setCustomText(String customText) {
            mCustomText = customText;
            notifyDataSetChanged();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return super.getDropDownView(position, convertView, parent);
        }
    }
}
