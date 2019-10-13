package com.example.gocar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_location);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        int k=0;
        String latitude="";
        String longitude="";
        String data= getIntent().getStringExtra("DATA");
        for(int i=0;i<data.length();i++) {
            if(data.charAt(i)=='-') {
                k=i;
            }
        }
        for(int i=0;i<k;i++) {
            latitude=latitude+data.charAt(i);
        }
        for(int i=k+1;i<data.length();i++) {
            longitude=longitude+data.charAt(i);
        }
        LatLng Current = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        googleMap.addMarker(new MarkerOptions().position(Current)
                .title("Car's Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Current));
    }
}
