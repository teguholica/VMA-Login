package com.teguholica.vmalogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new User(this);
        if (user.isEmpty()) {
            setContentView(R.layout.activity_main);

            txtUsername = (EditText) findViewById(R.id.username);
            txtPassword = (EditText) findViewById(R.id.password);
            Button btnSave = (Button) findViewById(R.id.save);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new User(
                            MainActivity.this,
                            txtUsername.getText().toString(),
                            txtPassword.getText().toString()
                    );
                    goToControlPage();
                }
            });
        } else {
            goToControlPage();
        }
    }

    private void goToControlPage() {
        Intent controlActivity = new Intent(MainActivity.this, ControlActivity.class);
        startActivity(controlActivity);
        finish();
    }
}
