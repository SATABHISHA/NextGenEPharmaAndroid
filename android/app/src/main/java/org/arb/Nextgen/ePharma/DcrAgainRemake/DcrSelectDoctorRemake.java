package org.arb.Nextgen.ePharma.DcrAgainRemake;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrDoctorWorkPlaceRemakeAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrSelectDoctorRemakeAdapter;
import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.DcrDctrChemistStockistWorkPlaceModel;
import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.config.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DcrSelectDoctorRemake extends AppCompatActivity implements View.OnClickListener {
    Button  btn_load_doctor, btn_cancel, btn_add;
    RecyclerView recycler_view_worked_place, recycler_view_select_doctor;
    ArrayList<DcrDctrChemistStockistWorkPlaceModel> dctrChemistStockistWorkPlaceModelArrayList = new ArrayList<>();
    ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList = new ArrayList<>();

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    SqliteDb sqliteDb = new SqliteDb();

    SQLiteDatabase db, db1;

    public Context context = this;

//    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList = new ArrayList<>();

    public ArrayList<DcrSelectDoctorStockistChemistModel> dcrViewDoctrArrayList = new ArrayList<>(); //--added o 2nd feb
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_select_doctor_remake);

        btn_load_doctor = findViewById(R.id.btn_load_doctor);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_add = findViewById(R.id.btn_add);

        btn_load_doctor.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_add.setOnClickListener(this);

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view_worked_place = findViewById(R.id.recycler_view_worked_place);
        recycler_view_worked_place.setHasFixedSize(true);
        recycler_view_worked_place.setLayoutManager(new LinearLayoutManager(this));

        recycler_view_select_doctor = findViewById(R.id.recycler_view_select_doctor);
        recycler_view_select_doctor.setHasFixedSize(true);
        recycler_view_select_doctor.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======


        //----------creating sqlite database, code starts-------
        try {
            db = openOrCreateDatabase("DCRDEtails", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------creating sqlite database, code ends-------

        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts added on 2nd feb---
        try {
            db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---
        fetchDcrData();
//        loadDoctor_to_retain_previous_data();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

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
//           //No value for work_place_doctor...22nd march
            JSONArray jsonArray1 = jsonObject.getJSONArray("work_place");
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
            recycler_view_worked_place.setAdapter(new DcrDoctorWorkPlaceRemakeAdapter(DcrSelectDoctorRemake.this, dctrChemistStockistWorkPlaceModelArrayList));
            //----------for manager's section, code ends-----


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //=============fetch sqlite data for worked place, code ends...============

    //=============fetch api data for Doctor's name using volley, code starts=========
    public void loadDoctorName() {

        if(!dcrSelectDoctorStockistChemistModelArrayList.isEmpty()){
            dcrSelectDoctorStockistChemistModelArrayList.clear();
        }

        loadDoctor_to_retain_previous_data();
        String id = "";
        String hqId = "";
        for (int i = 0; i < DcrDoctorWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size(); i++) {
            if (DcrDoctorWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getStatus().contains("1")) {
                id = id + DcrDoctorWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getId() + ",";
            }
        }
        if(id!="") {
            hqId = id.substring(0, id.length() - 1);
            Log.d("Id test-=-=>", hqId);
        }else if(id==""){
            Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
        }

//            String url = Config.BaseUrlEpharma + "MSR/Customer-List/" + userSingletonModel.getUser_id() + "/" + hqId + "/doctor/7";
        String url = Config.BaseUrlEpharma + "MSR/Customer-List/" + userSingletonModel.getUser_id() + "/" + hqId + "/doctor/" + userSingletonModel.getCalendar_id();
        Log.d("DoctorUrl-=>",url);
        final ProgressDialog loading = ProgressDialog.show(DcrSelectDoctorRemake.this, "Loading", "Please wait...", true, false);
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
//                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
//                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                String currentString = jsonObject1.getString("name");
                String[] separated = currentString.split("~");
                dcrSelectDoctorStockistChemistModel.setName(separated[0]);
                dcrSelectDoctorStockistChemistModel.setWork_place_id(separated[1]);
                dcrSelectDoctorStockistChemistModel.setEcl_no(separated[2]);
                dcrSelectDoctorStockistChemistModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                Log.d("Workplaceid-=>",separated[1]);
                Log.d("EclNo-=>",separated[2]);
                dcrSelectDoctorStockistChemistModel.setStatus("0");
                dcrSelectDoctorStockistChemistModelArrayList.add(dcrSelectDoctorStockistChemistModel);
            }
            recycler_view_select_doctor.setVisibility(View.VISIBLE);
            recycler_view_select_doctor.setAdapter(new DcrSelectDoctorRemakeAdapter(DcrSelectDoctorRemake.this, dcrSelectDoctorStockistChemistModelArrayList));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //=============fetch api data for Doctor's name using volley, code ends===========

    //============function to save data in the static arraylist for DcrSummary page, code starts==========
    public void saveToArrayList(){

        try {
            final JSONObject DocumentElementobj1 = new JSONObject();
            JSONArray reqDctr = new JSONArray();
            if (!DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {

                for (int i = 0; i < DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.size(); i++) {
                    if (DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus().contentEquals("1")) {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("name", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getName());
                        reqObj.put("id", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getId());
                        reqObj.put("work_place_id", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getWork_place_id());
                        reqObj.put("ecl_no", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getEcl_no());
                        reqObj.put("status", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus());
                        reqObj.put("work_place_name", "");
//                        reqObj.put("amount", "0.00");
                        reqObj.put("amount", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getAmount());
                        reqObj.put("last_visit_date", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getLast_visit_date());
                        reqDctr.put(reqObj);
                    }
                }
                DocumentElementobj1.put("values",reqDctr);
               /* DcrHome.jsonDcrSelectDoctor = DocumentElementobj1.toString();
                userSingletonCustomJsonModel.setJsonDoctorString(DocumentElementobj1.toString());*/
                Log.d("jsonDoctorTest-=>",DocumentElementobj1.toString());

                //---calling SqliteDb class to updateDCR
//                sqliteDb.createDatabase();

                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts---
                try {
                    db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
                    db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---
                sqliteDb.updateDCR("Doctor",DocumentElementobj1.toString(),userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);
//                sqliteDb.updateDoctor(DocumentElementobj1.toString(),userSingletonModel.getUser_id(),db1);
                Intent intent = new Intent(this, DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    //============function to save data in the static arraylist for DcrSummary page, code ends==========

    //-----function to retain previous data, code starts (added on 2nd feb)-------
    public void loadDoctor_to_retain_previous_data(){

        //---doctor
        String jsonDoctor = "";
        jsonDoctor = sqliteDb.fetch(6,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
        try {
            if(!dcrViewDoctrArrayList.isEmpty()){
                dcrViewDoctrArrayList.clear();
            }

            if(jsonDoctor!=null) {
                Log.d("sqlitejsontest-=>",jsonDoctor);
                JSONObject jsonObject = new JSONObject(jsonDoctor);

                JSONArray jsonArray = jsonObject.getJSONArray("values");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                    dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
//                dcrSelectDoctorStockistChemistModel.setStatus(jsonObject1.getString("status"));
                    dcrSelectDoctorStockistChemistModel.setStatus("1");
                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("work_place_id"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                    dcrSelectDoctorStockistChemistModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("amount"));
                    dcrViewDoctrArrayList.add(dcrSelectDoctorStockistChemistModel);

                    dcrSelectDoctorStockistChemistModelArrayList.add(dcrSelectDoctorStockistChemistModel);
//                    DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //-----function to retain previous data, code ends (added on 2nd feb)-------
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_load_doctor:
                String name="";
                for(int i=0;i<DcrDoctorWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size();i++){
                    name = name+","+DcrDoctorWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getStatus();
//                    Log.d("TestHqId-=>",DcrDoctorWorkPlaceAdapter.dcrSelectDoctorStockistChemistModelArrayList1.get(i).getId());
                    Log.d("TestHqId-=>",name);
                }
                if (DcrDoctorWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
                }else {
                    loadDoctorName();
                }
                break;
            case R.id.btn_cancel:
                Intent intent = new Intent(this, DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_add:
                saveToArrayList();
                break;
        }
    }
}
