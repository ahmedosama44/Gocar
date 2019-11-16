package com.example.gocar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.location.Location.distanceBetween;
import static android.provider.SettingsSlicesContract.KEY_LOCATION;

public class MainActivity extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnCurrent;
    private Button btnLogout;
    private Button showall;
    private SQLiteHandler db;
    private SessionManager session;
    private double Mylongitude;
    private double Mylatitude;
    private ArrayList<String> Id;
    private ArrayList<String> Modelname;
    private ArrayList<String> Productionyear;
    private ArrayList<String> Latitude;
    private ArrayList<String> Longitude;
    private ArrayList<String> Imagepath;
    private ArrayList<String> Fuellevel;
    private ArrayList<Double> Distance;
    private ArrayList<Double> TempDistance;
    private ArrayList<Double> sortedDistance;
    private ArrayList<Integer> Index;
    private ListView listview;
    private LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private boolean flag;
    private FusedLocationProviderClient client;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private String Userid;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        showall = (Button) findViewById(R.id.showall);
        Id = new ArrayList<>();
        Modelname = new ArrayList<>();
        Productionyear = new ArrayList<>();
        Latitude = new ArrayList<>();
        Longitude = new ArrayList<>();
        Imagepath = new ArrayList<>();
        Fuellevel = new ArrayList<>();
        Distance = new ArrayList<>();
        sortedDistance = new ArrayList<>();
        TempDistance = new ArrayList<>();
        Index = new ArrayList<>();
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        Userid = user.get("uid");
        client = LocationServices.getFusedLocationProviderClient(this);
        getmylocation();
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        getvehicles();
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        listview = (ListView) findViewById(R.id.listView);
        CustomAdapter custom = new CustomAdapter();
        listview.setAdapter(custom);
        //getting long and latit and passing them to the map activity
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent locationintent = new Intent(getApplicationContext(), DeviceDetailActivity.class);
                locationintent.putExtra("Userid",Userid);
                locationintent.putExtra("Carid",Id.get(Index.get(position)));
                locationintent.putExtra("Modelname",Modelname.get(Index.get(position)));
                locationintent.putExtra("Fuellevel",Fuellevel.get(Index.get(position)));
                locationintent.putExtra("CarImagePath","http://192.168.8.100/" +Imagepath.get(Index.get(position)));
                locationintent.putExtra("Currentlatitude",Mylatitude+"");
                locationintent.putExtra("Currentlongitude",Mylongitude+"");
                locationintent.putExtra("Carlatitude",Latitude.get(Index.get(position)) + "");
                locationintent.putExtra("Carlongitude",Longitude.get(Index.get(position)) + "");
                startActivity(locationintent);
            }
        });
        showall.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String la="";
                String lg="";
                String size = Latitude.size()+"";
                for(int i=0;i<Latitude.size()-1;i++){
                    la= la+Latitude.get(i)+",";
                    lg= lg+Longitude.get(i)+",";
                }
                la= la+Latitude.get(Latitude.size()-1);
                lg= lg+Longitude.get(Longitude.size()-1);
                Intent locationintent = new Intent(getApplicationContext(), showall.class);
                locationintent.putExtra("la",la );
                locationintent.putExtra("lg",lg  );
                locationintent.putExtra("size",size );
                startActivity(locationintent);
            }


        });
    }
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Id.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.listviewlayout, null);
            ImageView imageview = (ImageView) view.findViewById(R.id.imageView);
            TextView modelname = (TextView) view.findViewById(R.id.modelname);
            TextView productionyear = (TextView) view.findViewById(R.id.productionyear);
            TextView fuellevel = (TextView) view.findViewById(R.id.fuellevel);
            String path = "http://192.168.8.100/" + Imagepath.get(Index.get(position));
            Picasso.get().load(path).into(imageview);
            modelname.setText("ModelName: " + Modelname.get(Index.get(position)));
            productionyear.setText("ProductionYear: " + Productionyear.get(Index.get(position)));
            fuellevel.setText("FuelLevel: " + Fuellevel.get(Index.get(position)) + "%");
            return view;
        }
    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void showCurrentLocation() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void getvehicles() {
        String tag_string_req = "req_vehicles";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_Vehicles, new Response.Listener<String>() {

            @Override
            // response from db
            public void onResponse(String response) {
                Log.d(TAG, "Vehicles Response: " + response.toString());
                try {
                    JSONArray vehicles = new JSONArray(response);
                    for (int i = 0; i < vehicles.length(); i++) {
                        JSONObject jObj = vehicles.getJSONObject(i);
                        String id = jObj.getString("id");
                        String modelname = jObj.getString("modelname");
                        String productionyear = jObj.getString("productionyear");
                        String latitude = jObj.getString("latitude");
                        String longitude = jObj.getString("longitude");
                        String imagepath = jObj.getString("imagepath");
                        String fuellevel = jObj.getString("fuellevel");
                        Id.add(id);
                        Modelname.add(modelname);
                        Productionyear.add(productionyear);
                        Latitude.add(latitude);
                        Longitude.add(longitude);
                        Imagepath.add(imagepath);
                        Fuellevel.add(fuellevel);
                    }
                    calculatealldistances(Longitude, Latitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
        };
        // request from db
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void getmylocation() {
        try {
            mLocationPermissionGranted=true;
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = client.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Mylatitude=mLastKnownLocation.getLatitude();
                            Mylongitude=mLastKnownLocation.getLongitude();
                            Toast.makeText(MainActivity.this,"SUCCESS",Toast.LENGTH_LONG);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Toast.makeText(MainActivity.this,"FAILURE",Toast.LENGTH_LONG);

                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }
    public double calculatedistance(double longitude, double latitude ){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(Mylatitude);
        startPoint.setLongitude(Mylongitude);
        Location endPoint=new Location("locationA");
        endPoint.setLatitude(latitude);
        endPoint.setLongitude(longitude);
        double distance=startPoint.distanceTo(endPoint);
        return distance;
    }
    public void calculatealldistances(ArrayList<String> longitude,ArrayList<String> latitude ){
        for(int i=0; i<longitude.size(); i++){
            double longit = Double.parseDouble(longitude.get(i));
            double latit = Double.parseDouble(latitude.get(i));
            double distance = calculatedistance(longit,latit);
            Distance.add(distance);
            TempDistance.add(distance);
        }
        sortedDistance=sort(TempDistance);
        for(int i=0;i<sortedDistance.size();i++) {
            Index.add(Distance.indexOf(sortedDistance.get(i)));
        }
    }
    public ArrayList<Double> sort(ArrayList<Double> x) {
        Collections.sort(x);
        return  x;
    }
}