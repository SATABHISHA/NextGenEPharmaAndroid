package org.arb.Nextgen.ePharma.DCR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.DcrDctrChemistStockistWorkPlaceModel;
import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.DcrSelectStockistAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrStockistWorkPlaceAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DcrSelectStockist extends AppCompatActivity implements View.OnClickListener {
    Button btn_next, btn_load_stockist, btn_skip, btn_summary, btn_cancel, btn_back;
    RecyclerView recycler_view_worked_place, recycler_view_select_stockist;
    ArrayList<DcrDctrChemistStockistWorkPlaceModel> dctrChemistStockistWorkPlaceModelArrayList = new ArrayList<>();
    ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList = new ArrayList<>();
//    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectStockistArrayList = new ArrayList<>();
    RelativeLayout rl_back_arrow;
    ImageButton imgbtn_arrrow;

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

    SQLiteDatabase db, db1;
    SqliteDb sqliteDb = new SqliteDb();

    public Context context = this;

    TextView tv_date, tv_dcr_no, tv_status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_select_stockist);
        btn_next = findViewById(R.id.btn_next);
        btn_load_stockist = findViewById(R.id.btn_load_stockist);
//        btn_skip = findViewById(R.id.btn_skip);
        /*rl_back_arrow = findViewById(R.id.rl_back_arrow);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);*/
//        btn_summary = findViewById(R.id.btn_summary);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_date = findViewById(R.id.tv_date);
        tv_dcr_no = findViewById(R.id.tv_dcr_no);
        tv_status = findViewById(R.id.tv_status);
        btn_back = findViewById(R.id.btn_back);

        tv_status.setText("Status: "+userSingletonModel.getApproval_status_name());
        tv_dcr_no.setText("DCR No: "+userSingletonModel.getDcr_no_for_dcr_summary());
        tv_date.setText(userSingletonModel.getSelected_date_calendar());

        btn_next.setOnClickListener(this);
        btn_load_stockist.setOnClickListener(this);
//        btn_skip.setOnClickListener(this);
       /* rl_back_arrow.setOnClickListener(this);
        imgbtn_arrrow.setOnClickListener(this);*/
//        btn_summary.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view_worked_place = findViewById(R.id.recycler_view_worked_place);
        recycler_view_worked_place.setHasFixedSize(true);
        recycler_view_worked_place.setLayoutManager(new LinearLayoutManager(this));

        recycler_view_select_stockist = findViewById(R.id.recycler_view_select_stockist);
        recycler_view_select_stockist.setHasFixedSize(true);
        recycler_view_select_stockist.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        //----------creating sqlite database, code starts-------
        try {
            db = openOrCreateDatabase("DCRDEtails", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------creating sqlite database, code ends-------




        fetchDcrData(); //---calling function to fetchDcr Data on page load

        //----making static arraylist default clear(otherwise on back it also carries prev values---
       /* if(!DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }*/


    }

    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //----commented on 15th July
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
    protected void onResume() {
        super.onResume();
        fetchDcrData();
        if(DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.size()>0){
            recycler_view_select_stockist.setVisibility(View.VISIBLE);
        }else{
            recycler_view_select_stockist.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                saveToArrayList();
                /*if(HomeActivity.cancelStatus == 1) {
                    Intent intent = new Intent(DcrSelectStockist.this, DcrSummary.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if(HomeActivity.cancelStatus == 0) {
                    startActivity(new Intent(DcrSelectStockist.this, DcrSummary.class));
                }*/
                HomeActivity.cancelStatus = 0;
                Intent intent = new Intent(DcrSelectStockist.this, DcrSummary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
           /* case R.id.rl_back_arrow:
                *//*Intent intent1 = new Intent(DcrSelectStockist.this, DcrSelectChemist.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);*//*
                saveToArrayList();
                startActivity(new Intent(DcrSelectStockist.this, DcrSelectChemist.class));
                break;
            case R.id.imgbtn_arrrow:
                *//*Intent intent2 = new Intent(DcrSelectStockist.this, DcrSelectChemist.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);*//*
                saveToArrayList();
                startActivity(new Intent(DcrSelectStockist.this, DcrSelectChemist.class));
                break;*/
            case R.id.btn_back:
                saveToArrayList();
                startActivity(new Intent(DcrSelectStockist.this, DcrSelectChemist.class));
                break;
            case R.id.btn_cancel:

                //---------Alert dialog code starts(added on 21st nov)--------
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DcrSelectStockist.this);
                alertDialogBuilder.setMessage("Unsaved data will be lost. Do you want to continue?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts---
                                try {
                                    db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
                                    db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---
                                sqliteDb.cleanDataDCR(userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(),db1);
                                HomeActivity.cancelStatus = 1;
                                Intent intentCancel = new Intent(DcrSelectStockist.this, DcrHome.class);
//                                intentCancel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intentCancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentCancel);
                            }
                        });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                //--------Alert dialog code ends--------

                break;
            /*case R.id.btn_skip:
                Intent intent3 = new Intent(DcrSelectStockist.this, DcrSummary.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent3);
                break;*/
            case R.id.btn_load_stockist:
                if (DcrStockistWorkPlaceAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
                }else {
                    loadStockistName();
                }
                break;
            /*case R.id.btn_summary:
                Intent intent4 = new Intent(DcrSelectStockist.this, DcrSummary.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent4);
                break;*/
                default:
                    break;
        }
    }


    //============function to save data in the static arraylist for DcrSummary page, code starts==========
    public void saveToArrayList(){
        /*if(!DcrHome.dcrSelectStockistArrayList.isEmpty()){
            DcrHome.dcrSelectStockistArrayList.clear();
        }
        if(!DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            for (int i = 0; i < DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.size(); i++) {
                if (DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus().contentEquals("1")) {
                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                    dcrSelectDoctorStockistChemistModel.setId(DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getId());
                    dcrSelectDoctorStockistChemistModel.setStatus(DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus());
                    dcrSelectDoctorStockistChemistModel.setName(DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getName());
                    dcrSelectDoctorStockistChemistModel.setEcl_no(DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getEcl_no());
                    dcrSelectDoctorStockistChemistModel.setWork_place_id(DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getWork_place_id());
                    dcrSelectDoctorStockistChemistModel.setWork_place_name("");
                    dcrSelectDoctorStockistChemistModel.setLast_visit_date("");
                    dcrSelectDoctorStockistChemistModel.setAmount("0.00");
                    DcrHome.dcrSelectStockistArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
            }
        }*/

        try {
            final JSONObject DocumentElementobj1 = new JSONObject();
            JSONArray reqStockist = new JSONArray();
            if (!DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
                for (int i = 0; i < DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.size(); i++) {
                    if (DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus().contentEquals("1")) {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("name", DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getName());
                        reqObj.put("id", DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getId());
                        reqObj.put("work_place_id", DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getWork_place_id());
                        reqObj.put("ecl_no", DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getEcl_no());
                        reqObj.put("status", DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus());
                        reqObj.put("work_place_name", "");
                        reqObj.put("amount", "0.00");
                        reqObj.put("last_visit_date", "");
                        reqStockist.put(reqObj);
                    }
                }
                DocumentElementobj1.put("values",reqStockist);
                /*DcrHome.jsonDcrSelectDoctor = DocumentElementobj1.toString();
                userSingletonCustomJsonModel.setJsonDoctorString(DocumentElementobj1.toString());*/
                Log.d("jsonStockistTest-=>",DocumentElementobj1.toString());

                //---calling SqliteDb class to updateDCR
                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts---
                try {
                    db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
                    db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---
//                sqliteDb.createDatabase();
                sqliteDb.updateDCR("Stockist",DocumentElementobj1.toString(), userSingletonModel.getUser_id(), userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);
//                sqliteDb.updateStockist(DocumentElementobj1.toString(),userSingletonModel.getUser_id(),db1);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //============function to save data in the static arraylist for DcrSummary page, code ends==========


    //=============fetch sqlite data for worked place, code starts...============
    public void fetchDcrData(){
        Cursor c = db.rawQuery("SELECT * FROM dcrdetail", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();

        String json = "";
        while(!c.isAfterLast()){
            json = c.getString(1);
            c.moveToNext();
        }

        Log.d("fetchingSqltData-=-=>",json);
        if(!dctrChemistStockistWorkPlaceModelArrayList.isEmpty()){
            dctrChemistStockistWorkPlaceModelArrayList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);



            //----------for manager's section, code starts-----
            JSONArray jsonArray1 = jsonObject.getJSONArray("work_place_stockist");
            for(int i=0;i<jsonArray1.length();i++){
                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                DcrDctrChemistStockistWorkPlaceModel dcrDctrChemistStockistWorkPlaceModel = new DcrDctrChemistStockistWorkPlaceModel();
                dcrDctrChemistStockistWorkPlaceModel.setId(jsonObject1.getString("id"));
                dcrDctrChemistStockistWorkPlaceModel.setName(jsonObject1.getString("name"));
                dcrDctrChemistStockistWorkPlaceModel.setHq_id(jsonObject1.getString("hq_id"));
                dcrDctrChemistStockistWorkPlaceModel.setHq_name(jsonObject1.getString("hq_name"));
                dcrDctrChemistStockistWorkPlaceModel.setStatus("0");
                dctrChemistStockistWorkPlaceModelArrayList.add(dcrDctrChemistStockistWorkPlaceModel);
            }
            recycler_view_worked_place.setAdapter(new DcrStockistWorkPlaceAdapter(DcrSelectStockist.this, dctrChemistStockistWorkPlaceModelArrayList));
            //----------for manager's section, code ends-----


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //=============fetch sqlite data for worked place, code ends...============

    //=============fetch api data for Doctor's name using volley, code starts=========
    public void loadStockistName() {

        if(!dcrSelectDoctorStockistChemistModelArrayList.isEmpty()){
            dcrSelectDoctorStockistChemistModelArrayList.clear();
        }

        String id = "";
        String hqId = "";
        for (int i = 0; i < DcrStockistWorkPlaceAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size(); i++) {
            if (DcrStockistWorkPlaceAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getStatus().contains("1")) {
                id = id + DcrStockistWorkPlaceAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getId() + ",";
            }
        }
        if(id!="") {
            hqId = id.substring(0, id.length() - 1);
            Log.d("Id test-=-=>", hqId);
        }else if(id==""){
            Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
        }

//        String url = Config.BaseUrlEpharma + "MSR/Customer-List/" + userSingletonModel.getUser_id() + "/" + hqId + "/stockist/7";
        String url = Config.BaseUrlEpharma + "MSR/Customer-List/" + userSingletonModel.getUser_id() + "/" + hqId + "/stockist/" + userSingletonModel.getCalendar_id();
        final ProgressDialog loading = ProgressDialog.show(DcrSelectStockist.this, "Loading", "Please wait...", true, false);
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
    public void getResponseData (String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonData-=>", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("customers");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
                /*dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));*/

                String currentString = jsonObject1.getString("name");
                String[] separated = currentString.split("~");
                dcrSelectDoctorStockistChemistModel.setName(separated[0]);
                dcrSelectDoctorStockistChemistModel.setWork_place_id(separated[1]);
                dcrSelectDoctorStockistChemistModel.setEcl_no(separated[2]);
                Log.d("Workplaceid-=>",separated[1]);
                Log.d("EclNo-=>",separated[2]);

                dcrSelectDoctorStockistChemistModel.setStatus("0");
                dcrSelectDoctorStockistChemistModelArrayList.add(dcrSelectDoctorStockistChemistModel);
            }
            recycler_view_select_stockist.setVisibility(View.VISIBLE);
            recycler_view_select_stockist.setAdapter(new DcrSelectStockistAdapter(DcrSelectStockist.this, dcrSelectDoctorStockistChemistModelArrayList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //=============fetch api data for Doctor's name using volley, code ends===========

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
