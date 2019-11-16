package com.example.gocar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {
    EditText review;
    Button submit;
    String carid;
    String userid;
    String model;
    String fuel;
    String imagepath;
    String currentlogitude;
    String currentlatitude;
    String carlogitude;
    String carlatitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        review = (EditText)findViewById(R.id.userreview);
        submit = (Button)findViewById(R.id.done);
        carid= getIntent().getStringExtra("Carid");
        userid= getIntent().getStringExtra("Userid");
        model= getIntent().getStringExtra("Modelname");
        fuel= getIntent().getStringExtra("Fuellevel");
        imagepath= getIntent().getStringExtra("CarImagePath");
        currentlogitude= getIntent().getStringExtra("Currentlongitude");
        currentlatitude= getIntent().getStringExtra("Currentlatitude");
        carlogitude= getIntent().getStringExtra("Carlongitude");
        carlatitude= getIntent().getStringExtra("Carlatitude");
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println(userid);
                System.out.println(carid);
                System.out.println(review.getText());
                submitReviews();
                Intent reviewintent = new Intent(getApplicationContext(), DeviceDetailActivity.class);
                reviewintent.putExtra("Carid",carid);
                reviewintent.putExtra("Userid",userid);
                reviewintent.putExtra("Modelname",model);
                reviewintent.putExtra("Fuellevel",fuel);
                reviewintent.putExtra("CarImagePath",imagepath);
                reviewintent.putExtra("Currentlatitude",currentlatitude);
                reviewintent.putExtra("Currentlongitude",currentlogitude);
                reviewintent.putExtra("Carlatitude",carlatitude);
                reviewintent.putExtra("Carlongitude",carlogitude);
                startActivity(reviewintent);
                finish();
            }
        });

    }
    private void submitReviews( ) {
        // Tag used to cancel the request
        final String tag_string_req = "req_reviews";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Review_in, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(tag_string_req, "Reviews In Response: ");
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag_string_req, "Reviews In Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("carid", carid);
                params.put("review",review.getText()+"");
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
