package org.arb.Nextgen.ePharma.MWR;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.ConnectivityReceiver;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MWROtherDetails extends AppCompatActivity implements View.OnClickListener {
    EditText ed_total_chemist_call, ed_pob_amount, ed_remarks;
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    Button btn_save, btn_back, btn_cancel;
    TextView tv_mwr_no, tv_date, tv_status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwr_other_details);
        ed_total_chemist_call = findViewById(R.id.ed_total_chemist_call);
        ed_pob_amount = findViewById(R.id.ed_pob_amount);
        ed_remarks = findViewById(R.id.ed_remarks);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_mwr_no = findViewById(R.id.tv_mwr_no);
        tv_date = findViewById(R.id.tv_date);
        tv_status = findViewById(R.id.tv_status);

        MWRDetails.temp_for_clearing_stack = 0; //---making item 0 as it is req for clearing Activity stack

        if(MWRDetails.tempForMwrType == 0){
            ed_total_chemist_call.setClickable(false);
            ed_total_chemist_call.setEnabled(false);
            ed_total_chemist_call.setCursorVisible(false);

            ed_pob_amount.setClickable(false);
            ed_pob_amount.setEnabled(false);
            ed_pob_amount.setCursorVisible(false);

            //===========for saved purpose, code starts=========
            if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1") ||
                    MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")) {
                try {
                    JSONObject jsonObject1 = new JSONObject(MWRDetails.responseSavedData);
                    ed_remarks.setText(jsonObject1.getString("remarks"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            //==========for saved purpose, code ends==========

        }else if(MWRDetails.tempForMwrType == 1){
            ed_total_chemist_call.setClickable(true);
            ed_total_chemist_call.setEnabled(true);
            ed_total_chemist_call.setCursorVisible(true);

            ed_pob_amount.setClickable(true);
            ed_pob_amount.setEnabled(true);
            ed_pob_amount.setCursorVisible(true);

            //===========for saved purpose, code starts=========
            if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1") ||
                    MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")) {
                try {
                    JSONObject jsonObject1 = new JSONObject(MWRDetails.responseSavedData);
                    ed_total_chemist_call.setText(jsonObject1.getString("total_chemist_call"));
                    ed_pob_amount.setText(jsonObject1.getString("pob_amount"));
                    ed_remarks.setText(jsonObject1.getString("remarks"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
           //==========for saved purpose, code ends==========
        }
        //---------initializing sqlitedatabase, code starts---
        try {
            db = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS MWR(id INTEGER PRIMARY KEY AUTOINCREMENT, mwr_id VARCHAR, mwr_no VARCHAR, mwr_date VARCHAR, week_day VARCHAR, manager_id VARCHAR, cal_year_id VARCHAR, base_work_place_id VARCHAR, msr_1_id VARCHAR, msr_1_work_place VARCHAR, msr_1_doctor VARCHAR, msr_2_id VARCHAR, msr_2_work_place VARCHAR, msr_2_doctor, json VARCHAR, draft_yn VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase, code ends---
        btn_save.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        //---making save button diable for sent items
        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
            btn_save.setClickable(false);
            btn_save.setEnabled(false);
            btn_save.setAlpha(0.5f);
        }else{
            btn_save.setClickable(true);
            btn_save.setEnabled(true);
            btn_save.setAlpha(1.0f);
        }


        //---setting the values to the textview
        tv_mwr_no.setText("MWR No: "+MWRWeekDate.mwr_no);
        tv_date.setText("Date: "+MWRWeekDate.mwr_selected_date_for_appDisplay_format);

        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("0")){
            tv_status.setText("");
        }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")){
            tv_status.setText("Saved");
        }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
            tv_status.setText("Sent");
        }else{
            tv_status.setText("");
        }
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
            case R.id.btn_save:
//                makeJsonData();
                saveData();
                break;
            case R.id.btn_back:
                startActivity(new Intent(MWROtherDetails.this,MWRmsr2.class));
                break;
            case R.id.btn_cancel:
                MWRDetails.temp_for_clearing_stack = 1; //---making this item 1 for clearing backstack
                Intent intent = new Intent(MWROtherDetails.this,MWRWeekDate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //================function to make JsonObject to save data to server, code starts======
    public String makeJsonData(){
        String jsondata = "";
        try {
            final JSONObject DocumentElementobj = new JSONObject();
            JSONArray msr_1_doctors_list_array = new JSONArray();
            JSONArray msr_2_doctors_list_array = new JSONArray();

            DocumentElementobj.put("mwr_date_list",MWRWeekDate.mwr_date_list_array_json);
            DocumentElementobj.put("mwr_id",Integer.parseInt(sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(1,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db))); //---have to change variable name
//            DocumentElementobj.put("mwr_no",Integer.parseInt(MWRHome.mwr_no));
            DocumentElementobj.put("mwr_no",Integer.parseInt(MWRWeekDate.mwr_no));
            DocumentElementobj.put("mwr_date",MWRWeekDate.mwr_selected_date);
            DocumentElementobj.put("week_day",MWRWeekDate.week_day_name);
            DocumentElementobj.put("mwr_type",Integer.parseInt(userSingletonModel.getMwr_details_mwr_type_id()));
            DocumentElementobj.put("week_date_start",MWRWeekDate.week_start_date);
            DocumentElementobj.put("week_date_end",MWRWeekDate.week_end_date);

            if(!sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(7,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db).contentEquals("")) {
                DocumentElementobj.put("base_work_place_id", Integer.parseInt(sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(7, MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), db)));
            }
            if(!sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(8,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db).contentEquals("")) {
                DocumentElementobj.put("worked_with_msr_1", Integer.parseInt(sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(8, MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), db)));
            }

            if(!sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(11,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db).contentEquals("")) {
                DocumentElementobj.put("worked_with_msr_2", Integer.parseInt(sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(11, MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), db)));
            }

            //---code to make jsonObject for msr1 doct list, code starts-----
            String msr_1_doctors_list = sqliteDb.fetchMWRMsr1Msr2Doctor(10,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db);
            if(!msr_1_doctors_list.contentEquals("")) {
                JSONObject jsonObject_msr1_doctor_list = new JSONObject(msr_1_doctors_list);
                JSONArray jsonArray_msr1_doctor_list = jsonObject_msr1_doctor_list.getJSONArray("values");
                for (int i = 0; i < jsonArray_msr1_doctor_list.length(); i++) {
                    JSONObject reqObj = new JSONObject();
                    JSONObject jsonObject = jsonArray_msr1_doctor_list.getJSONObject(i);
                    reqObj.put("customer_id", Integer.parseInt(jsonObject.getString("customer_id")));
                    reqObj.put("ecl_no", Integer.parseInt(jsonObject.getString("ecl_no")));
                    reqObj.put("work_place_id", Integer.parseInt(jsonObject.getString("work_place_id")));
                    msr_1_doctors_list_array.put(reqObj);

                }
                DocumentElementobj.put("msr_1_doctors_list", msr_1_doctors_list_array);
            }
            //---code to make jsonObject for msr1 doct list, code ends-----

            //---code to make jsonObject for msr2 doct list, code starts-----
            String msr_2_doctors_list = sqliteDb.fetchMWRMsr1Msr2Doctor(13,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db);
            if(!msr_2_doctors_list.contentEquals("")) {
                JSONObject jsonObject_msr2_doctor_list = new JSONObject(msr_2_doctors_list);
                JSONArray jsonArray_msr2_doctor_list = jsonObject_msr2_doctor_list.getJSONArray("values");
                for (int i = 0; i < jsonArray_msr2_doctor_list.length(); i++) {
                    JSONObject reqObj = new JSONObject();
                    JSONObject jsonObject = jsonArray_msr2_doctor_list.getJSONObject(i);
                    reqObj.put("customer_id", Integer.parseInt(jsonObject.getString("customer_id")));
                    reqObj.put("ecl_no", Integer.parseInt(jsonObject.getString("ecl_no")));
                    reqObj.put("work_place_id", Integer.parseInt(jsonObject.getString("work_place_id")));
                    msr_2_doctors_list_array.put(reqObj);

                }
                DocumentElementobj.put("msr_2_doctors_list", msr_2_doctors_list_array);
            }

            //---code to make jsonObject for msr2 doct list, code ends-----

            int total_msr1_ms2_doctor = 0;
            if(!sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(10,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db).contentEquals("")) {
                String msr1_doctor = sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(10, MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), db);
                JSONObject jsonObject_msr1_doctor = new JSONObject(msr1_doctor);
                JSONArray jsonArray_msr1_doctor = jsonObject_msr1_doctor.getJSONArray("values");
                total_msr1_ms2_doctor = total_msr1_ms2_doctor + jsonArray_msr1_doctor.length();
            }

            if(!sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(13,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db).contentEquals("")) {
                String msr2_doctor = sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(13, MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), db);
                JSONObject jsonObject_msr2_doctor = new JSONObject(msr2_doctor);
                JSONArray jsonArray_msr2_doctor = jsonObject_msr2_doctor.getJSONArray("values");
                total_msr1_ms2_doctor = total_msr1_ms2_doctor + jsonArray_msr2_doctor.length();
            }

            DocumentElementobj.put("total_doctor_call",total_msr1_ms2_doctor);
            try {
                if(!ed_total_chemist_call.getText().toString().isEmpty())
                    DocumentElementobj.put("total_chemist_call", Integer.parseInt(ed_total_chemist_call.getText().toString()));
                else{
                    DocumentElementobj.put("total_chemist_call", 0);
                }
                if(!ed_pob_amount.getText().toString().isEmpty()) {
                    DocumentElementobj.put("pob_amount", Double.parseDouble(ed_pob_amount.getText().toString()));
                }else{
                    DocumentElementobj.put("pob_amount", 0);
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            DocumentElementobj.put("remarks",ed_remarks.getText().toString());
            DocumentElementobj.put("manager_id",Integer.parseInt(userSingletonModel.getUser_id()));
            DocumentElementobj.put("mwr_status",1); //--doubt sorted(1 is for save purpose)
            DocumentElementobj.put("cal_year_id",Integer.parseInt(userSingletonModel.getCalendar_id()));
            DocumentElementobj.put("entry_user",userSingletonModel.getUser_name());

            Log.d("jsonobjtest-=>",DocumentElementobj.toString());
            jsondata = DocumentElementobj.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsondata;
    }
    //================function to make JsonObject to save data to server, code ends======


    //================function to save data to server, code starts=======
    public void saveData(){
        final String URL = Config.BaseUrlEpharma+"mwr/save";
        JsonObjectRequest request_json = null;
        try {
            request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(makeJsonData()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("SubmitResponse-=>",response.toString());
                            try {
                                //Process os success response

                                JSONObject jsonObj = null;
                                try{
                                    String responseData = response.toString();
                                    String val = "";
                                    JSONObject resobj = new JSONObject(responseData);
                                    Log.d("getData",resobj.toString());

                                    if(resobj.getString("status").contentEquals("1")){
                                        try {
                                            /*Toast.makeText(getApplicationContext(),resobj.getString("message"),Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(DcrSummary.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);*/
                                            //---------Alert dialog code starts(added on 21st nov)--------
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MWROtherDetails.this);
                                            alertDialogBuilder.setMessage(resobj.getString("message"));
                                            alertDialogBuilder.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {
//                                                            removeData();
                                                            //-----following code is commented on 6th dec to get the calender saved state data------
                                                            MWRDetails.temp_for_clearing_stack = 1; //---making this item 1 for clearing backstack
                                                            Intent intent = new Intent(MWROtherDetails.this,MWRHome.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            MWROtherDetails.this.finish();
                                                        }
                                                    });
                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();

                                            //--------Alert dialog code ends--------
                                        }catch (SQLiteException e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(),resobj.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                    /*Iterator<?> keys = resobj.keys();
                                    while(keys.hasNext() ) {
                                        String key = (String) keys.next();
                                        if (resobj.get(key) instanceof JSONObject) {
                                            JSONObject xx = new JSONObject(resobj.get(key).toString());

                                        }
                                    }*/

                                }catch (JSONException e){
//                                    loading.dismiss();
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
//                    sqliteDb.updateDCR("FinalJson",DocumentElementobj.toString(), userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);

                    String message = "";
                    boolean isConnected = ConnectivityReceiver.isConnected();
                    if (isConnected == false){
                        View v = findViewById(R.id.cordinatorLayout);
                        message = "No Internet Available. DCR saved as Draft.";
//                        new Snackbar("No Internet Available. DCR saved as Draft.",v,Color.parseColor("#ffffff"));
                    }else if(isConnected == true){
                        View v = findViewById(R.id.cordinatorLayout);
                        message = "Server Error. DCR saved as Draft.";
//                        new Snackbar("Server Error. DCR saved as Draft.",v,Color.parseColor("#ffffff"));
                    }
                    //---------Alert dialog code starts--------
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MWROtherDetails.this);
                    alertDialogBuilder.setMessage(message);
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    userSingletonModel.setCheck_draft_saved_last_yn("Y");
                                    //-----following code is commented on 6th dec to get the calender saved state data------
                                    Intent intent = new Intent(MWROtherDetails.this,MWRHome.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    MWROtherDetails.this.finish();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    //--------Alert dialog code ends--------
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request_json);

    }
    //================function to save data to server, code ends=======


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
