package com.sushinamu.safetytracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    //your computer's ip address


    private GoogleMap mMap;
    FirebaseAuth auth;
    GoogleSignInClient clientf;
    Double myLatitude;
    Double myLongitude;
    private GoogleApiClient googleApiClient;
    private int requestcode = 1000;
    private RequestQueue mRequestQueue;

    android.support.v7.widget.Toolbar toolbar;

    //private final static int MY_PERMISSION_FINE_LOCATION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Safety Tracker");
       gpsfunc();

    }

    private void gpsfunc() {
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.12.93/get.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("temp");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                myLatitude = jsonObject.optDouble("latitude");
                                myLongitude = jsonObject.optDouble("longitude");



                                LatLng Loc = new LatLng(myLatitude,myLongitude);

                                mMap.addMarker(new MarkerOptions().position(Loc).title("Marker in"+Loc));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(Loc));

                               // Toast.makeText(MapsActivity.this, "data"+ myLongitude + myLatitude, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }

        });
        mRequestQueue.add(jsonObjectRequest);



    }



      /*   JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                String result;
                JSONObject jsonRootObject = new JSONObject();
                JSONArray jsonArray = jsonRootObject.optJSONArray("temp");
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Double latitude = jsonObject.optDouble("latitude");
                Double longitude = jsonObject.optDouble("longitude");

                System.out.println(latitude);



            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );*/




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }


/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (item.getItemId()){
            case (R.id.login):
                auth = FirebaseAuth.getInstance();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                client = GoogleSignIn.getClient(this,gso);

                Intent signin;
                toolbar.setOnClickListener({
                        signin = client.getSignInIntent();
                        startActivityForResult(signin,requestcode);
                });

                break;
            case (R.id.logout):
                break;
        }
        return super.onOptionsItemSelected(item);

    }
*/

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

        //int[] arrayOfString = {23,45,12,44,55};


        //for (int s : arrayOfString) {
            //LatLng myLocation = new LatLng(90,91);
        LatLng myLocation = new LatLng(90,91);

        mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in"+myLocation));
           // mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
       // Toast.makeText(this, "lat:"+ myLatitude+"long:"+ myLongitude, Toast.LENGTH_SHORT).show();

        //}


        statusCheck();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mMap.setMyLocationEnabled(true);
            }

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
