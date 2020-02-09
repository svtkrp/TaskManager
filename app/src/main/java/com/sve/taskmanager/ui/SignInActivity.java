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

public class SignInActivity extends AppCompatActivity {

    private EditText mUserNameEditText;
    private Button mSignInButton;
    private String mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUserNameEditText = findViewById(R.id.user_name_edit_text);
        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsername = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SignIn.isCorrect(SignInActivity.this, mUsername)) {
                    CurrentUserPreferences.setStoredUsername(SignInActivity.this, mUsername);
                    Intent intent = NavDrawerActivity.newIntent(SignInActivity.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignInActivity.this,
                            R.string.sign_in_incorrect_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
