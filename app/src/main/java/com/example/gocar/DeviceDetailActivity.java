package com.example.gocar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceDetailActivity extends AppCompatActivity {
    ImageView carimage;
    TextView  modelname;
    TextView  fuellevel;
    TextView  ourcomment;
    Button    navigate;
    Button    addreview;
    ListView  reviewlist;
    ArrayList<String> Names;
    ArrayList<String> Reviews;
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
        setContentView(R.layout.activity_device_detail);
        carid= getIntent().getStringExtra("Carid");
        Reviews = new ArrayList<>();
        Names = new ArrayList<>();
        carimage = (ImageView)findViewById(R.id.carimage);
        modelname = (TextView)findViewById(R.id.carname);
        fuellevel = (TextView)findViewById(R.id.fuellevel2);
        ourcomment = (TextView)findViewById(R.id.reviewtext);
        navigate = (Button)findViewById(R.id.navigate);
        addreview = (Button)findViewById(R.id.reviews);
        userid= getIntent().getStringExtra("Userid");
        model= getIntent().getStringExtra("Modelname");
        fuel= getIntent().getStringExtra("Fuellevel");
        imagepath= getIntent().getStringExtra("CarImagePath");
        currentlogitude= getIntent().getStringExtra("Currentlongitude");
        currentlatitude= getIntent().getStringExtra("Currentlatitude");
        carlogitude= getIntent().getStringExtra("Carlongitude");
        carlatitude= getIntent().getStringExtra("Carlatitude");
        Picasso.get().load(imagepath).into(carimage);
        modelname.setText(model);
        fuellevel.setText("Fuel level: "+fuel+"%");
        getreviews();
        navigate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent locationintent = new Intent(getApplicationContext(), MapsActivity.class);
                locationintent.putExtra("Currentlatitude",currentlatitude);
                locationintent.putExtra("Currentlongitude",currentlogitude);
                locationintent.putExtra("Carlatitude",carlatitude);
                locationintent.putExtra("Carlongitude",carlogitude);
                startActivity(locationintent);
            }
        });
        addreview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent reviewintent = new Intent(getApplicationContext(), ReviewActivity.class);
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
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Reviews.size();
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
            view = getLayoutInflater().inflate(R.layout.reviewlistviewlayout, null);
            //TextView username = (TextView) view.findViewById(R.id.usernametextView);
            TextView review = (TextView) view.findViewById(R.id.reviewtextView);
            //username.setText("From User: "+Names.get(position));
            review.setText(Reviews.get(position));
            return view;
        }
    }
    public void getreviews() {
        final String tag_string_req = "req_reviews";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Review_out, new Response.Listener<String>() {

            @Override
            // response from db
            public void onResponse(String response) {
                Log.d(tag_string_req, "Reviews Out Response: " + response.toString());

                try {
                    JSONArray reviews = new JSONArray(response);
                    for (int i = 0; i < reviews.length(); i++) {
                        JSONObject jObj = reviews.getJSONObject(i);
                        //String username = jObj.getString("username");
                        String review = jObj.getString("review");
                        //Names.add(username);
                        Reviews.add(review);
                    }
                    reviewlist = (ListView) findViewById(R.id.reviewlist);
                    CustomAdapter custom = new CustomAdapter();
                    reviewlist.setAdapter(custom);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag_string_req, "Reviews Out Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", carid);
                return params;
            }
        };
        // request from db
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}