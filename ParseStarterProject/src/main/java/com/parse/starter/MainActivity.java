/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  // define views
  EditText userTxt;
  EditText passTxt;
  TextView signUp;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // initialize views
    userTxt = (EditText)findViewById(R.id.username_txt) ;
    passTxt = (EditText)findViewById(R.id.pass_txt);
    signUp = (TextView)findViewById(R.id.signup_txt);

    // signup
    signUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        // attempt signup
        Intent intent = new Intent(MainActivity.this , SignupActivity.class);
        startActivity(intent);
      }
    });



    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  // login
  public void loginFunction(View v)
  {
    // progress dialog for loading
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Please Wait");
    progressDialog.setMessage("logging in...");
    progressDialog.setCancelable(false);
    progressDialog.show();
    ParseUser.logInInBackground(userTxt.getText().toString(), passTxt.getText().toString(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        progressDialog.cancel();
        if(e == null)
        {
          Toast.makeText(MainActivity.this, "welcome , "+user.getUsername(), Toast.LENGTH_SHORT).show();
          // navigate to home
          Intent intent = new Intent(MainActivity.this , HomeActivity.class);
          finish();
          startActivity(intent);
        }
        else {
          Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });

  }

}