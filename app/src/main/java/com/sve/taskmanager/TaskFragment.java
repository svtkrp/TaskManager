package com.sve.taskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class TaskFragment extends Fragment {

    private static final String ARG_TASK_ID = "task_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private static final int REQUEST_CONTACT = 2;

    private static final int[] HOURS = {8, 12, 15, 17};

    private Task mTask;
    private EditText mTitleField;
    private Button mDateButton;

    private Spinner mTimeSpinner;
    private AdapterWithCustomItem mTimeAdapter;
    private String[] mTimes;
    private boolean mTimeItemWasClicked = false;

    private CheckBox mSolvedCheckBox;

    private Button mUserButton;
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

        mDateButton = view.findViewById(R.id.task_date_date);

        mTimes = getResources().getStringArray(R.array.times);
        mTimeAdapter = new AdapterWithCustomItem(getActivity(), mTimes);
        mTimeSpinner = view.findViewById(R.id.task_date_time);
        mTimeSpinner.setAdapter(mTimeAdapter);

        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mTask.getDate());
                dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mTimeItemWasClicked) {
                    if (position == mTimes.length-1) {
                        FragmentManager fragmentManager = getFragmentManager();
                        TimePickerFragment dialog = TimePickerFragment.newInstance(mTask.getDate());
                        dialog.setTargetFragment(TaskFragment.this, REQUEST_TIME);
                        dialog.show(fragmentManager, DIALOG_TIME);
                    } else {
                        mTask.setDate(changeTimeNotDate(mTask.getDate(), HOURS[position], 0));
                        updateDate();
                    }
                } else {
                    mTimeItemWasClicked = true;
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

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mUserButton = view.findViewById(R.id.task_user);
        mUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mDeleteUserButton = view.findViewById(R.id.task_user_delete);
        mDeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.setUser(null);
                mUserButton.setText(R.string.task_user_text);
                mDeleteUserButton.setVisibility(GONE);
            }
        });

        if (mTask.getUser() != null) {
            mUserButton.setText(getString(R.string.task_user_chosen_text,
                    mTask.getUser()));
        } else {
            mDeleteUserButton.setVisibility(GONE);
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mUserButton.setEnabled(false);
        }

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
            updateDate();

        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE);
            mTask.setDate(date);
            updateDate();

        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);

            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                String user = cursor.getString(0);
                mTask.setUser(user);
                mUserButton.setText(getString(R.string.task_user_chosen_text, user));
                mDeleteUserButton.setVisibility(VISIBLE);
            } finally {
                cursor.close();
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

    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEEE, MMM d, yyyy", mTask.getDate()));
        mTimeAdapter.setCustomText((String)DateFormat.format("HH:mm", mTask.getDate()));
    }

    private Date changeTimeNotDate(Date date, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    private String getTaskReport() {

        String solvedString;
        if (mTask.isSolved()) {
            solvedString = getString(R.string.task_report_solved);
        } else {
            solvedString = getString(R.string.task_report_unsolved);
        }

        String dateString = DateFormat.format("EEEE, MMM d", mTask.getDate()).toString();

        String user = mTask.getUser();
        if (user == null) {
            user = getString(R.string.task_report_no_user);
        } else {
            user = getString(R.string.task_report_user, user);
        }

        String report = getString(R.string.task_report,
                mTask.getTitle(), dateString, solvedString, user);
        return report;
    }

    private class AdapterWithCustomItem extends ArrayAdapter<String>
    {
        private String[] mOptions;

        private String mCustomText = "";

        public AdapterWithCustomItem(Context context, String[] options){
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