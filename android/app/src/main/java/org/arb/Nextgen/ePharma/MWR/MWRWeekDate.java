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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import org.arb.Nextgen.ePharma.Model.MWRWeekDateModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.CustomMWRWeekDateAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MWRWeekDate extends AppCompatActivity implements View.OnClickListener {
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<MWRWeekDateModel> mwrWeekDateModelArrayList = new ArrayList<>();
    public static String manager_id, mwr_no, week_date, week_start_date, week_end_date, week_day_name, mwr_selected_date_for_appDisplay_format, mwr_date_for_json_format, mwr_header_id_for_draft, mwr_day_status_for_draft, send_data = "";
    public static JSONArray mwr_date_list_array_json = new JSONArray();
    TextView tv_week_date, tv_mwr_no, tv_mwr_no_caption;
    ListView list_mwr_dates;
    public static String mwr_selected_date;
    public Boolean status_send_y_n = false;
    Button btn_cancel, btn_send;

    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwr_week_date);
        list_mwr_dates = findViewById(R.id.list_mwr_dates);
        tv_week_date = findViewById(R.id.tv_week_date);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_send = findViewById(R.id.btn_send);
        tv_mwr_no = findViewById(R.id.tv_mwr_no);
        tv_mwr_no_caption = findViewById(R.id.tv_mwr_no_caption);

       //---making mwr_no visible/invisible, code starts
        if(MWRHome.mwr_no.contentEquals("0")){
            tv_mwr_no.setVisibility(View.INVISIBLE);
            tv_mwr_no_caption.setVisibility(View.INVISIBLE);
        }else{
            tv_mwr_no.setVisibility(View.VISIBLE);
            tv_mwr_no_caption.setVisibility(View.VISIBLE);
            tv_mwr_no.setText(MWRHome.mwr_no);
        }
       //---making mwr_no visible/invisible, code ends

        load_mwr_week_dates(); //----calling function to load data


        //---------initializing sqlitedatabase, code starts---
        try {
            db = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS MWR(id INTEGER PRIMARY KEY AUTOINCREMENT, mwr_id VARCHAR, mwr_no VARCHAR, mwr_date VARCHAR, week_day VARCHAR, manager_id VARCHAR, cal_year_id VARCHAR, base_work_place_id VARCHAR, msr_1_id VARCHAR, msr_1_work_place VARCHAR, msr_1_doctor VARCHAR, msr_2_id VARCHAR, msr_2_work_place VARCHAR, msr_2_doctor, json VARCHAR, draft_yn VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        sqliteDb.deleteDCR(db);
        //---------initializing sqlitedatabase, code ends---

        btn_cancel.setOnClickListener(this);
        btn_send.setOnClickListener(this);
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
          case R.id.btn_cancel:
              Intent intent = new Intent(MWRWeekDate.this,MWRHome.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
              break;
          case R.id.btn_send:
              saveData();
              break;
              default:
                  break;
      }
    }


    //==============function to load data using volley, code starts=============
    public void load_mwr_week_dates(){
        String week_start_date_output_format = "", week_end_date_output_format = "";
        Date week_start_date_original_format = null, week_end_date_original_format = null;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            week_start_date_original_format = originalFormat.parse(MWRHome.week_start_date);
            week_end_date_original_format = originalFormat.parse(MWRHome.week_end_date);

            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            week_start_date_output_format = outputFormat.format(week_start_date_original_format);
            week_end_date_output_format = outputFormat.format(week_end_date_original_format);
        }catch (Exception e){
            e.printStackTrace();
        }

        String url = Config.BaseUrlEpharma + "mwr/week_days/" + MWRHome.mwr_no + "/" + userSingletonModel.getUser_id()+"/"+week_start_date_output_format+"/"+week_end_date_output_format+"/"+userSingletonModel.getCalendar_id();
        final ProgressDialog loading = ProgressDialog.show(MWRWeekDate.this, "Loading", "Please wait...", true, false);
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
        if(!mwrWeekDateModelArrayList.isEmpty()){
            mwrWeekDateModelArrayList.clear();
        }
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(request);
            Log.d("weekDateData-=>",jsonObject.toString());

            manager_id = jsonObject.getString("manager_id");
            mwr_no = jsonObject.getString("mwr_no");
            week_date = jsonObject.getString("week_date");

            //---code to format date, starts----
            String week_date_output_format = "";
            Date week_date_original_format = null;
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                week_date_original_format = originalFormat.parse(week_date);

                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                week_date_output_format = outputFormat.format(week_date_original_format);
            }catch (Exception e){
                e.printStackTrace();
            }
            //---code to format date, ends----
            tv_week_date.setText(week_date_output_format);

            week_start_date = jsonObject.getString("week_start_date");
            week_end_date = jsonObject.getString("week_end_date");

            //----code to make jsonObject for sending data, starts-----
            final JSONObject DocumentElementobj1 = new JSONObject();
            DocumentElementobj1.put("mwr_no",jsonObject.getInt("mwr_no"));
            DocumentElementobj1.put("manager_id",jsonObject.getInt("manager_id"));
            DocumentElementobj1.put("week_start",jsonObject.getString("week_start_date"));
            DocumentElementobj1.put("week_end",jsonObject.getString("week_end_date"));
            DocumentElementobj1.put("cal_year_id",jsonObject.getInt("cal_year_id"));

            send_data = DocumentElementobj1.toString();
            //----code to make jsonObject for sending data, ends-----

            JSONArray jsonArray = jsonObject.getJSONArray("week_days");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                MWRWeekDateModel mwrWeekDateModel = new MWRWeekDateModel();
                mwrWeekDateModel.setMwr_header_id(jsonObject1.getString("mwr_header_id"));
                mwrWeekDateModel.setSelected_yn("N"); //---this piece of code is for newly generated json(which is required for save purpose at MWROtherDetails Activity)
                mwrWeekDateModel.setMwr_date(jsonObject1.getString("mwr_date"));
                mwrWeekDateModel.setMwr_week_day(jsonObject1.getString("mwr_week_day"));
                mwrWeekDateModel.setMwr_type(jsonObject1.getString("mwr_type"));
                mwrWeekDateModel.setId_work_place(jsonObject1.getString("id_work_place"));
                mwrWeekDateModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                mwrWeekDateModel.setId_worked_with_1(jsonObject1.getString("id_worked_with_1"));
                mwrWeekDateModel.setId_worked_with_2(jsonObject1.getString("id_worked_with_2"));
                mwrWeekDateModel.setDay_status(jsonObject1.getString("day_status"));
                mwrWeekDateModel.setDay_status_desc(jsonObject1.getString("day_status_desc"));
                mwrWeekDateModel.setCall_total_doctor(jsonObject1.getString("call_total_doctor"));
                mwrWeekDateModel.setCall_total_chemist(jsonObject1.getString("call_total_chemist"));
                mwrWeekDateModel.setPob_amount(jsonObject1.getString("pob_amount"));
                mwrWeekDateModel.setComment(jsonObject1.getString("comment"));
                mwrWeekDateModelArrayList.add(mwrWeekDateModel);
            }

            //---code to enable/disable Send button, starts (added on 8th Jan)---
            for(int i=0;i<mwrWeekDateModelArrayList.size();i++){
                if(mwrWeekDateModelArrayList.get(i).getDay_status().contentEquals("1")){
                    status_send_y_n = true;
                }else{
                    status_send_y_n = false;
                    break;
                }
            }

            if(status_send_y_n == true){
                btn_send.setClickable(true);
                btn_send.setEnabled(true);
                btn_send.setAlpha(1.0f);
            }else if(status_send_y_n == false){
                btn_send.setClickable(false);
                btn_send.setEnabled(false);
                btn_send.setAlpha(0.5f);
            }
            //---code to enable/disable Send button, ends---

            list_mwr_dates.setAdapter(new CustomMWRWeekDateAdapter(this, mwrWeekDateModelArrayList));
//            list_mwr_dates.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list_mwr_dates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getApplicationContext(),mwrWeekDateModelArrayList.get(position).getMwr_date(),Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(MWRWeekDate.this, MWRDetails.class));
                    Intent intent = new Intent(MWRWeekDate.this,MWRDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    mwrWeekDateModelArrayList.get(position).setSelected_yn("Y"); //---this piece of code is for newly generated json(which is required for save purpose at MWROtherDetails Activity)

                    mwr_day_status_for_draft = mwrWeekDateModelArrayList.get(position).getDay_status(); //---added on 20th jan
                    mwr_header_id_for_draft = mwrWeekDateModelArrayList.get(position).getMwr_header_id(); //---added on 20th jan
                    mwr_selected_date = mwrWeekDateModelArrayList.get(position).getMwr_date();
                    week_day_name = mwrWeekDateModelArrayList.get(position).getMwr_week_day();
                    sqliteDb.updateMWRDateAndWeekDay(MWRHome.mwr_no,mwr_selected_date, mwrWeekDateModelArrayList.get(position).getMwr_week_day(), userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(),db);

                    Date selected_date_original_format = null;
                    try {
                        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                        selected_date_original_format = originalFormat.parse(mwr_selected_date);

                        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        mwr_selected_date_for_appDisplay_format = outputFormat.format(selected_date_original_format);

                        //--newly added on 7th jan, code starts
                        /*DateFormat outputFormat1 = new SimpleDateFormat("yyyy-dd-MM");
                        mwr_selected_date = outputFormat1.format(selected_date_original_format);*/
                        DateFormat outputFormat1 = new SimpleDateFormat("yyyy-dd-MM");
                        mwr_date_for_json_format = outputFormat1.format(selected_date_original_format);
                        //--newly added on 7th jan, code ends
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //---code to make mwr_date_list json, starts-----
                    final JSONObject DocumentElementobj = new JSONObject();
                    JSONArray mwr_date_list_array = new JSONArray();
                    for(int i=0;i<mwrWeekDateModelArrayList.size();i++){
                        try {
                            JSONObject reqObj = new JSONObject();

                            Date original_format = null;
                            String output_format = "";
                            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                            original_format = originalFormat.parse(mwrWeekDateModelArrayList.get(i).getMwr_date());

                            DateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                            output_format = outputFormat1.format(original_format);

                            reqObj.put("mwr_date", output_format);
                            reqObj.put("mwr_id", Integer.parseInt(mwrWeekDateModelArrayList.get(i).getMwr_header_id()));
                            reqObj.put("mwr_type", Integer.parseInt(mwrWeekDateModelArrayList.get(i).getMwr_type()));
                            reqObj.put("selected_yn", mwrWeekDateModelArrayList.get(i).getSelected_yn());
                            mwr_date_list_array.put(reqObj);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    mwr_date_list_array_json = mwr_date_list_array;
                    Log.d("mwrListJsonArray-=>",mwr_date_list_array_json.toString());

                    //---code to make mwr_date_list json, ends-----

                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //==============function to load data using volley, code ends=============

    //===========function to send data using volley, code starts============
    public void saveData(){
        send_data = send_data.replace(":","'colon'");
//        Log.d("colonTest-=>",send_data);
        String url = Config.BaseUrlEpharma + "mwr/send/" + send_data;
        final ProgressDialog loading = ProgressDialog.show(MWRWeekDate.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        getResponseData(response);
                        Log.d("sendDataTest-=>",response);
                        load_mwr_week_dates();
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
    //===========function to send data using volley, code ends============

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
