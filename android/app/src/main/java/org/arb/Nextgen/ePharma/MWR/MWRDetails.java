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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import org.arb.Nextgen.ePharma.Model.MWRTypeModel;
import org.arb.Nextgen.ePharma.Model.MWRWorkPlaceModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MWRDetails extends AppCompatActivity implements View.OnClickListener {
    Button btn_cancel;
    static Button btn_next;
    SQLiteDatabase db;
    LinearLayout ll_base_work_place;
    public static int tempForMwrType = 1, temp_for_clearing_stack = 1;
    public static String responseSavedData = "", base_work_place_id_for_saved_data = "", base_work_place_name_for_saved_data = "";
    TextView tvName, tvMWRNo, tvMWRDate, tvHQ;

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<MWRWorkPlaceModel> mwrWorkPlaceModelArrayList = new ArrayList<>();
    ArrayList<MWRTypeModel> mwrTypeModelArrayList = new ArrayList<>();

    Spinner spinner_work_place, spinner_type;
    SqliteDb sqliteDb = new SqliteDb();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwr_details);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_next = findViewById(R.id.btn_next);
        spinner_work_place = findViewById(R.id.spinner_work_place);
        spinner_type = findViewById(R.id.spinner_type);
        ll_base_work_place = findViewById(R.id.ll_base_work_place);
        tvName = findViewById(R.id.tvName);
        tvMWRNo = findViewById(R.id.tvMWRNo);
        tvMWRDate = findViewById(R.id.tvMWRDate);
        tvHQ = findViewById(R.id.tvHQ);

        btn_cancel.setOnClickListener(this);
        btn_next.setOnClickListener(this);


        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")) {
            loadPrevSavedData(Integer.parseInt(MWRWeekDate.mwr_header_id_for_draft)); //---added on 20th jan
            Log.d("mwrDayStatus-=>",MWRWeekDate.mwr_day_status_for_draft);
        }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
            loadPrevSavedData(Integer.parseInt(MWRWeekDate.mwr_header_id_for_draft));
            Log.d("mwrDayStatus-=>",MWRWeekDate.mwr_day_status_for_draft);
            Log.d("mwrIDHeaderDraft-=>",MWRWeekDate.mwr_header_id_for_draft);
        }

        spinner_load_mwr_type(); //------calling function to load mwrtype in the spinner with static values
        loadMwrDetailsData(); //--calling function to load work_place spinner data

        //---------initializing sqlitedatabase, code starts---
        try {
            db = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS MWR(id INTEGER PRIMARY KEY AUTOINCREMENT, mwr_id VARCHAR, mwr_no VARCHAR, mwr_date VARCHAR, week_day VARCHAR, manager_id VARCHAR, cal_year_id VARCHAR, base_work_place_id VARCHAR, msr_1_id VARCHAR, msr_1_work_place VARCHAR, msr_1_doctor VARCHAR, msr_2_id VARCHAR, msr_2_work_place VARCHAR, msr_2_doctor, json VARCHAR, draft_yn VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase, code ends---

        //---setting values to textView
        tvName.setText(userSingletonModel.getUser_full_name());
        tvMWRNo.setText(MWRWeekDate.mwr_no);
        tvMWRDate.setText(MWRWeekDate.mwr_selected_date_for_appDisplay_format + " (" + MWRWeekDate.week_day_name + ")");
        tvHQ.setText(userSingletonModel.getHq_name());

    }

    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //------commented on 15th July
       /* final LocationManager manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                /*MWRDetails.temp_for_clearing_stack = 1; //---making this item 1 for clearing backstack
                Intent intent = new Intent(MWRDetails.this, MWRWeekDate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/

                //added on 12th march,  starts
                if(!MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")) {
//                    tv_status.setText("Sent");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Do you really want to cancel the MWR?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    MWRDetails.temp_for_clearing_stack = 1; //---making this item 1 for clearing backstack
                                    Intent intent = new Intent(MWRDetails.this, MWRWeekDate.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
//                            HomeActivity.this.finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    //added on 12th march,  ends
                }else{
                    MWRDetails.temp_for_clearing_stack = 1; //---making this item 1 for clearing backstack
                    Intent intent = new Intent(MWRDetails.this, MWRWeekDate.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                break;
            case R.id.btn_next:
                if(tempForMwrType == 1) {
                    /*if(userSingletonModel.getCheck_draft_saved_last_yn().contentEquals("Y")){
                        Intent intent = new Intent(DcrDetails.this, DcrSelectDoctor.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        if (HomeActivity.cancelStatus == 1) {
                            Intent intent = new Intent(DcrDetails.this, DcrSelectDoctor.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (HomeActivity.cancelStatus == 0) {
                            startActivity(new Intent(DcrDetails.this, DcrSelectDoctor.class));
                        }
                    }*/
                    if(temp_for_clearing_stack == 1) {
                        Intent intent1 = new Intent(MWRDetails.this, MWRmsr1.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    }else if(temp_for_clearing_stack == 0){
                        startActivity(new Intent(MWRDetails.this, MWRmsr1.class));
                    }
//                    startActivity(new Intent(MWRDetails.this, MWRmsr1.class));
                }else if(tempForMwrType == 0){
                    Intent intentRemarks = new Intent(MWRDetails.this, MWROtherDetails.class);
                    intentRemarks.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentRemarks);
//                    startActivity(new Intent(MWRDetails.this, MWROtherDetails.class));
                }
                break;
            default:
                break;
        }
    }

    //==============function for MwrType Spinner code starts============
    public void spinner_load_mwr_type(){

       /* userSingletonModel.setDcr_details_dcr_type_id("0");
        userSingletonModel.setDcr_details_dcr_type_name("Field Work");*/

        MWRTypeModel mwrTypeModel = new MWRTypeModel();
        mwrTypeModel.setId("0");
        mwrTypeModel.setMwr_type("Field Work");
        mwrTypeModelArrayList.add(mwrTypeModel);

        MWRTypeModel mwrTypeModel1 = new MWRTypeModel();
        mwrTypeModel1.setId("1");
        mwrTypeModel1.setMwr_type("Office Day");
        mwrTypeModelArrayList.add(mwrTypeModel1);

        MWRTypeModel mwrTypeModel2 = new MWRTypeModel();
        mwrTypeModel2.setId("2");
        mwrTypeModel2.setMwr_type("Travel");
        mwrTypeModelArrayList.add(mwrTypeModel2);

        MWRTypeModel mwrTypeModel3 = new MWRTypeModel();
        mwrTypeModel3.setId("3");
        mwrTypeModel3.setMwr_type("On Leave");
        mwrTypeModelArrayList.add(mwrTypeModel3);

        MWRTypeModel mwrTypeModel4 = new MWRTypeModel();
        mwrTypeModel4.setId("4");
        mwrTypeModel4.setMwr_type("Holiday");
        mwrTypeModelArrayList.add(mwrTypeModel4);

        MWRTypeModel mwrTypeModel6 = new MWRTypeModel();
        mwrTypeModel6.setId("6");
        mwrTypeModel6.setMwr_type("Other");
        mwrTypeModelArrayList.add(mwrTypeModel6);

        List<String> mwr_type = new ArrayList<>();
        for(int i=0; i<mwrTypeModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
            mwr_type.add(mwrTypeModelArrayList.get(i).getMwr_type());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MWRDetails.this, android.R.layout.simple_spinner_item, mwr_type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
        spinner_type.setSelection(1);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == -1){
//                        edt_date_select.setClickable(false);
                }else {
//                    Toast.makeText(getApplicationContext(), "Selected: " + dcrTypeModelArrayList.get(position).getId(), Toast.LENGTH_LONG).show();
                       /* msr_id = Integer.parseInt(trackingDetailsMsrNameModelArrayList.get(position).getMsr_id());
                        edt_date_select.setClickable(true);*/
                    if(!mwrTypeModelArrayList.get(position).getId().contentEquals("0")){
                        btn_next.setText("Remarks");
                        btn_next.setClickable(true);
                        btn_next.setEnabled(true);
                        btn_next.setAlpha(1.0f);

                        spinner_work_place.setVisibility(View.GONE);
                        ll_base_work_place.setVisibility(View.GONE);
                        tempForMwrType = 0;
                        userSingletonModel.setMwr_details_mwr_type_id(mwrTypeModelArrayList.get(position).getId());
                        userSingletonModel.setMwr_details_mwr_type_name(mwrTypeModelArrayList.get(position).getMwr_type());
                    }  else{
                        btn_next.setText("Next");
                        ll_base_work_place.setVisibility(View.VISIBLE);
                        spinner_work_place.setVisibility(View.VISIBLE);
                        tempForMwrType = 1;
                        userSingletonModel.setMwr_details_mwr_type_id(mwrTypeModelArrayList.get(position).getId());
                        userSingletonModel.setMwr_details_mwr_type_name(mwrTypeModelArrayList.get(position).getMwr_type());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //==============function for MwrType Spinner code ends============

    //=============function for MWR Details from api, code starts...=============
    public void loadMwrDetailsData(){

        String mwr_selected_date_output_format = "";
        Date mwr_selected_date_original_format = null;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            mwr_selected_date_original_format = originalFormat.parse(MWRWeekDate.mwr_selected_date);

            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            mwr_selected_date_output_format = outputFormat.format(mwr_selected_date_original_format);
        }catch (Exception e){
            e.printStackTrace();
        }

//        String url = Config.BaseUrlEpharma + "msr/dcr-master-data/" + userSingletonModel.getUser_id() + "/7" ;
        String url = Config.BaseUrlEpharma + "Manager/MWR-Master-Data/" + userSingletonModel.getUser_id() + "/" + mwr_selected_date_output_format + "/" + userSingletonModel.getCalendar_id() ;
        final ProgressDialog loading = ProgressDialog.show(MWRDetails.this, "Loading", "Please wait...", true, false);
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
    public void getResponseData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonData-=>",jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("work_place");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                MWRWorkPlaceModel mwrWorkPlaceModel = new MWRWorkPlaceModel();
                mwrWorkPlaceModel.setId(jsonObject1.getString("id"));
                mwrWorkPlaceModel.setName(jsonObject1.getString("name"));
                mwrWorkPlaceModelArrayList.add(mwrWorkPlaceModel);
            }

            //----------------Spinner code starts---------------
            List<String> work_place_list = new ArrayList<>();
            for(int i=0; i<mwrWorkPlaceModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                work_place_list.add(mwrWorkPlaceModelArrayList.get(i).getName());
            }

            //----for saved case, code starts----
            int count = 0;
            if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")) {
                for (int i = 0; i < mwrWorkPlaceModelArrayList.size(); i++) {
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                    if (mwrWorkPlaceModelArrayList.get(i).getId() == base_work_place_id_for_saved_data) {
//                    base_work_place_name_for_saved_data = mwrWorkPlaceModelArrayList.get(i).getName();
                        count = i + 1;
                        break;
                    }
                }
            }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
                for (int i = 0; i < mwrWorkPlaceModelArrayList.size(); i++) {
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                    if (mwrWorkPlaceModelArrayList.get(i).getId() == base_work_place_id_for_saved_data) {
//                    base_work_place_name_for_saved_data = mwrWorkPlaceModelArrayList.get(i).getName();
                        count = i + 1;
                        break;
                    }
                }
            }
            Log.d("CountDatatest-=>", String.valueOf(count));
            //----for saved case, code ends----

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MWRDetails.this, android.R.layout.simple_spinner_item, work_place_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_work_place.setAdapter(adapter);

            if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")) {
                spinner_work_place.setSelection(count);
            }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
                spinner_work_place.setSelection(count);
            }

            spinner_work_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == -1){
                        btn_next.setClickable(false);
                        btn_next.setEnabled(false);
                        btn_next.setAlpha(0.5f);
//                        Toast.makeText(getApplicationContext(),"Please Select Work Place",Toast.LENGTH_SHORT).show();
//                        edt_date_select.setClickable(false);
                    }else {
                        btn_next.setClickable(true);
                        btn_next.setEnabled(true);
                        btn_next.setAlpha(1.0f);

//                        Toast.makeText(getApplicationContext(),mwrWorkPlaceModelArrayList.get(position).getId(), Toast.LENGTH_LONG).show();
                        sqliteDb.updateMWRWorkPlaceId(MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), mwrWorkPlaceModelArrayList.get(position).getId(), db);
                        /*userSingletonModel.setBase_work_place_id(dcrDetailsListModelArrayList.get(position).getId());
                        userSingletonModel.setBase_work_place_name(dcrDetailsListModelArrayList.get(position).getName());*/

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //---------------Spinner code ends-----------------

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //=============function for MWR Details from api, code ends...===============

    //=============function to load previously saved Data from api, code starts==========
    public void loadPrevSavedData(int mwr_id){
        String url = Config.BaseUrlEpharma + "mwr/day_data/" + mwr_id;
        final ProgressDialog loading = ProgressDialog.show(MWRDetails.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getSavedResponseData(response);
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
    public void getSavedResponseData(String response){
        responseSavedData = response;
        Log.d("responseSavedData-=>",responseSavedData);
        try {
            JSONObject jsonObject = new JSONObject(responseSavedData);
            base_work_place_id_for_saved_data = jsonObject.getString("base_work_place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //=============function to load previously saved Data from api, code ends==========


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
