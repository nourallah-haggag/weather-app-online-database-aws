package com.parse.starter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // declare views and objects related to the list
    RecyclerView recyclerView;
    RecyclerLocAdapter adapter;
    static List<LocModel> locList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // init the views and the list items
        locList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        adapter =  new RecyclerLocAdapter(locList , this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSavedLocations();
    }

    // navigate to the map activity
    public void openMap(View v )
    {

        Intent intent = new Intent(this , MapsActivity.class);
        startActivity(intent);

    }

    // get locations saved in online database
    public void getSavedLocations()
    {
        // start the locading porgress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Getting saved locations...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // get teh locations saved in the online database
        ParseQuery<ParseObject> locationsQuery = ParseQuery.getQuery("loc");
        locationsQuery.whereEqualTo("user" , ParseUser.getCurrentUser().getUsername());
        locationsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                progressDialog.cancel();
                if( e == null)
                {
                    if(objects.size()>0)
                    {
                        // clear the list first to prevent duplicate entries
                        locList.clear();
                        // retrieve saved locations and place them on the map
                        for(ParseObject object : objects)
                        {
                            // get the lat and lon attributes from online database
                            String latS = object.getString("lat");
                            String lonS = object.getString("lon");
                            String main = object.getString("main");
                            String desc = object.getString("desc");

                            LocModel locModel = new LocModel(latS , lonS , main , desc);

                           HomeActivity.locList.add(locModel);



                        }
                        // reverse so that the mosy recent location appear at the top
                        Collections.reverse(locList);
                        adapter.notifyDataSetChanged();

                    }
                    else {
                        Toast.makeText(HomeActivity.this, "You have not saved any locations yet !!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this, "failed, please check your network connection !!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // action to perform when returning to the activity (refresh the list)
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSavedLocations();
    }

    // action to perform when pressing the back button
    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App ?");
        builder.setMessage("Are you sure you want to exit ?");
        builder.setCancelable(false);
        builder.setPositiveButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               HomeActivity.super.onBackPressed();

            }
        });
        builder.setNegativeButton("stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.show();
    }
}
