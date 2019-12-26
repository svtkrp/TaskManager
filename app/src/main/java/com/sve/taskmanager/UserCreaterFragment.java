package com.sve.taskmanager;

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

public class UserCreaterFragment extends DialogFragment {
    public static final String EXTRA_USER_NAME = "user_name";

    private EditText mNameField;

    public static UserCreaterFragment newInstance() {
        UserCreaterFragment fragment = new UserCreaterFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user, null);

        mNameField = view.findViewById(R.id.dialog_user_name);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.create_new_user_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = mNameField.getText().toString();
                        sendResult(Activity.RESULT_OK, str);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String str) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_NAME, str);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
