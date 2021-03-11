package org.arb.Nextgen.ePharma.MWR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.MWRMsr1Msr2DoctorModel;
import org.arb.Nextgen.ePharma.Model.MWRMsr1Msr2NameModel;
import org.arb.Nextgen.ePharma.Model.MWRMsr1Msr2WorkPlaceModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.MWRMsr1DoctorAdapter;
import org.arb.Nextgen.ePharma.adapter.MWRMsr1WorkPlaceAdapter;
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

import fr.ganfra.materialspinner.MaterialSpinner;

public class MWRmsr1 extends AppCompatActivity implements View.OnClickListener {
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<MWRMsr1Msr2NameModel> mwrMsr1Msr2NameModelArrayList = new ArrayList<>();
    ArrayList<MWRMsr1Msr2WorkPlaceModel> mwrMsr1Msr2WorkPlaceModelArrayList = new ArrayList<>();
    ArrayList<MWRMsr1Msr2DoctorModel> mwrMsr1Msr2DoctorModelArrayList = new ArrayList<>();
    TextView tv_mwr_no, tv_date, tv_status;

    MaterialSpinner spinner_type_msr1;
    Button btn_next_load, btn_msr1_doctor, btn_back, btn_cancel;
    public static Button btn_next;
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();
    public static String msr_id;
    RecyclerView recycler_view_worked_place, recycler_view_select_msr1_doctor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwr_msr1);
        spinner_type_msr1 = findViewById(R.id.spinner_type_msr1);
        btn_next_load = findViewById(R.id.btn_next_load);
        btn_msr1_doctor = findViewById(R.id.btn_msr1_doctor);
        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_mwr_no = findViewById(R.id.tv_mwr_no);
        tv_date = findViewById(R.id.tv_date);
        tv_status = findViewById(R.id.tv_status);
        recycler_view_worked_place = findViewById(R.id.recycler_view_worked_place);
        recycler_view_select_msr1_doctor = findViewById(R.id.recycler_view_select_msr1_doctor);

        //---------initializing sqlitedatabase, code starts---
        try {
            db = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS MWR(id INTEGER PRIMARY KEY AUTOINCREMENT, mwr_id VARCHAR, mwr_no VARCHAR, mwr_date VARCHAR, week_day VARCHAR, manager_id VARCHAR, cal_year_id VARCHAR, base_work_place_id VARCHAR, msr_1_id VARCHAR, msr_1_work_place VARCHAR, msr_1_doctor VARCHAR, msr_2_id VARCHAR, msr_2_work_place VARCHAR, msr_2_doctor, json VARCHAR, draft_yn VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase, code ends---

        btn_next_load.setOnClickListener(this);
        btn_msr1_doctor.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        //---making Next button default disable
        btn_next.setClickable(false);
        btn_next.setEnabled(false);
        btn_next.setAlpha(0.5f);

        loadMwrDetailsData(); //--calling function to load msr names

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view_worked_place = findViewById(R.id.recycler_view_worked_place);
        recycler_view_worked_place.setHasFixedSize(true);
        recycler_view_worked_place.setLayoutManager(new LinearLayoutManager(this));

        recycler_view_select_msr1_doctor = findViewById(R.id.recycler_view_select_msr1_doctor);
        recycler_view_select_msr1_doctor.setHasFixedSize(true);
        recycler_view_select_msr1_doctor.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        //---setting the values to the textview
        tv_mwr_no.setText("MWR No: "+MWRWeekDate.mwr_no);
        tv_date.setText("Date: "+MWRWeekDate.mwr_selected_date_for_appDisplay_format);

        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("0")){
            tv_status.setText("");
        }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")){
            tv_status.setText("Saved");
        }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
            tv_status.setText("Sent");
            btn_msr1_doctor.setClickable(false);
            btn_msr1_doctor.setAlpha(0.5f);
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
            case R.id.btn_next_load:
                loadMwrWorkPlace();
                break;
            case R.id.btn_msr1_doctor:
               /* String id="";
                for(int i=0;i<MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.size();i++){
                    id = id+","+MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.get(i).getId();
//                    Log.d("TestHqId-=>",DcrDoctorWorkPlaceAdapter.dcrSelectDoctorStockistChemistModelArrayList1.get(i).getId());
                    Log.d("TestHqId-=>",id);
                }*/
                if (MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
                }else {
//                    loadDoctorName();
                    saveMWRMsr1WorkPlace();
                    loadMsr1DoctorDetails();
                }
                break;
            case R.id.btn_next:
                if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
                    if(MWRDetails.temp_for_clearing_stack == 0) {
                        startActivity(new Intent(MWRmsr1.this, MWRmsr2.class));
                    }else if(MWRDetails.temp_for_clearing_stack == 1){
                        Intent intent = new Intent(MWRmsr1.this,MWRmsr2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }else {
                    /*saveMWRMsr1Doctor();
                    startActivity(new Intent(MWRmsr1.this, MWRmsr2.class));*/
                    if(MWRDetails.temp_for_clearing_stack == 0) {
                        saveMWRMsr1Doctor();
                        startActivity(new Intent(MWRmsr1.this, MWRmsr2.class));
                    }else if(MWRDetails.temp_for_clearing_stack == 1){
                        saveMWRMsr1Doctor();
                        Intent intent = new Intent(MWRmsr1.this,MWRmsr2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.btn_back:
                startActivity(new Intent(MWRmsr1.this,MWRDetails.class));
                break;
            case R.id.btn_cancel:
                MWRDetails.temp_for_clearing_stack = 1; //---making this item 1 for clearing backstack
                Intent intent = new Intent(MWRmsr1.this,MWRWeekDate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

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
        final ProgressDialog loading = ProgressDialog.show(MWRmsr1.this, "Loading", "Please wait...", true, false);
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
        if(!mwrMsr1Msr2NameModelArrayList.isEmpty()){
            mwrMsr1Msr2NameModelArrayList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonData-=>",jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("msr");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                MWRMsr1Msr2NameModel mwrMsr1Msr2NameModel = new MWRMsr1Msr2NameModel();
                mwrMsr1Msr2NameModel.setId(jsonObject1.getString("id"));
                mwrMsr1Msr2NameModel.setName(jsonObject1.getString("name"));
                mwrMsr1Msr2NameModelArrayList.add(mwrMsr1Msr2NameModel);
            }

            //----------------Spinner code starts---------------
            List<String> msr_list = new ArrayList<>();
            for(int i = 0; i< mwrMsr1Msr2NameModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                msr_list.add(mwrMsr1Msr2NameModelArrayList.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MWRmsr1.this, android.R.layout.simple_spinner_item, msr_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_type_msr1.setAdapter(adapter);

            //----for saved case, code starts----
            String worked_with_msr_1 = "";
            try {
                JSONObject jsonObject1 = new JSONObject(MWRDetails.responseSavedData);
                worked_with_msr_1 = jsonObject1.getString("worked_with_msr_1");
                Log.d("worked_with_msr_1-=>", worked_with_msr_1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            int count = 0;
            if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")) {
                for (int i = 0; i < mwrMsr1Msr2NameModelArrayList.size(); i++) {
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                    if (mwrMsr1Msr2NameModelArrayList.get(i).getId().contentEquals(worked_with_msr_1)) {
//                    base_work_place_name_for_saved_data = mwrWorkPlaceModelArrayList.get(i).getName();
                        count = i + 1;
                        break;
                    }
                }
            }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
                for (int i = 0; i < mwrMsr1Msr2NameModelArrayList.size(); i++) {
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                    if (mwrMsr1Msr2NameModelArrayList.get(i).getId().contentEquals(worked_with_msr_1)) {
//                    base_work_place_name_for_saved_data = mwrWorkPlaceModelArrayList.get(i).getName();
                        count = i + 1;
                        break;
                    }
                }
            }
            Log.d("CountDatatest-=>", String.valueOf(count));

            if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")) {
                spinner_type_msr1.setSelection(count);
               /* msr_id = mwrMsr1Msr2NameModelArrayList.get(count).getId();
                Log.d("msridTest-=>",msr_id);*/
            }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
                spinner_type_msr1.setSelection(count);
               /* msr_id = mwrMsr1Msr2NameModelArrayList.get(count).getId();
                Log.d("msridTest-=>",msr_id);*/
            }
            //----for saved case, code ends----

            spinner_type_msr1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == -1){
                        btn_next_load.setClickable(false);
                        btn_next_load.setEnabled(false);
                        btn_next_load.setAlpha(0.5f);
//                        Toast.makeText(getApplicationContext(),"Please Select Work Place",Toast.LENGTH_SHORT).show();
//                        edt_date_select.setClickable(false);
                    }else {
                        btn_next_load.setClickable(true);
                        btn_next_load.setEnabled(true);
                        btn_next_load.setAlpha(1.0f);

//                        Toast.makeText(getApplicationContext(), mwrMsr1Msr2NameModelArrayList.get(position).getId(), Toast.LENGTH_LONG).show();
                        msr_id = mwrMsr1Msr2NameModelArrayList.get(position).getId();
                        sqliteDb.updateMWRMsr1Msr2Name("msr_1", MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), mwrMsr1Msr2NameModelArrayList.get(position).getId(), db);

                        Log.d("msridTest-=>",msr_id);
                        //===========for saved purpose, code starts=========
                        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1")) {
                            /*loadMwrWorkPlace();

                            saveMWRMsr1WorkPlace(); //wknd added
                            loadMsr1DoctorDetails(); //wknd added*/

                            Asynctask asynctask = new Asynctask();
                            asynctask.execute();
                        }else if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("2")){
                            /*loadMwrWorkPlace();

                            saveMWRMsr1WorkPlace(); //wknd added
                            loadMsr1DoctorDetails(); //wknd added*/

                            Asynctask asynctask = new Asynctask();
//                            asynctask.execute(String.valueOf(1000));
                            asynctask.execute();
                        }
                        //==========for saved purpose, code ends==========

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


    //=============function to load workplace details, code starts============
    public void loadMwrWorkPlace(){
        String url = Config.BaseUrlEpharma + "MSR/Work-Place/" + msr_id + "/doctor/" + userSingletonModel.getCalendar_id() ;
        final ProgressDialog loading = ProgressDialog.show(MWRmsr1.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getWorkPlaceResponseData(response);
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
    public void getWorkPlaceResponseData(String response){
        if(!mwrMsr1Msr2WorkPlaceModelArrayList.isEmpty()){
            mwrMsr1Msr2WorkPlaceModelArrayList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonDataWorkOlace-=>",jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("work_places");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                MWRMsr1Msr2WorkPlaceModel mwrMsr1Msr2WorkPlaceModel = new MWRMsr1Msr2WorkPlaceModel();
                mwrMsr1Msr2WorkPlaceModel.setId(jsonObject1.getString("id"));
                mwrMsr1Msr2WorkPlaceModel.setName(jsonObject1.getString("name"));
                mwrMsr1Msr2WorkPlaceModel.setHq_id(jsonObject1.getString("hq_id"));
                mwrMsr1Msr2WorkPlaceModel.setHq_name(jsonObject1.getString("hq_name"));
                mwrMsr1Msr2WorkPlaceModel.setStatus("0");
                mwrMsr1Msr2WorkPlaceModel.setChecked(false);

                mwrMsr1Msr2WorkPlaceModelArrayList.add(mwrMsr1Msr2WorkPlaceModel);
            }
            recycler_view_worked_place.setAdapter(new MWRMsr1WorkPlaceAdapter(MWRmsr1.this, mwrMsr1Msr2WorkPlaceModelArrayList));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //=============function to load workplace details, code ends============

    //============function to save MWRMsr1WorkPlace data in sqlite for further reference, code starts==========
    public void saveMWRMsr1WorkPlace() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (!MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.isEmpty()) {
            for(int i = 0; i<MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.size(); i++){
                if(MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.get(i).getStatus().contentEquals("1")) {
                    arrayList.add(MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.get(i).getId());
                    Log.d("testId-=>",MWRMsr1WorkPlaceAdapter.mwrMsr1Msr2WorkPlaceModelArrayList.get(i).getId());
                }
            }
            sqliteDb.updateMWRMsr1Msr2WorkPlaceId("msr_1", MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), arrayList, db);
        }
    }
    //============function to save MWRMsr1WorkPlace data in sqlite for further reference, code ends==========


    //==========function to load Msr1's doctor details, code starts========
    public void loadMsr1DoctorDetails(){
        String hq_id = sqliteDb.fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(9,MWRHome.mwr_no,MWRWeekDate.mwr_selected_date,userSingletonModel.getUser_id(),userSingletonModel.getCalendar_id(),db);
//        Toast.makeText(getApplicationContext(),"MsrId: "+hq_id,Toast.LENGTH_LONG).show();
        Log.d("MsrId-=> ",hq_id);
        hq_id = hq_id.replace("[","");
        hq_id = hq_id.replace("]","");
        hq_id = hq_id.replace(" ","");

        String url = Config.BaseUrlEpharma + "MSR/Customer-List/" + msr_id + "/"+hq_id.trim()+ "/doctor/" + userSingletonModel.getCalendar_id() ;
        Log.d("url-=>",url);
        final ProgressDialog loading = ProgressDialog.show(MWRmsr1.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getMsr1DoctorDetailsResponseData(response);
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

    public void getMsr1DoctorDetailsResponseData(String response){
        if(!mwrMsr1Msr2DoctorModelArrayList.isEmpty()){
            mwrMsr1Msr2DoctorModelArrayList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonDataDoctorNames-=>", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("customers");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                MWRMsr1Msr2DoctorModel mwrMsr1Msr2DoctorModel = new MWRMsr1Msr2DoctorModel();
                mwrMsr1Msr2DoctorModel.setId(jsonObject1.getString("id"));
                mwrMsr1Msr2DoctorModel.setStatus("0");
                mwrMsr1Msr2DoctorModel.setChecked(false); //--wknd added
                /*mwrMsr1Msr2DoctorModel.setEcl_no(jsonObject1.getString("ecl_no"));
                mwrMsr1Msr2DoctorModel.setName(jsonObject1.getString("name"));*/

                String currentString = jsonObject1.getString("name");
                String[] separated = currentString.split("~");
                mwrMsr1Msr2DoctorModel.setName(separated[0]);
                mwrMsr1Msr2DoctorModel.setWork_place_id(separated[1]);
                mwrMsr1Msr2DoctorModel.setEcl_no(separated[2]);

                mwrMsr1Msr2DoctorModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                mwrMsr1Msr2DoctorModelArrayList.add(mwrMsr1Msr2DoctorModel);
            }
            recycler_view_select_msr1_doctor.setAdapter(new MWRMsr1DoctorAdapter(MWRmsr1.this, mwrMsr1Msr2DoctorModelArrayList));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //==========function to load Msr1's doctor details, code ends========

    //============function to save MWRMsr1Doctor data in sqlite for further reference, code starts==========
    public void saveMWRMsr1Doctor() {
        try {
            final JSONObject DocumentElementobj = new JSONObject();
            JSONArray reqDctr = new JSONArray();
            if (!MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.isEmpty()) {
                for (int i = 0; i < MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.size(); i++) {
                    if (MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.get(i).getStatus().contentEquals("1")) {
//                        arrayList1.add(MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.get(i).getId());
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("customer_id", MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.get(i).getId());
                        reqObj.put("ecl_no", MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.get(i).getEcl_no());
                        reqObj.put("work_place_id", MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.get(i).getWork_place_id());
                        reqDctr.put(reqObj);
                    }
                }
                DocumentElementobj.put("values",reqDctr);
                Log.d("jsonMsr1DoctorTest-=>",DocumentElementobj.toString());
                sqliteDb.updateMWRMsr1Msr2Doctor("msr_1", MWRHome.mwr_no, MWRWeekDate.mwr_selected_date, userSingletonModel.getUser_id(), userSingletonModel.getCalendar_id(), DocumentElementobj.toString(), db);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //============function to save MWRMsr1Doctor data in sqlite for further reference, code ends==========


    //============code for Asynctask, starts (asynctask must be needed to use, as two different methods need to execute respectively) ========
    public class Asynctask extends AsyncTask<String, Void, String> {

       /* @Override
        protected Void doInBackground(Void... voids) {
            loadMwrWorkPlace();
            return null;
        }*/

        private String resp;
        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadMwrWorkPlace();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveMWRMsr1WorkPlace();
            loadMsr1DoctorDetails();
        }
    }
    //============code for Asynctask, ends========

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
