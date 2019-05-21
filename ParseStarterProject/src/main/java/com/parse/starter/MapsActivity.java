package com.parse.starter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);








    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        // get teh locations saved in the online database
        // show loading progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Getting saved locations...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                        // retrieve saved locations and place them on the map
                        for(ParseObject object : objects)
                        {
                            // get the lat and lon attributes from online database
                            String latS = object.getString("lat");
                            String lonS = object.getString("lon");
                            // get the weather to add as marker title
                            String weather = object.getString("main");
                            // convert to double
                            Double lat = Double.parseDouble(latS);
                            Double lon = Double.parseDouble(lonS);
                            LatLng position = new LatLng(lat , lon);
                            // add location on the map
                            mMap.addMarker(new MarkerOptions().position(position).title(weather));

                        }
                    }
                    else {
                        Toast.makeText(MapsActivity.this, "You have not saved any locations yet !!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MapsActivity.this, "failed, please check your network connection !!", Toast.LENGTH_LONG).show();
                }
            }
        });



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
                // Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();
                getWeather(latLng);
               /* if(result)
                {
                    Toast.makeText(MapsActivity.this, "location added successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapsActivity.this, "failed to add location", Toast.LENGTH_SHORT).show();
                }*/


            }
        });


    }









    void getWeather(final LatLng selectedLocation){
        final ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setTitle("Weather Info");
        progressDialog.setMessage("loading weather data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //String url = "https://samples.openweathermap.org/data/2.5/weather?lat="+selectedLocation.latitude+"&lon="+selectedLocation.longitude+"&appid=1a164502f6d99932213b0469778a3c98";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+selectedLocation.latitude+"&lon="+selectedLocation.longitude+"&appid=1a164502f6d99932213b0469778a3c98";
        RequestQueue requestQueue;
        // jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, Response.Listener);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                progressDialog.cancel();
                String main="";
                String description="";
                try {

                    JSONArray responseJSONArray = ((JSONObject)response).getJSONArray("weather");
                    for (int i = 0; i < responseJSONArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) responseJSONArray.get(i);
                        main = jsonObject.getString("main");
                        description = jsonObject.getString("description");


                        Log.i("response" , main);
                        Toast.makeText(MapsActivity.this, "weather info / main: "+main, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MapsActivity.this, "weather info / description: "+description, Toast.LENGTH_SHORT).show();

                    }
                   /* Intent intent = new Intent(MapsActivity.this , ResultActivity.class);
                    intent.putExtra("main" , main);
                    intent.putExtra("desc" , description);

                    startActivity(intent);*/

                   // set the view for the alert dialog
                    View v = LayoutInflater.from(MapsActivity.this).inflate(R.layout.map_result_view , null ,false);
                    // init the views from the layout
                    TextView mainTxt = (TextView)v.findViewById(R.id.main_txt) ;
                    TextView descTxt = (TextView)v.findViewById(R.id.desc_txt) ;
                   // show the weather in an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Weather Status");
                    builder.setIcon(R.drawable.ic_map_black_24dp);
                    builder.setView(v);
                    mainTxt.setText(main);
                    descTxt.setText(description);
                    final String finalMain = main;
                    final String finalDescription = description;
                    builder.setPositiveButton("Save location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            // progress of saving operation
                            final ProgressDialog progressDialog1 = new ProgressDialog(MapsActivity.this);
                            progressDialog1.setTitle("Please Wait");
                            progressDialog1.setMessage("saving location and weather data...");
                            progressDialog1.setCancelable(false);
                            progressDialog1.show();

                            // convert double to string
                            String lat = String.valueOf(selectedLocation.latitude);
                            String lon = String.valueOf(selectedLocation.longitude);

                            // save location in online database
                            final ParseObject locationParseObject = new ParseObject("loc");
                            // save the following attributes in the pasre object "loc"
                            locationParseObject.put("user" , ParseUser.getCurrentUser().getUsername());
                            locationParseObject.put("lat" , lat); // saving latitude
                            locationParseObject.put("lon" , lon); // saving longitude
                            locationParseObject.put("main" , finalMain); // saving main weather status
                            locationParseObject.put("desc" , finalDescription); // saving description of the weather
                            locationParseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    progressDialog1.cancel();
                                    if(e==null)
                                    {
                                        Toast.makeText(MapsActivity.this, "Location Saved Successfully", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(MapsActivity.this, "Save operation failed, location will be saved automatically once connection is restored !!", Toast.LENGTH_LONG).show();
                                        locationParseObject.saveEventually();
                                    }
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // do nothing
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();



                    Log.e("latLng","error");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Log.e("fail","failed to volley");
                Toast.makeText(MapsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }


}
