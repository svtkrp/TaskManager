package com.sve.taskmanager;

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
    private static final String DIALOG_USER = "DialogUser";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_USER = 2;

    private static final int[] MINUTES = {0, 8*60, 12*60, 15*60, 17*60};

    private Task mTask;
    private EditText mTitleField;

    private Spinner mDateSpinner;
    private AdapterWithCustomItem mDateAdapter;
    private List<Date> mSomeDates;
    private String[] mSomeDateNames;

    private Spinner mTimeSpinner;
    private AdapterWithCustomItem mTimeAdapter;
    private String[] mTimes;

    private CheckBox mSolvedCheckBox;

    private Spinner mUserSpinner;
    private AdapterWithCustomItem mUserAdapter;
    private UserLab mUserLab;
    private List<User> mUsers;
    private String[] mUserNames;
    private boolean mUserItemWasClicked = false;

    private ImageButton mDeleteUserButton;
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
        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        mTask = TaskLab.get(getActivity()).getTask(taskId);

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

        mUserSpinner = view.findViewById(R.id.task_user);
        updateUserView();

        mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mUserItemWasClicked) {
                    if (position == 0) {
                        mTask.setUser(null);
                    } else if (position == mUserNames.length - 1) {
                        FragmentManager fragmentManager = getFragmentManager();
                        UserCreaterFragment dialog = UserCreaterFragment.newInstance();
                        dialog.setTargetFragment(TaskFragment.this, REQUEST_USER);
                        dialog.show(fragmentManager, DIALOG_USER);
                    } else {
                        mTask.setUser(mUsers.get(position - 1).getId().toString());
                    }
                } else {
                    mUserItemWasClicked = true;
                }

                if (mTask.getUser() != null) {
                    mDeleteUserButton.setVisibility(VISIBLE);
                    mUserAdapter.setCustomText(mUserLab
                            .getUser(UUID.fromString(mTask.getUser())).getName());
                } else {
                    mDeleteUserButton.setVisibility(GONE);
                    mUserAdapter.setCustomText(getString(R.string.task_user_text));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDeleteUserButton = view.findViewById(R.id.task_user_delete);
        mDeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserSpinner.setSelection(0);
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

        } else if (requestCode == REQUEST_USER) {
            String name = (String) data.getSerializableExtra(UserCreaterFragment.EXTRA_USER_NAME);
            if ((name != null)&&(!name.equals(""))) {
                User user = new User();
                user.setName(name);
                mUserLab.addUser(user);
                mTask.setUser(user.getId().toString());

                updateUserView();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task, menu);
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

    private void updateUserView() {
        mUserLab = UserLab.get(getActivity());
        mUsers = mUserLab.getUsers();
        User user;
        if (mTask.getUser() == null) {
            user = null;
        } else {
            user = mUserLab.getUser(UUID.fromString(mTask.getUser()));
        }

        mUserNames = new String[mUsers.size() + 2];
        for (int i = 0; i < mUsers.size(); i++) {
            mUserNames[i + 1] = mUsers.get(i).getName();
        }
        mUserNames[0] = getString(R.string.no_task_user_text);
        mUserNames[mUserNames.length - 1] = getString(R.string.add_new_user_text);

        mUserAdapter = new AdapterWithCustomItem(getActivity(), mUserNames);
        mUserSpinner.setAdapter(mUserAdapter);

        if (user == null) {
            mUserSpinner.setSelection(0);
        } else {
            for (int j = 0; j < mUsers.size(); j++) {
                if (user.equals(mUsers.get(j))) {
                    mUserSpinner.setSelection(j + 1);
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
        String user = mTask.getUser();
        if (user == null) {
            user = getString(R.string.task_report_no_user);
        } else {
            user = getString(R.string.task_report_user,
                    mUserLab.getUser(UUID.fromString(user)).getName());
        }

        String report = getString(R.string.task_report,
                mTask.getTitle(), dateString, solvedString, user);
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