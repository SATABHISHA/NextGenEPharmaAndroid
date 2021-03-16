package org.arb.Nextgen.ePharma.Login;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.config.AppVersionUpgradeNotifier;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.ConnectivityReceiver;
import org.arb.Nextgen.ePharma.config.MyApplication;
import org.arb.Nextgen.ePharma.config.RSSPullService;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener, AppVersionUpgradeNotifier.VersionUpdateListener {

    Button btnLogin;
//    TextView btnForgotPassword; //commenting this line on 6th July as per client requirement
    EditText edtCorpId, edtUsername, edtPassword;
    String corpId, username, password;
    private ProgressDialog progressBar;
//    CheckBox chkSignedIn; //commenting this line on 6th July as per client requirement
    Dialog forgotPassword;
    String corporateID, userName, emailID;
    private int REQUEST_CODE = 1;
    SharedPreferences sharedPreferences;
    SQLiteDatabase db;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static int chck_menulist_upload_document = 0;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469; //--added on 19th June
//    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppVersionUpgradeNotifier.init(this,this); //---for version updateDCR

        requestMultiplePermissions(); //---added on 7th july
        //-----get Location lat long and address code starts--------
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

            } else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }

        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }

//        startService(new Intent(this, RSSPullService.class)); //commenting, to run on simulator otherwise location tracking code will crash
        checkConnection();//----function calling to check the internet connection


        //----Using thread to check Internet connection
        final Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isConnected = ConnectivityReceiver.isConnected();
                                if (isConnected == false){
                                    View v = findViewById(R.id.relativeLayout);
                                    new Snackbar("Please connect to the iternet",v,Color.parseColor("#ffffff"));
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();

        sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        btnLogin = (Button) findViewById(R.id.activity_login_btn_login);
//        btnForgotPassword = (TextView) findViewById(R.id.activity_login_btn_forgot_password); //commenting this line on 6th July as per client requirement
        edtCorpId = (EditText) findViewById(R.id.activity_login_edt_corp_ID);
        edtUsername = (EditText) findViewById(R.id.activity_login_edt_username);
        edtPassword = (EditText) findViewById(R.id.activity_login_edt_password);
//        chkSignedIn = (CheckBox) findViewById(R.id.activity_login_chk); //commenting this line on 6th July as per client requirement

        //===========button onClickListner() code starts=======
        btnLogin.setOnClickListener(this);
        btnLogin.setEnabled(true);
        btnLogin.setClickable(true);
//        btnForgotPassword.setOnClickListener(this); //commenting this line on 6th July as per client requirement
//        edtCorpId.setOnClickListener(this);
        edtUsername.setOnClickListener(this);
        edtPassword.setOnClickListener(this);
//        chkSignedIn.setOnClickListener(this); //commenting this line on 6th July as per client requirement
        //===========button onClickListner() code ends=======


        //=====================code for one time login starts============
        sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        //=========to check sharedpref and autofill corpid, code starts, added on 12th june=============
        String user_id_epharma = sharedPreferences.getString("user_id","");
        if (user_id_epharma != ""){
            userSingletonModel.setCorp_id(sharedPreferences.getString("corp_id",""));
            userSingletonModel.setAbm_id(sharedPreferences.getString("abm_id",""));
            userSingletonModel.setAbm_name(sharedPreferences.getString("abm_name",""));
            userSingletonModel.setDesignation_id(sharedPreferences.getString("designation_id",""));
            userSingletonModel.setDesignation_name(sharedPreferences.getString("designation_name",""));
            userSingletonModel.setDesignation_type(sharedPreferences.getString("designation_type",""));
            userSingletonModel.setHq_id(sharedPreferences.getString("hq_id",""));
            userSingletonModel.setHq_name(sharedPreferences.getString("hq_name",""));
            userSingletonModel.setRbm_id(sharedPreferences.getString("rbm_id",""));
            userSingletonModel.setRbm_name(sharedPreferences.getString("rbm_name",""));
            userSingletonModel.setSm_id(sharedPreferences.getString("sm_id",""));
            userSingletonModel.setSm_name(sharedPreferences.getString("sm_name",""));
            userSingletonModel.setState(sharedPreferences.getString("state",""));
            userSingletonModel.setUser_full_name(sharedPreferences.getString("user_full_name",""));
            userSingletonModel.setUser_group_id(sharedPreferences.getString("user_group_id",""));
            userSingletonModel.setUser_id(sharedPreferences.getString("user_id",""));
            userSingletonModel.setUser_id_rss_pull_service(sharedPreferences.getString("user_id","")); //--added on 19th june
            userSingletonModel.setUser_name(sharedPreferences.getString("user_name",""));
            userSingletonModel.setZbm_id(sharedPreferences.getString("zbm_id",""));
            userSingletonModel.setZbm_name(sharedPreferences.getString("zbm_name",""));
            userSingletonModel.setMenu_list(sharedPreferences.getString("menu_list",""));

            userSingletonModel.setCalendar_id(sharedPreferences.getString("calendar_id",""));
            userSingletonModel.setCalendar_year(sharedPreferences.getString("calendar_year",""));
            userSingletonModel.setCalendar_start_date(sharedPreferences.getString("start_date",""));
            userSingletonModel.setCalendar_end_date(sharedPreferences.getString("end_date",""));


            //---added on 6th July as per client requirement, code starts---
           /* final LocationManager manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            }*/
            //---added on 6th July as per client requirement, code ends---
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);

            //========added on 19th June========
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(LoginActivity.this)) {
                askPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, RSSPullService.class));
            }else{
                startService(new Intent(this, RSSPullService.class));
            }
        }
        //--added on 22ns oct for autofill/autologin, code ends----
        //===================code for one time login ends=====================
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //=====================code for one time login starts============
        sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        //=========to check sharedpref and autofill corpid, code starts, added on 12th june=============
        String user_id_epharma = sharedPreferences.getString("user_id","");
        if (user_id_epharma != ""){
            userSingletonModel.setCorp_id(sharedPreferences.getString("corp_id",""));
            userSingletonModel.setAbm_id(sharedPreferences.getString("abm_id",""));
            userSingletonModel.setAbm_name(sharedPreferences.getString("abm_name",""));
            userSingletonModel.setDesignation_id(sharedPreferences.getString("designation_id",""));
            userSingletonModel.setDesignation_name(sharedPreferences.getString("designation_name",""));
            userSingletonModel.setDesignation_type(sharedPreferences.getString("designation_type",""));
            userSingletonModel.setHq_id(sharedPreferences.getString("hq_id",""));
            userSingletonModel.setHq_name(sharedPreferences.getString("hq_name",""));
            userSingletonModel.setRbm_id(sharedPreferences.getString("rbm_id",""));
            userSingletonModel.setRbm_name(sharedPreferences.getString("rbm_name",""));
            userSingletonModel.setSm_id(sharedPreferences.getString("sm_id",""));
            userSingletonModel.setSm_name(sharedPreferences.getString("sm_name",""));
            userSingletonModel.setState(sharedPreferences.getString("state",""));
            userSingletonModel.setUser_full_name(sharedPreferences.getString("user_full_name",""));
            userSingletonModel.setUser_group_id(sharedPreferences.getString("user_group_id",""));
            userSingletonModel.setUser_id(sharedPreferences.getString("user_id",""));
            userSingletonModel.setUser_id_rss_pull_service(sharedPreferences.getString("user_id","")); //--added on 19th june
            userSingletonModel.setUser_name(sharedPreferences.getString("user_name",""));
            userSingletonModel.setZbm_id(sharedPreferences.getString("zbm_id",""));
            userSingletonModel.setZbm_name(sharedPreferences.getString("zbm_name",""));
            userSingletonModel.setMenu_list(sharedPreferences.getString("menu_list",""));

            userSingletonModel.setCalendar_id(sharedPreferences.getString("calendar_id",""));
            userSingletonModel.setCalendar_year(sharedPreferences.getString("calendar_year",""));
            userSingletonModel.setCalendar_start_date(sharedPreferences.getString("start_date",""));
            userSingletonModel.setCalendar_end_date(sharedPreferences.getString("end_date",""));


            //---added on 6th July as per client requirement, code starts---
           /* final LocationManager manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            }*/
            //---added on 6th July as per client requirement, code ends---
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);

            //========added on 19th June========
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(LoginActivity.this)) {
                askPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, RSSPullService.class));
            }else{
                startService(new Intent(this, RSSPullService.class));
            }
        }
        //--added on 22ns oct for autofill/autologin, code ends----
    }

    //=======fuction for app overlay permission added on 19th June, starts=====
    private void askPermission() {
        Intent intent= new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
        startActivityForResult(intent,ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(resultCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE){
            if(!Settings.canDrawOverlays(this)){
                askPermission();
            }
        }*/
    }
    //=======fuction for app overlay permission added on 19th June, ends=====
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

    //======version updateDCR code starts(24th dec2019)======
   /* @Override
    public void onCreate() {
        super.onCreate();

        AppVersionUpgradeNotifier.init(this,this);
    }*/

    @Override
    public boolean onVersionUpdate(int newVersion, int oldVersion) {
        //do what you want

        return true;
    }
    //======version updateDCR code ends(24th dec2019)======

    //=============swicth case for button onClickListner code starts=========
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_login_btn_login:

               if(edtUsername.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {

                    View v = findViewById(R.id.relativeLayout);
                    new Snackbar("Field cannot be left empty",v,Color.parseColor("#ffffff"));
                }
                else{
                    btnLogin.setEnabled(false);
                    btnLogin.setClickable(false);
                    btnLogin.setAlpha(0.4f);
//                    login();
                   epharmaLogin();
                }
//              epharmaLogin();
              /*  Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);*/
//                upload_data_delete_sqlite_data_test();
                break;
           /* case R.id.activity_login_btn_forgot_password:
                break;*/ //commenting this line on 6th July as per client requirement
            case R.id.activity_login_edt_username:
                break;
            case R.id.activity_login_edt_password:
                break;
           /* case R.id.activity_login_chk:
                break;*/ //commenting this line on 6th July as per client requirement
        }
    }
    //=============swicth case for button onClickListner code ends========

public void test(){
    final JSONObject reqObjdt = new JSONObject();
    try {
        reqObjdt.put("CorpID", "gst-inc-101");
        reqObjdt.put("Date", "01/01/2019");
        reqObjdt.put("StatusFlag", "0");
        Log.d("jsonTest", reqObjdt.toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }
    final String URL = "http://192.168.10.175:9006/api/Timesheet/HolidayList";
// Post params to be sent to the server
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("CorpID", "gst-inc-101");
    params.put("Date", "01/01/2019");
    params.put("StatusFlag", "0");

    JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            JSONObject jsonObj = null;
                            try{
                                String response1 = response.toString();
                                jsonObj = XML.toJSONObject(response1);
                                String responseData = response.toString();
                                String val = "";
                                JSONObject resobj = new JSONObject(responseData);
    //                            String status=jsonObject.getString("status");

    //                            Toast.makeText(getApplicationContext(),jsonObject.getString("status"),Toast.LENGTH_LONG).show();
                                Log.d("getData",resobj.toString());

                                Iterator<?> keys = resobj.keys();
                                while(keys.hasNext() ) {
                                    String key = (String) keys.next();
                                    if (resobj.get(key) instanceof JSONObject) {
                                        JSONObject xx = new JSONObject(resobj.get(key).toString());
                                        val = xx.getString("content");
    //                                    Toast.makeText(getApplicationContext(),xx.getString("content"),Toast.LENGTH_LONG).show();
    //                                    Log.d("getdata1", xx.getString("content"));



                                            // caldroidFragment.setDisableDates(disableDates);
                                            //caldroidFragment.setSelectedDate(selectDate);
                                        }


                                    }

                            }catch (JSONException e){
    //                            loading.dismiss();
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

// add the request object to the queue to be executed
    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
    requestQueue.add(request_json);

}
    //---------------volley code for login starts-----------
    public void epharmaLogin(){
        //----code to get device_id and upload to api code starts(added on 17th June)......
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("AndroidIId-=>",android_id);
        //----code to get device_id and upload to api code ends(added on 17th June)......
//        String url = "http://220.225.40.151:9029/api/login/?user_id="+edtUsername.getText().toString()+"&password="+edtPassword.getText().toString();
//        String url = "http://220.225.40.151:9029/api/login/"+edtUsername.getText().toString()+"/"+edtPassword.getText().toString();
        String url = Config.BaseUrlEpharma+"/epharma/login/"+edtCorpId.getText().toString()+"/"+edtUsername.getText().toString()+"/"+edtPassword.getText().toString()+"/"+android_id;
        Log.d("urlLogin-=>",url);
        final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Authenticating", "Please wait while logging", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadEpharmaData(response);
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
                loading.dismiss();
                btnLogin.setEnabled(true);
                btnLogin.setClickable(true);
                btnLogin.setAlpha(1.0f);

                String message = "Could not connect server";
                View v = findViewById(R.id.relativeLayout);
                new Snackbar(message,v,Color.parseColor("#ffffff"));
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
    public void loadEpharmaData(String request){
//        final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Authenticating", "Please wait while logging", false, false);
        //-----location check code starts------
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            btnLogin.setEnabled(true);
            btnLogin.setClickable(true);
            btnLogin.setAlpha(1.0f);

        }else if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //-----------code to check location permission, code starts(added on 25th nov)---------
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                btnLogin.setEnabled(true);
                btnLogin.setClickable(true);
                btnLogin.setAlpha(1.0f);

            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                //-----------code to check location permission, code ends---------


                //-----location check code ends------
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(request);
                    JSONObject jsonObjectResponse = jsonObject.getJSONObject("response");

                    if (jsonObjectResponse.getString("status").contentEquals("true")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("user");
                        Log.d("Logintest-=>",jsonObject1.toString());

                        userSingletonModel.setCorp_id(edtCorpId.getText().toString()); //--newly added on 16th march
                        userSingletonModel.setAbm_id(jsonObject1.getString("abm_id"));
                        userSingletonModel.setAbm_name(jsonObject1.getString("abm_name"));
                        userSingletonModel.setDesignation_id(jsonObject1.getString("designation_id"));
                        userSingletonModel.setDesignation_name(jsonObject1.getString("designation_name"));
                        userSingletonModel.setDesignation_type(jsonObject1.getString("designation_type"));
                        userSingletonModel.setHq_id(jsonObject1.getString("hq_id"));
                        userSingletonModel.setHq_name(jsonObject1.getString("hq_name"));
                        userSingletonModel.setRbm_id(jsonObject1.getString("rbm_id"));
                        userSingletonModel.setRbm_name(jsonObject1.getString("rbm_name"));
                        userSingletonModel.setSm_id(jsonObject1.getString("sm_id"));
                        userSingletonModel.setSm_name(jsonObject1.getString("sm_name"));
                        userSingletonModel.setState(jsonObject1.getString("state"));
                        userSingletonModel.setUser_full_name(jsonObject1.getString("user_full_name"));
                        userSingletonModel.setUser_group_id(jsonObject1.getString("user_group_id"));
                        userSingletonModel.setUser_id(jsonObject1.getString("user_id"));
                        userSingletonModel.setUser_id_rss_pull_service(jsonObject1.getString("user_id")); //--added on 19th june
                        userSingletonModel.setUser_name(jsonObject1.getString("user_name"));
                        userSingletonModel.setZbm_id(jsonObject1.getString("zbm_id"));
                        userSingletonModel.setZbm_name(jsonObject1.getString("zbm_name"));
                        userSingletonModel.setMenu_list(jsonObject1.getString("menu_list"));

                        Log.d("responseMenuTest-=>",userSingletonModel.getMenu_list());

                        //----code to add calendar details, starts(added on 5th Dec)------
                        JSONObject jsonObject2 = jsonObject.getJSONObject("calendar_year");
                        userSingletonModel.setCalendar_id(jsonObject2.getString("id"));
                        userSingletonModel.setCalendar_year(jsonObject2.getString("year"));
                        userSingletonModel.setCalendar_start_date(jsonObject2.getString("start_date"));
                        userSingletonModel.setCalendar_end_date(jsonObject2.getString("end_date"));

                        Log.d("StartDate-=>",userSingletonModel.getCalendar_start_date());
                        Log.d("EndDate-=>",userSingletonModel.getCalendar_end_date());
                        //----code to add calendar details, ends------

//                Toast.makeText(getApplicationContext(),userSingletonModel.getUserID(),Toast.LENGTH_LONG).show();

//                Toast.makeText(getApplicationContext(),"User Id:-=>"+userSingletonModel.getUser_id(),Toast.LENGTH_LONG).show();

                        //======================storing the value to shared preference for onetime login code starts=============
//                        if (chkSignedIn.isChecked()) { //commenting this line on 6th July as per client requirement
                            sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("corp_id",userSingletonModel.getCorp_id());
                            editor.putString("abm_id", userSingletonModel.getAbm_id());
                            editor.putString("abm_name", userSingletonModel.getAbm_name());
                            editor.putString("designation_id", userSingletonModel.getDesignation_id());
                            editor.putString("designation_name", userSingletonModel.getDesignation_name());
                            editor.putString("designation_type", userSingletonModel.getDesignation_type());
                            editor.putString("hq_id", userSingletonModel.getHq_id());
                            editor.putString("hq_name", userSingletonModel.getHq_name());
                            editor.putString("rbm_id", userSingletonModel.getRbm_id());
                            editor.putString("rbm_name", userSingletonModel.getRbm_name());
                            editor.putString("sm_id", userSingletonModel.getSm_id());
                            editor.putString("sm_name", userSingletonModel.getSm_name());
                            editor.putString("state", userSingletonModel.getState());
                            editor.putString("user_full_name", userSingletonModel.getUser_full_name());
                            editor.putString("user_group_id", userSingletonModel.getUser_group_id());
                            editor.putString("user_id", userSingletonModel.getUser_id());
                            editor.putString("user_name", userSingletonModel.getUser_name());
                            editor.putString("zbm_id", userSingletonModel.getZbm_id());
                            editor.putString("zbm_name", userSingletonModel.getZbm_name());
                            editor.putString("menu_list", userSingletonModel.getMenu_list());

                            editor.putString("calendar_id", userSingletonModel.getCalendar_id());
                            editor.putString("calendar_year", userSingletonModel.getCalendar_year());
                            editor.putString("start_date", userSingletonModel.getCalendar_start_date());
                            editor.putString("end_date", userSingletonModel.getCalendar_end_date());


                            editor.commit();
//                        }
                        //======================storing the value to shared preference for onetime login code ends=============
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
//                        statusCheck(); //---added on 26th Nov
//                loading.dismiss();
                        //-----------storing the value og corp-id from edittext, added on 12th june, starts============
                        /*sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserNameForUserAutofill", edtUsername.getText().toString()); //--added on 22nd oct
                        editor.commit();*/ //commenting this line on 6th July as per client requirement
                        //-----------storing the value og corp-id from edittext, added on 12th june, ends============
                        finish();
                    } else if (jsonObjectResponse.getString("status").contentEquals("false")) {
//                loading.dismiss();
//                                        String message = "Invalid Login Credential";
                        String message = jsonObjectResponse.getString("message");
                                       /* int color = Color.parseColor("#ffffff");
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.relativeLayout), message, Snackbar.LENGTH_LONG);

                                        View sbView = snackbar.getView();
                                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                        textView.setTextColor(color);
                                        snackbar.show();*/

                        View v = findViewById(R.id.relativeLayout);
//                                        new org.arb.timesheet_demo.config.Snackbar(message,v);
                        new Snackbar(message, v, Color.parseColor("#ffffff"));

                        btnLogin.setEnabled(true);
                        btnLogin.setClickable(true);
                        btnLogin.setAlpha(1.0f);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    //---------------volley code for login ends-------------


    //===============newly added 26thnov,2019 , location enability check, code starts============
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }else if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //-----------code to check location permission, code starts(added on 25th nov)---------
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

            }else if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
            //-----------code to check location permission, code ends---------

        }
    }
    //===============newly added 26thnov,2019 ,location enability check location code ends============



    //=============Internet checking code starts(added 22nd Nov)=============
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
//            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
           /* color = Color.parseColor("#ffffff");
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.relativeLayout), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();*/

        }
        View v = findViewById(R.id.relativeLayout);
//            new org.arb.timesheet_demo.config.Snackbar(message,v);
        new Snackbar(message,v,Color.parseColor("#ffffff"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        //---added on 7th july
        //=====================code for one time login starts============
        sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        //=========to check sharedpref and autofill corpid, code starts, added on 12th june=============
        String user_id_epharma = sharedPreferences.getString("user_id","");
        if (user_id_epharma != ""){
            userSingletonModel.setAbm_id(sharedPreferences.getString("abm_id",""));
            userSingletonModel.setAbm_name(sharedPreferences.getString("abm_name",""));
            userSingletonModel.setDesignation_id(sharedPreferences.getString("designation_id",""));
            userSingletonModel.setDesignation_name(sharedPreferences.getString("designation_name",""));
            userSingletonModel.setDesignation_type(sharedPreferences.getString("designation_type",""));
            userSingletonModel.setHq_id(sharedPreferences.getString("hq_id",""));
            userSingletonModel.setHq_name(sharedPreferences.getString("hq_name",""));
            userSingletonModel.setRbm_id(sharedPreferences.getString("rbm_id",""));
            userSingletonModel.setRbm_name(sharedPreferences.getString("rbm_name",""));
            userSingletonModel.setSm_id(sharedPreferences.getString("sm_id",""));
            userSingletonModel.setSm_name(sharedPreferences.getString("sm_name",""));
            userSingletonModel.setState(sharedPreferences.getString("state",""));
            userSingletonModel.setUser_full_name(sharedPreferences.getString("user_full_name",""));
            userSingletonModel.setUser_group_id(sharedPreferences.getString("user_group_id",""));
            userSingletonModel.setUser_id(sharedPreferences.getString("user_id",""));
            userSingletonModel.setUser_id_rss_pull_service(sharedPreferences.getString("user_id","")); //--added on 19th june
            userSingletonModel.setUser_name(sharedPreferences.getString("user_name",""));
            userSingletonModel.setZbm_id(sharedPreferences.getString("zbm_id",""));
            userSingletonModel.setZbm_name(sharedPreferences.getString("zbm_name",""));
            userSingletonModel.setMenu_list(sharedPreferences.getString("menu_list",""));

            userSingletonModel.setCalendar_id(sharedPreferences.getString("calendar_id",""));
            userSingletonModel.setCalendar_year(sharedPreferences.getString("calendar_year",""));
            userSingletonModel.setCalendar_start_date(sharedPreferences.getString("start_date",""));
            userSingletonModel.setCalendar_end_date(sharedPreferences.getString("end_date",""));


            //---added on 6th July as per client requirement, code starts---
           /* final LocationManager manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            }*/
            //---added on 6th July as per client requirement, code ends---
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);

            //========added on 19th June========
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(LoginActivity.this)) {
                askPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, RSSPullService.class));
            }else{
                startService(new Intent(this, RSSPullService.class));
            }
        }
        //--added on 7th july for autofill/autologin, code ends----
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    //=============Internet checking code ends(added 22nd Nov)=============


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    //========following function is to resign keyboard on touching anywhere in the screen
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    //-------added for file permission on 7th july, code starts
    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    //-------added for file permission on 7th july, code ends
}
