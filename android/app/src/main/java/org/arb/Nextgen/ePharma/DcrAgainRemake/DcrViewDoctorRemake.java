package org.arb.Nextgen.ePharma.DcrAgainRemake;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrViewDoctorRemakeAdapter;
import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DcrViewDoctorRemake extends AppCompatActivity implements View.OnClickListener {
    public ArrayList<DcrSelectDoctorStockistChemistModel> dcrViewDoctrArrayList = new ArrayList<>();
    SQLiteDatabase db1;
    SqliteDb sqliteDb = new SqliteDb();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    RecyclerView recycler_view;
    public static DcrViewDoctorRemakeAdapter dcrViewDoctorRemakeAdapter;
    Button btn_ok, btn_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_view_doctor_remake);

        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        dcrViewDoctorRemakeAdapter = new DcrViewDoctorRemakeAdapter(this,dcrViewDoctrArrayList);
        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======
        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts---
        try {
            db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---

        loadDoctor();

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //-----function to load doctor,chemist,stockist from jsonObject string, code starts-------
    public void loadDoctor(){

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
                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("work_place_id"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                    dcrSelectDoctorStockistChemistModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("amount"));
                    dcrViewDoctrArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
                recycler_view.setAdapter(dcrViewDoctorRemakeAdapter);
                dcrViewDoctorRemakeAdapter.notifyDataSetChanged();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //-----function to load doctor,chemist,stockist from jsonObject string, code ends-------

    //============function to save data in sqlite after modification, code starts==========
    public void saveData(){

        try {
            final JSONObject DocumentElementobj1 = new JSONObject();
            JSONArray reqDctr = new JSONArray();
            if (!dcrViewDoctrArrayList.isEmpty()) {
                for (int i = 0; i < dcrViewDoctrArrayList.size(); i++) {
//                    if (DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus().contentEquals("1")) {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("name", dcrViewDoctrArrayList.get(i).getName());
                    reqObj.put("id", dcrViewDoctrArrayList.get(i).getId());
                    reqObj.put("work_place_id", dcrViewDoctrArrayList.get(i).getWork_place_id());
                    reqObj.put("ecl_no", dcrViewDoctrArrayList.get(i).getEcl_no());
                    reqObj.put("status", dcrViewDoctrArrayList.get(i).getStatus());
                    reqObj.put("work_place_name", "");
                    reqObj.put("amount", "0.00");
                    reqObj.put("last_visit_date", dcrViewDoctrArrayList.get(i).getLast_visit_date());
                    reqDctr.put(reqObj);
//                    }
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

                Intent intent = new Intent(this,DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                sqliteDb.updateDoctor(DocumentElementobj1.toString(),userSingletonModel.getUser_id(),db1);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    //============function to save data in sqlite after modification, code ends==========

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_ok:
                saveData();
                break;
            case R.id.btn_cancel:
                Intent intent = new Intent(this,DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
