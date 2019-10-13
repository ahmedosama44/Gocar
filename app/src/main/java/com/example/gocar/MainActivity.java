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
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.LocationListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements LocationListener {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnCurrent;
    private Button btnLogout;
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


    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
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

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // vehicles
        getvehicles();
        //listview

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
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent locationintent = new Intent(getApplicationContext(), LocationActivity.class);
                String data = Latitude.get(position)+"-"+Longitude.get(position);
                locationintent.putExtra("DATA",data);
                startActivity(locationintent);
            }
        });
        System.out.println(Index.toString());

        //List View
        //String path = "http://192.168.8.100/" + "android_login_api/car_images/opel_astra_image.png";
        //Picasso.get().load(path).into(imageview);

        // my location

        //calculate distance


        // SqLite database handler

        // session manager

    }
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onLocationChanged(Location location) {
        Mylatitude=location.getLatitude();
        Mylongitude=location.getLongitude();

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
            String path = "http://172.20.10.2/" + Imagepath.get(Index.get(position));
            Picasso.get().load(path).into(imageview);
            modelname.setText("ModelName: " + Modelname.get(Index.get(position)));
            productionyear.setText("ProductionYear: " + Productionyear.get(Index.get(position)));
            fuellevel.setText("FuelLevel: " + Fuellevel.get(Index.get(position))+"%");
            return view;
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void showCurrentLocation() {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
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
                        //db.addvehicle(id, modelname, productionyear, latitude, longitude, imagepath, fuellevel);
                        Id.add(id);
                        Modelname.add(modelname);
                        Productionyear.add(productionyear);
                        Latitude.add(latitude);
                        Longitude.add(longitude);
                        Imagepath.add(imagepath);
                        Fuellevel.add(fuellevel);
                    }
                    getmylocation();
                    calculatealldistances(Longitude,Latitude);
                    //db.retrievevehicles(Id, Modelname, Productionyear, Latitude, Longitude, Imagepath, Fuellevel);
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
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        System.out.println("11111111"+sortedDistance.toString());
    }

    public void getmylocation() {
        if (isLocationEnabled(MainActivity.this)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                Mylatitude = location.getLatitude();
                Mylongitude = location.getLongitude();
            }
            else{
                //This is what you need:
                //locationManager.requestLocationUpdates(bestProvider, 1000, 0, (com.google.android.gms.location.LocationListener) this);
            }
        }
        else {
            //prompt user to enable location....
            //.................
        }
    }
    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
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
            System.out.println(distance);

        }
        sortedDistance=sort(TempDistance);
        for(int i=0;i<sortedDistance.size();i++) {
            Index.add(Distance.indexOf(sortedDistance.get(i)));
        }
        System.out.println("Nour:"+Index.toString());
    }
    public ArrayList<Double> sort(ArrayList<Double> x) {
//        double[] array = new double[x.size()];
//        for(int i=0;i<x.size();i++) {
//            array[i]=x.get(i);
//        }
//        Arrays.sort(array);
//        x.clear();
//        for(int i=0;i<x.size();i++) {
//            x.set(i,array[i]);
//        }
        Collections.sort(x);
        return  x;
    }
}