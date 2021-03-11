package org.arb.Nextgen.ePharma.TrackingDetails;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.TrackingDetailsMapModel;
import org.arb.Nextgen.ePharma.Model.TrackingDetailsMsrNameModel;
import org.arb.Nextgen.ePharma.Model.TrackingDetailsStateSpinnerModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.config.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

public class TrackingDetailsActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    RelativeLayout rl_back_arrow;
    ImageButton imgbtn_arrrow, imgBtnCalender;
    EditText edt_date_select;
    final Calendar myCalendar = Calendar.getInstance();
//    Spinner  spinner_msr_name;
    MaterialSpinner spinner_state, spinner_msr_name;
    int msr_id = 0;

    private GoogleMap mMap;
    ArrayList<TrackingDetailsStateSpinnerModel> trackingDetailsStateSpinnerModelArrayList = new ArrayList<>();
    ArrayList<TrackingDetailsMsrNameModel> trackingDetailsMsrNameModelArrayList = new ArrayList<>();
    ArrayList<TrackingDetailsMapModel> trackingDetailsMapModelArrayList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_details);
        rl_back_arrow = findViewById(R.id.rl_back_arrow);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);
        imgBtnCalender = findViewById(R.id.imgBtnCalender);
        edt_date_select = findViewById(R.id.edt_date_select);
        edt_date_select.setClickable(false); //---default making it false, due to spinner selection

        spinner_state = findViewById(R.id.spinner_state);
        spinner_msr_name = findViewById(R.id.spinner_msr_name);

        imgBtnCalender.setOnClickListener(this);
        rl_back_arrow.setOnClickListener(this);
        imgbtn_arrrow.setOnClickListener(this);
        edt_date_select.setOnClickListener(this);

        loadStateData(); //--calling function to load state names to the spinner




//        loadLocationData();

        //=====MAp code starts=====
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //=====MAp code ends======
    }

    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //------commented on 15th July
        /*final LocationManager manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }*/
    }
    //======function for location, code starts======
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ePharma - Caplet");
        builder.setMessage("Your GPS seems to be disabled. Please enable it.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        startActivity(new Intent(HomeActivity.this, FingerprintActivity.class));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //======function for location, code ends======

    //---added on 6th July, code ends----
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_arrow:
                /*Intent intent = new Intent(DcrDetails.this,DcrHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                startActivity(new Intent(TrackingDetailsActivity.this, HomeActivity.class));
                break;
            case R.id.imgbtn_arrrow:
                startActivity(new Intent(TrackingDetailsActivity.this, HomeActivity.class));
            case R.id.imgBtnCalender:
                break;
            case R.id.edt_date_select:
                if (edt_date_select.isClickable()) {
                        //---------Calendar code starts--------
                        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateLabel();
                            }

                        };

                        DatePickerDialog datePickerDialog = new DatePickerDialog(TrackingDetailsActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        // to set Max Date
//                myCalendar.set(2019, -1, 1);
                        long now = System.currentTimeMillis() - 1000;
                        datePickerDialog.getDatePicker().setMaxDate(now); //---set max date

                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, -2);
                        Date preToPreMonthDate = cal.getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = sdf.format(preToPreMonthDate);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date mDate = sdf1.parse(strDate);
                            long timeInMilliseconds = mDate.getTime();
                            datePickerDialog.getDatePicker().setMinDate(timeInMilliseconds);
                            System.out.println("Date in milli :: " + timeInMilliseconds);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        datePickerDialog.show();
                        //---------Calendar code ends--------
                    }

                break;
        }
    }

    //---------Calendar code starts--------
    private void updateLabel() {
//        String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edt_date_select.setText(sdf.format(myCalendar.getTime()));
        loadLocationData(sdf.format(myCalendar.getTime()));
    }
    //---------Calendar code ends--------

    //=====MAp code starts=====
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("SuccessReport-=-=>", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("ErrorReport-=-=>", "Can't find style. Error: ", e);
        }
//        loadLocationData();
        //---setting default location till data loads, code starts----
        LatLng india = new LatLng(22.607073,88.404523);

        mMap.addMarker(new MarkerOptions().position(india).title("Default Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 17.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 17));
        //---setting default location till data loads, code ends----

            for (int i = 0; i < trackingDetailsMapModelArrayList.size(); i++) {
                Log.d("LatitudeTest", trackingDetailsMapModelArrayList.get(i).getLatitude());
            }

            //----code for polyline, starts----
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(3);
            //----code for polyline, ends----

            for (int i = 0; i < trackingDetailsMapModelArrayList.size(); i++) {

                LatLng currentLatLong = new LatLng(Double.parseDouble(trackingDetailsMapModelArrayList.get(i).getLatitude()), Double.parseDouble(trackingDetailsMapModelArrayList.get(i).getLongitude()));
//            mMap.addMarker(new MarkerOptions().position(currentLatLong).title("Marker in Kolkata"));
                mMap.addMarker(new MarkerOptions().position(currentLatLong).title(trackingDetailsMapModelArrayList.get(i).getAddress()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 17.0f));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 17));

//                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));
//            mMap.addPolyline((new PolylineOptions()).add(currentLatLong).width(5).color(Color.BLUE).geodesic(true));
                polylineOptions.add(currentLatLong);

                Log.d("MapData", trackingDetailsMapModelArrayList.get(i).getLatitude());

            }
            mMap.addPolyline(polylineOptions);

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(22, 88);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng india = new LatLng(24,88);
        mMap.addMarker(new MarkerOptions().position(india).title("Marker in Kolkata"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 17.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 17));*/
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));



    }
    //=====MAp code ends======

    //==========volley code to fetch location data, starts========
    public void loadLocationData(String date){
//        String url = "http://220.225.40.151:9029/api/msrtracking/?msr_id=105&log_date="+date;
//        String url = "http://220.225.40.151:9029/api/msrtracking/"+msr_id+"/"+date;
        String url = Config.BaseUrlEpharma+"msrtracking/"+msr_id+"/"+date;
        final ProgressDialog loading = ProgressDialog.show(TrackingDetailsActivity.this, "Loading", "Please wait while loading map", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                getLocationData(response);
               /* JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    if(jsonObject1.getString("status").contentEquals("true")){
                        JSONArray jsonArray = jsonObject.getJSONArray("location");
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            mMap = googleMap;
                            LatLng mapPlot = new LatLng(Double.parseDouble(jsonObject2.getString("latitude")), Double.parseDouble(jsonObject2.getString("longitude")));
                           *//* mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapPlot, 17.0f));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPlot, 17));*//*
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mapPlot));
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }*/
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
                loading.dismiss();

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
    public void getLocationData(String request){
//        final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Authenticating", "Please wait while logging", false, false);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(request);
            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
            if(!trackingDetailsMapModelArrayList.isEmpty()){
                trackingDetailsMapModelArrayList.clear();
            }
            Log.d("locationData-=>",request.toString());
            if(jsonObject1.getString("status").contentEquals("true")){
                JSONArray jsonArray = jsonObject.getJSONArray("location");
                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    TrackingDetailsMapModel trackingDetailsMapModel = new TrackingDetailsMapModel();
                    trackingDetailsMapModel.setTime(jsonObject2.get("time").toString());
                    trackingDetailsMapModel.setAddress(jsonObject2.get("address").toString());
                    trackingDetailsMapModel.setLatitude(jsonObject2.get("latitude").toString());
                    trackingDetailsMapModel.setLongitude(jsonObject2.get("longitude").toString());
                    trackingDetailsMapModelArrayList.add(trackingDetailsMapModel);

                }

                onMapReady(mMap);

              /*  for(int i = 0 ; i < trackingDetailsMapModelArrayList.size() ; i++) {

                    createMarker(Double.parseDouble(trackingDetailsMapModelArrayList.get(i).getLatitude()), Double.parseDouble(trackingDetailsMapModelArrayList.get(i).getLongitude()), "Located", trackingDetailsMapModelArrayList.get(i).getTime(), R.drawable.supervisor);
                }*/

            }else{
                mMap.clear();
//                Toast.makeText(getApplicationContext(),"No data to load",Toast.LENGTH_LONG).show();
            }


        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    //==========volley code to fetch location data, ends========
   /* protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconResID)));
    }*/


   //===========volley and spinner code to fetch states, starts=======
    public void loadStateData(){
        String url = Config.BaseUrlEpharma + "state";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!trackingDetailsStateSpinnerModelArrayList.isEmpty()){
                    trackingDetailsStateSpinnerModelArrayList.clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("states");
                    for(int i = 0; i<jsonArray.length(); i++){
                        TrackingDetailsStateSpinnerModel trackingDetailsStateSpinnerModel = new TrackingDetailsStateSpinnerModel();
                        trackingDetailsStateSpinnerModel.setStates(jsonArray.getString(i));
                        trackingDetailsStateSpinnerModelArrayList.add(trackingDetailsStateSpinnerModel);
                    }

                    //--------Spinner code starts------
                    List<String> stateList = new ArrayList<>();
                    for(int i=0; i<trackingDetailsStateSpinnerModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                        stateList.add(trackingDetailsStateSpinnerModelArrayList.get(i).getStates());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(TrackingDetailsActivity.this, android.R.layout.simple_spinner_item, stateList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_state.setAdapter(adapter);

                    spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position == -1){
//                                spinner_msr_name.setClickable(false);
                                edt_date_select.setClickable(false);
                            }else {
//                                Toast.makeText(getApplicationContext(), "Selected: " + trackingDetailsStateSpinnerModelArrayList.get(position).getStates(), Toast.LENGTH_LONG).show();

//                                spinner_msr_name.setClickable(true);
                                loadMsrNamesData(trackingDetailsStateSpinnerModelArrayList.get(position).getStates()); //----Calling function to load MSR_Names in the spinner
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //--------Spinner code ends------

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
   //===========volley and spinner code to fetch states, ends=======


    //==========volley and spinner code to fetch msr_name, code starts============
    public void loadMsrNamesData(String state_name){
        state_name = state_name.replaceAll(" ","%20");
        String url = Config.BaseUrlEpharma + "msr/list/" + state_name;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!trackingDetailsMsrNameModelArrayList.isEmpty()){
                    trackingDetailsMsrNameModelArrayList.clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("Tracking state-=-=>",jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("msrs");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        TrackingDetailsMsrNameModel trackingDetailsMsrNameModel = new TrackingDetailsMsrNameModel();
                        trackingDetailsMsrNameModel.setMsr_id(jsonObject1.getString("id"));
                        trackingDetailsMsrNameModel.setName(jsonObject1.getString("name"));
                        trackingDetailsMsrNameModel.setLogin_id(jsonObject1.getString("login_id"));
                        trackingDetailsMsrNameModelArrayList.add(trackingDetailsMsrNameModel);
                    }

                    //--------Spinner code starts------
                    List<String> msr_name_list = new ArrayList<>();
                    for(int i=0; i<trackingDetailsMsrNameModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                        msr_name_list.add(trackingDetailsMsrNameModelArrayList.get(i).getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(TrackingDetailsActivity.this, android.R.layout.simple_spinner_item, msr_name_list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_msr_name.setAdapter(adapter);

                    spinner_msr_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position == -1){
                                edt_date_select.setClickable(false);
                            }else {
//                                Toast.makeText(getApplicationContext(), "Selected: " + trackingDetailsMsrNameModelArrayList.get(position).getMsr_id(), Toast.LENGTH_LONG).show();
                                msr_id = Integer.parseInt(trackingDetailsMsrNameModelArrayList.get(position).getMsr_id());
                                edt_date_select.setClickable(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //--------Spinner code ends------

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
    //==========volley and spinner code to fetch msr_name, code ends==============
}
