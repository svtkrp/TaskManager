package com.sve.taskmanager.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sve.taskmanager.R;

public class UserCreaterFragment extends DialogFragment {

    public static final String EXTRA_USER_LOGIN = "user_login";
    public static final String EXTRA_USER_NAME = "user_name";

    private EditText mLoginField;
    private EditText mNameField;

    public static UserCreaterFragment newInstance() {
        UserCreaterFragment fragment = new UserCreaterFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user, null);

        mLoginField = view.findViewById(R.id.dialog_user_login);
        mNameField = view.findViewById(R.id.dialog_user_name);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.add_new_user_button)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String login = mLoginField.getText().toString();
                        String name = mNameField.getText().toString();
                        sendResult(Activity.RESULT_OK, login, name);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String login, String name) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_LOGIN, login);
        intent.putExtra(EXTRA_USER_NAME, name);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
