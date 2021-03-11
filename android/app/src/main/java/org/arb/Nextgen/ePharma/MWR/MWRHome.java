package org.arb.Nextgen.ePharma.MWR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.MWRHomeModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.CustomMWRHomeAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MWRHome extends AppCompatActivity implements View.OnClickListener {
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<MWRHomeModel> mwrHomeModelArrayList = new ArrayList<>();
    ListView list_mwr_date_details;
    RelativeLayout rl_back_arrow;
    ImageButton imgbtn_home;
    public static String week_start_date, week_end_date, mwr_no;
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwr_home);
        list_mwr_date_details = findViewById(R.id.list_mwr_date_details);
//        rl_back_arrow = findViewById(R.id.rl_back_arrow);
        imgbtn_home = findViewById(R.id.imgbtn_home);

        load_mwr_week_dates(); //--calling function to load data

//        rl_back_arrow.setOnClickListener(this);
        imgbtn_home.setOnClickListener(this);

        //---------initializing sqlitedatabase, code starts---
        try {
            db = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS MWR(id INTEGER PRIMARY KEY AUTOINCREMENT, mwr_id VARCHAR, mwr_no VARCHAR, mwr_date VARCHAR, week_day VARCHAR, manager_id VARCHAR, cal_year_id VARCHAR, base_work_place_id VARCHAR, msr_1_id VARCHAR, msr_1_work_place VARCHAR, msr_1_doctor VARCHAR, msr_2_id VARCHAR, msr_2_work_place VARCHAR, msr_2_doctor, json VARCHAR, draft_yn VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        sqliteDb.deleteDCR(db);
        //---------initializing sqlitedatabase, code ends---

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
        switch (v.getId()){
//            case R.id.rl_back_arrow:
            case R.id.imgbtn_home:
                Intent intent = new Intent(MWRHome.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

    //==============function to load data using volley, code starts=============
    public void load_mwr_week_dates(){
        String url = Config.BaseUrlEpharma + "mwr/list/" + userSingletonModel.getUser_id() + "/" + userSingletonModel.getCalendar_id();
        final ProgressDialog loading = ProgressDialog.show(MWRHome.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getResponseData(response);
                        loading.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void getResponseData(String request){
        if(!mwrHomeModelArrayList.isEmpty()){
            mwrHomeModelArrayList.clear();
        }
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(request);
            Log.d("initialData-=>",jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("week_date");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                MWRHomeModel mwrHomeModel = new MWRHomeModel();
                mwrHomeModel.setId(jsonObject1.getString("id"));
                mwrHomeModel.setMwrNo(jsonObject1.getString("mwr_no"));
                mwrHomeModel.setWeek_date(jsonObject1.getString("week_date"));
                mwrHomeModel.setWeek_start_date(jsonObject1.getString("week_start_date"));
                mwrHomeModel.setWeek_end_date(jsonObject1.getString("week_end_date"));
                mwrHomeModel.setStatus(jsonObject1.getString("status"));
                mwrHomeModel.setStatus_desc(jsonObject1.getString("status_desc"));
                mwrHomeModelArrayList.add(mwrHomeModel);
            }
            list_mwr_date_details.setAdapter(new CustomMWRHomeAdapter(this, mwrHomeModelArrayList));
            list_mwr_date_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getApplicationContext(),mwrHomeModelArrayList.get(position).getWeek_date(),Toast.LENGTH_LONG).show();
                    week_start_date = mwrHomeModelArrayList.get(position).getWeek_start_date();
                    week_end_date = mwrHomeModelArrayList.get(position).getWeek_end_date();
                    if(mwrHomeModelArrayList.get(position).getMwrNo().trim().contentEquals("")){
                        mwr_no = "0";
                        sqliteDb.deleteMWR(db); //---for temporary use
//                        sqliteDb.insertDataMWR("0","0",null,null, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), null,null,null,null,null,null,null, null,"No",db);
                        sqliteDb.insertDataMWR(mwrHomeModelArrayList.get(position).getId(),"0",null,null, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), null,null,null,null,null,null,null, null,"No",db);
                    }else{
                        mwr_no = mwrHomeModelArrayList.get(position).getMwrNo();
                        sqliteDb.deleteMWR(db); //---for temporary use
//                        sqliteDb.insertDataMWR("0",mwr_no,null,null, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), null,null,null,null,null,null,null, null,"No",db);
                        sqliteDb.insertDataMWR(mwrHomeModelArrayList.get(position).getId(),mwr_no,null,null, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), null,null,null,null,null,null,null, null,"No",db);
                    }

                    Intent intent = new Intent(MWRHome.this, MWRWeekDate.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(new Intent(MWRHome.this, MWRWeekDate.class));
                    startActivity(intent);
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //==============function to load data using volley, code ends=============

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
