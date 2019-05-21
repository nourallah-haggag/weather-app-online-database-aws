package com.parse.starter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    // declare the views
    EditText userTxt;
    EditText passTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userTxt = (EditText)findViewById(R.id.user_s_txt);
        passTxt = (EditText)findViewById(R.id.pass_s_txt);
    }


    public void SignUp(View v)
    {
        // start the progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ParseUser user = new ParseUser();
        user.setUsername(userTxt.getText().toString());
        user.setPassword(passTxt.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.cancel();
                if( e == null)
                {
                    Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    // navigate to home activity
                    Intent intent = new Intent(SignupActivity.this , HomeActivity.class);
                    finish();
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(SignupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
