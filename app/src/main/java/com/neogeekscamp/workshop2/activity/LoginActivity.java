package com.neogeekscamp.workshop2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.neogeekscamp.workshop2.R;
import com.neogeekscamp.workshop2.manager.AppPrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private AppPrefManager appPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        appPrefManager = new AppPrefManager(this);

        if (appPrefManager.getIsLoggedIn()) {
            launchChat();
        }
    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        doLogin();
    }

    private void doLogin() {
        String username = etUsername.getText().toString();
        if (validate(username)) {
            appPrefManager.setIsLoggedIn(true);
            appPrefManager.setUser(username);
            launchChat();
            Toast.makeText(LoginActivity.this, "Join talks", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String username) {
        boolean valid = true;

        if (username.isEmpty()) {
            etUsername.setError("enter username");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        return valid;
    }

    private void launchChat() {
        startActivity(new Intent(LoginActivity.this, ChatActivity.class));
        finish();
    }
}
