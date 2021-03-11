package org.arb.Nextgen.ePharma.config;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Model.LocationDetailsSqliteDataModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class RSSPullService extends Service implements LocationListener {
    SQLiteDatabase db;

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    LocationManager locationManager;
    ArrayList<LocationDetailsSqliteDataModel> locationDetailsSqliteDataModelArrayList = new ArrayList<>();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }else{
            startForeground(1, new Notification());
        }
        getLocation();

        //-----get Location lat long and address code starts--------
       /* if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }*/

        //----Using thread to upload data after evry x secs
        final Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000);  //1000ms = 1 sec
//                        upload_data_delete_sqlite_data_test();
                        new UploadData().execute();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();

        /*final Thread t1 = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec

                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t1.start();*/

        //-----get Location lat long and address code ends--------

    }

    //---------added on 22nd June to run service all time, code starts------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    //---------added on 22nd June, code ends------
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);

       /* final Thread t1 = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t1.start();*/
    }

    public class UploadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
//        fetch_sqlite_upload_data_to_server();
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            boolean isConnected = ConnectivityReceiver.isConnected();
            if (isConnected == true) {
                fetch_sqlite_upload_data_to_server();
//                upload_data_and_delete_sqlite_data();
                upload_data_delete_sqlite_data_test();
            }
        }

    }

    //-------------location code starts--------
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, this);
            //---minTime(in millisec), minDistance(in meters)
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
//        Toast.makeText(getApplicationContext(), "Latitude:" + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+ addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
//            Toast.makeText(getApplicationContext(), addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2), Toast.LENGTH_LONG).show();

            final Context context = this;
            try {
                db = openOrCreateDatabase("LocationDetails", MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS detail(id integer PRIMARY KEY AUTOINCREMENT, lat VARCHAR, long VARCHAR, address VARCHAR, time VARCHAR, date VARCHAR)");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String locationAddress = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2);
//            String locationAddress = addresses.toString();

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c);

            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
            String formattedTime = dfTime.format(time);

            ContentValues values = new ContentValues();
            values.put("lat", location.getLatitude());
            values.put("long", location.getLongitude());
            values.put("address", locationAddress.replace("null",""));
            values.put("time", formattedTime);
            values.put("date", formattedDate);
            if ((db.insert("detail", null, values)) != -1) {
//                Toast.makeText(getApplicationContext(), "Inserted...", Toast.LENGTH_LONG).show();
                Log.d("Test Db", "Data inserted");
            } else {
//                Toast.makeText(getApplicationContext(),"Error...",Toast.LENGTH_LONG).show();
                Log.d("Test Db", "Data not inserted");
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

        //-----location check code starts, added on 16th June------
        /*Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        //-----get Location lat long and address code ends--------



    }

    //==========custom popup code starts, added on 19th June====
    public void load_custom_popup(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialog = li.inflate(R.layout.rsspull_service_custom_popup, null);
        RelativeLayout rl_yes = dialog.findViewById(R.id.rl_yes);
        final TextView tv_yes = dialog.findViewById(R.id.tv_yes);
        android.app.AlertDialog.Builder custom_alert = new android.app.AlertDialog.Builder(getApplicationContext(),R.style.AppTheme);
        custom_alert.setView(dialog);
        custom_alert.setCancelable(false);
        //Creating an alert dialog
        final android.app.AlertDialog custom_alertDialog = custom_alert.create();
//        custom_alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            custom_alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1);
            custom_alertDialog.show();
        }else {
            custom_alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            custom_alertDialog.show();
        }
        rl_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                startActivity(intent);
                custom_alertDialog.dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                startActivity(intent);
                custom_alertDialog.dismiss();
            }
        });
    }
    //==========custom popup code ends, added on 19th June====

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
    //-------------location code ends--------

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
//        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, RSSPullService.class));
        }else{
            startService(new Intent(this, RSSPullService.class));
        }
    }

    @Override
    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }

    //--------------fetch data from sqlite database and upload data to the server, code starts-------
    public void fetch_sqlite_upload_data_to_server() {
        try {
            db = openOrCreateDatabase("LocationDetails", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * FROM detail ORDER BY date,time DESC LIMIT 5", null);

//            TextView v=(TextView)findViewById(R.id.v);
            c.moveToFirst();

            String temp = "";
            if (!locationDetailsSqliteDataModelArrayList.isEmpty()){
                locationDetailsSqliteDataModelArrayList.clear();
            }
            while (!c.isAfterLast()) {
                String s2 = c.getString(0);
                String s3 = c.getString(1);
                String s4 = c.getString(2);
                String s5 = c.getString(5);
//                temp=temp+"\n Lat:"+s2+"\tLong:"+s3+"\tTime:"+s4;
                temp = temp + "\n Date:" + s5;
                LocationDetailsSqliteDataModel locationDetailsSqliteDataModel = new LocationDetailsSqliteDataModel();
                locationDetailsSqliteDataModel.setId(c.getString(0));
                locationDetailsSqliteDataModel.setLatitude(c.getString(1));
                locationDetailsSqliteDataModel.setLongitude(c.getString(2));
                locationDetailsSqliteDataModel.setAddress(c.getString(3));
                locationDetailsSqliteDataModel.setTime(c.getString(4));
                locationDetailsSqliteDataModel.setDate(c.getString(5));
                locationDetailsSqliteDataModelArrayList.add(locationDetailsSqliteDataModel);
                c.moveToNext();
            }
//            v.setText(temp);
//            Toast.makeText(context, temp, Toast.LENGTH_LONG).show();
            Log.d("SqliteData", temp);
        } catch (SQLiteException e) {

        }
    }
    //--------------fetch data from sqlite database and upload data to the server, code ends-------

    //-------------fetch data from arraylist and upload data to the server using volley, code starts-----
    public void upload_data_and_delete_sqlite_data(){
        final JSONObject DocumentElementobj = new JSONObject();
        JSONArray req = new JSONArray();
        JSONObject reqObjdt = new JSONObject();
        try {
            for (int i = 0; i < locationDetailsSqliteDataModelArrayList.size(); i++) {
                JSONObject reqObj = new JSONObject();
                reqObj.put("date", locationDetailsSqliteDataModelArrayList.get(i).getDate());
                reqObj.put("time", locationDetailsSqliteDataModelArrayList.get(i).getTime());
                reqObj.put("longitude", locationDetailsSqliteDataModelArrayList.get(i).getLongitude());
                reqObj.put("latitude", locationDetailsSqliteDataModelArrayList.get(i).getLatitude());
                reqObj.put("address", locationDetailsSqliteDataModelArrayList.get(i).getAddress());
                req.put(reqObj);
            }
            DocumentElementobj.put( "msr_id", Integer.parseInt(userSingletonModel.getUser_id()) );
            DocumentElementobj.put( "location", req );
            Log.d("jsonObjectTest",DocumentElementobj.toString());
//            String url = "http://220.225.40.151:9029/api/msrtracking/?msr_id=105&data_json="+req.toString();
//            String url = "http://220.225.40.151:9029/api/MSRTracking/"+DocumentElementobj.toString();
            /*String encodedurl = URLEncoder.encode(DocumentElementobj.toString(),"UTF-8");
            encodedurl = encodedurl.replaceAll("^",".");*/
            String uri1 = DocumentElementobj.toString().replace(".","^");
            uri1 = uri1.replace(":","=");
            String uri = Uri.encode(uri1);

            String url = "http://220.225.40.151:9029/api/MSRTracking/saveLocation/"+uri;

//            url = url.replaceAll(" ", "%20");
            /*url = url.replaceAll("=", ":");
            url = url.replaceAll("^", ".");*/
            Log.d("url test-=>",url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").contentEquals("true")){
                            try {
                                db = openOrCreateDatabase("LocationDetails", MODE_PRIVATE, null);
                                for(int i=0;i<locationDetailsSqliteDataModelArrayList.size();i++){
                                    String d="DELETE FROM detail WHERE id="+locationDetailsSqliteDataModelArrayList.get(i).getId();
                                    db.execSQL(d);
                                }

                            }catch (SQLiteException e){
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error",error.toString());
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //-------------fetch data from arraylist and upload data to the server using volley, code ends-----

    //-------------testing ---------
    public void upload_data_delete_sqlite_data_test(){

        final JSONObject DocumentElementobj = new JSONObject();
        JSONArray req = new JSONArray();
        JSONObject reqObjdt = new JSONObject();
        try {
            for (int i = 0; i < locationDetailsSqliteDataModelArrayList.size(); i++) {
                JSONObject reqObj = new JSONObject();
                reqObj.put("date", locationDetailsSqliteDataModelArrayList.get(i).getDate());
                reqObj.put("time", locationDetailsSqliteDataModelArrayList.get(i).getTime());
                reqObj.put("longitude", locationDetailsSqliteDataModelArrayList.get(i).getLongitude());
                reqObj.put("latitude", locationDetailsSqliteDataModelArrayList.get(i).getLatitude());
                reqObj.put("address", locationDetailsSqliteDataModelArrayList.get(i).getAddress());
                req.put(reqObj);
            }
//            DocumentElementobj.put("msr_id", Integer.parseInt(userSingletonModel.getUser_id()));
            DocumentElementobj.put("msr_id", Integer.parseInt(userSingletonModel.getUser_id_rss_pull_service())); //--added on 19th june
            DocumentElementobj.put("location", req);
        }catch (Exception e){
            e.printStackTrace();
        }
            Log.d("jsonObjectTest",DocumentElementobj.toString());
//            final String URL = "http://220.225.40.151:9029/api/msrtracking/SaveLocation";
            final String URL = Config.BaseUrlEpharma+"msrtracking/SaveLocation";
            Log.d("UrlTrack-=>",URL);
// Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("msr_id", userSingletonModel.getUser_id());
            params.put("msr_id", userSingletonModel.getUser_id_rss_pull_service());  //---added on 19th june
            params.put("location", req.toString());
        JsonObjectRequest request_json = null;
        try {
            request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(DocumentElementobj.toString()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            JSONObject jsonObj = null;
                            try{
                                String responseData = response.toString();
                                String val = "";
                                JSONObject resobj = new JSONObject(responseData);
                                Log.d("getData",resobj.toString());

                                if(resobj.getString("status").contentEquals("true")){
                                    try {
                                        db = openOrCreateDatabase("LocationDetails", MODE_PRIVATE, null);
                                        for(int i=0;i<locationDetailsSqliteDataModelArrayList.size();i++){
                                            String d="DELETE FROM detail WHERE id="+locationDetailsSqliteDataModelArrayList.get(i).getId();
                                            db.execSQL(d);
                                        }

                                    }catch (SQLiteException e){
                                        e.printStackTrace();
                                    }
                                }
                                Iterator<?> keys = resobj.keys();
                                while(keys.hasNext() ) {
                                    String key = (String) keys.next();
                                    if (resobj.get(key) instanceof JSONObject) {
                                        JSONObject xx = new JSONObject(resobj.get(key).toString());

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

// add the request object to the queue to be executed
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request_json);

    }
    //-------------testing ---------

    //===============code to clear sharedPref data starts, added on 16th June=========
    public void removeSharedPref(){
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("abm_id");
        editor.remove("abm_name");
        editor.remove("designation_id");
        editor.remove("designation_name");
        editor.remove("designation_type");
        editor.remove("hq_id");
        editor.remove("hq_name");
        editor.remove("rbm_id");
        editor.remove("rbm_name");
        editor.remove("sm_id");
        editor.remove("sm_name");
        editor.remove("state");
        editor.remove("user_full_name");
        editor.remove("user_group_id");
        editor.remove("user_id");
        editor.remove("user_name");
        editor.remove("zbm_id");
        editor.remove("zbm_name");
        editor.commit();
    }
    //===============code to clear sharedPref data ends========

}

