package com.sve.taskmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sve.taskmanager.CurrentUserPreferences;
import com.sve.taskmanager.R;
import com.sve.taskmanager.SignIn;
import com.sve.taskmanager.internet.DownloadTasksController;
import com.sve.taskmanager.internet.DownloadUsersController;
import com.sve.taskmanager.model.Task;
import com.sve.taskmanager.model.TaskLab;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText mLoginEditText;
    private Button mSignInButton;
    private String mLogin;

    private boolean mTasksWereReceived = false;
    private boolean mUsersWereReceived = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        /*Toast.makeText(SignInActivity.this,
                "downloadDb(example_company){GET(example_company, tasks) - return List<Task>, GET(example_company, users) - return List<User>}",
                Toast.LENGTH_LONG).show();*/



        mLoginEditText = findViewById(R.id.user_name_edit_text);
        mLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setEnabled(false);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SignIn.isCorrect(SignInActivity.this, mLogin)) {
                    CurrentUserPreferences.setStoredUserLogin(SignInActivity.this, mLogin);
                    Intent intent = NavDrawerActivity.newIntent(SignInActivity.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignInActivity.this,
                            R.string.sign_in_incorrect_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });

        new DownloadTasksController().start(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    List<Task> tasksList = response.body();
                    TaskLab.get(SignInActivity.this).addAll(tasksList);
                    mTasksWereReceived = true;
                    allowSignIn();
                } else {
                    Toast.makeText(SignInActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "failed", Toast.LENGTH_LONG).show();
            }
        });

        new DownloadUsersController().start(SignInActivity.this);
    }

    private void allowSignIn() {
        if (mTasksWereReceived && mUsersWereReceived) {
            Toast.makeText(SignInActivity.this, "tasks & users were received", Toast.LENGTH_LONG).show();
            mSignInButton.setEnabled(true);
        }
    }
}
