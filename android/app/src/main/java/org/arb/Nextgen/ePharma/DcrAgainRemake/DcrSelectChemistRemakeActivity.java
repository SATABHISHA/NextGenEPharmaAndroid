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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrChemistWorkPlaceRemakeAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrSelectChemistRemakeAdapter;
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

public class DcrSelectChemistRemakeActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_add, btn_load_chemist, btn_cancel;
    RecyclerView recycler_view_worked_place, recycler_view_select_chemist;
    ArrayList<DcrDctrChemistStockistWorkPlaceModel> dctrChemistStockistWorkPlaceModelArrayList = new ArrayList<>();
    ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList = new ArrayList<>();
    //    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectChemistArrayList = new ArrayList<>();
    RelativeLayout rl_back_arrow;
    ImageButton imgbtn_arrrow;

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

    SQLiteDatabase db, db1;
    SqliteDb sqliteDb = new SqliteDb();

    public Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dcr_select_chemist_remake);
        btn_add = findViewById(R.id.btn_add);
        btn_load_chemist = findViewById(R.id.btn_load_chemist);
       /* rl_back_arrow = findViewById(R.id.rl_back_arrow);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);*/
//        btn_skip = findViewById(R.id.btn_skip);
//        btn_summary = findViewById(R.id.btn_summary);
        btn_cancel = findViewById(R.id.btn_cancel);





        btn_load_chemist.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view_worked_place = findViewById(R.id.recycler_view_worked_place);
        recycler_view_worked_place.setHasFixedSize(true);
        recycler_view_worked_place.setLayoutManager(new LinearLayoutManager(this));

        recycler_view_select_chemist = findViewById(R.id.recycler_view_select_chemist);
        recycler_view_select_chemist.setHasFixedSize(true);
        recycler_view_select_chemist.setLayoutManager(new LinearLayoutManager(this));
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


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btn_add:
               saveToArrayList();
               break;
           case R.id.btn_cancel:
               Intent intent_cancel = new Intent(this,DcrDetailsRemakeActivity.class);
               intent_cancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent_cancel);
               break;
           case R.id.btn_load_chemist:
               if (DcrChemistWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size() < 0) {
                   Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
               }else {
                   loadChemistName();
               }
               break;
       }
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
//            JSONArray jsonArray1 = jsonObject.getJSONArray("work_place_chemist");
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
            recycler_view_worked_place.setAdapter(new DcrChemistWorkPlaceRemakeAdapter(this, dctrChemistStockistWorkPlaceModelArrayList));
            //----------for manager's section, code ends-----


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //=============fetch sqlite data for worked place, code ends...============

    //=============fetch api data for Doctor's name using volley, code starts=========
    public void loadChemistName() {
        if(!dcrSelectDoctorStockistChemistModelArrayList.isEmpty()){
            dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        loadChemist_to_retain_previous_data();
        String id = "";
        String hqId = "";
        for (int i = 0; i < DcrChemistWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.size(); i++) {
            if (DcrChemistWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getStatus().contains("1")) {
                id = id + DcrChemistWorkPlaceRemakeAdapter.dcrDctrChemistStockistWorkPlaceModelArrayList.get(i).getId() + ",";
            }
        }
        if(id!="") {
            hqId = id.substring(0, id.length() - 1);
            Log.d("Id test-=-=>", hqId);
        }else if(id==""){
            Toast.makeText(getApplicationContext(), "Please select atleast one value", Toast.LENGTH_LONG).show();
        }

//        String url = Config.BaseUrlEpharma + "MSR/Customer-List/" + userSingletonModel.getUser_id() + "/" + hqId + "/chemist/7";
        String url = Config.BaseUrlEpharma + "epharma/MSR/Customer-List/" +userSingletonModel.getCorp_id()+"/"+ userSingletonModel.getUser_id() + "/" + hqId + "/chemist/" + userSingletonModel.getCalendar_id();
        Log.d("urlChemist-=>",url);
        final ProgressDialog loading = ProgressDialog.show(DcrSelectChemistRemakeActivity.this, "Loading", "Please wait...", true, false);
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
            recycler_view_select_chemist.setVisibility(View.VISIBLE);
            recycler_view_select_chemist.setAdapter(new DcrSelectChemistRemakeAdapter(DcrSelectChemistRemakeActivity.this, dcrSelectDoctorStockistChemistModelArrayList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //=============fetch api data for Doctor's name using volley, code ends===========

    //============function to save data in the static arraylist for DcrSummary page, code starts==========
    public void saveToArrayList(){
        try {
            final JSONObject DocumentElementobj1 = new JSONObject();
            JSONArray reqChemist = new JSONArray();
            if (!DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
                for (int i = 0; i < DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.size(); i++) {
                    if (DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus().contentEquals("1")) {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("name", DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getName());
                        reqObj.put("id", DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getId());
                        reqObj.put("work_place_id", DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getWork_place_id());
                        reqObj.put("ecl_no", DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getEcl_no());
                        reqObj.put("status", DcrSelectChemistRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus());
                        reqObj.put("work_place_name", "");
//                        reqObj.put("amount", "0.00");
                        reqObj.put("amount", DcrSelectDoctorRemakeAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getAmount());
                        reqObj.put("last_visit_date", "");
                        reqChemist.put(reqObj);
                    }
                }
                DocumentElementobj1.put("values",reqChemist);
                /*DcrHome.jsonDcrSelectDoctor = DocumentElementobj1.toString();
                userSingletonCustomJsonModel.setJsonDoctorString(DocumentElementobj1.toString());*/
                Log.d("jsonChemistTest-=>",DocumentElementobj1.toString());

                //---calling SqliteDb class to updateDCR
                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts---
                try {
                    db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
                    db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, misc VARCHAR)");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---
//                sqliteDb.createDatabase();
                sqliteDb.updateDCR("Chemist",DocumentElementobj1.toString(),userSingletonModel.getUser_id(), userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);
//                sqliteDb.updateChemist(DocumentElementobj1.toString(),userSingletonModel.getUser_id(),db1);

                Intent intent = new Intent(this, DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),"Please select atleast one chemist",Toast.LENGTH_LONG).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //============function to save data in the static arraylist for DcrSummary page, code ends==========

    //-----function to retain previous data, code starts (added on 2nd feb)-------
    public void loadChemist_to_retain_previous_data(){

        //---chemist
        String jsonChemist = "";
        jsonChemist = sqliteDb.fetch(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
        try {

            if(jsonChemist!=null) {
                Log.d("sqlitejsontest-=>",jsonChemist);
                JSONObject jsonObject = new JSONObject(jsonChemist);

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
                    dcrSelectDoctorStockistChemistModelArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //-----function to retain previous data, code ends (added on 2nd feb)-------
}
