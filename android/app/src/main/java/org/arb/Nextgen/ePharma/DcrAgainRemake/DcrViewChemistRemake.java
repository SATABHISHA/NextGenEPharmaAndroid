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

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrViewChemistRemakeAdapter;
import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DcrViewChemistRemake extends AppCompatActivity implements View.OnClickListener {
    public ArrayList<DcrSelectDoctorStockistChemistModel> dcrViewChemistArrayList = new ArrayList<>();
    SQLiteDatabase db1;
    SqliteDb sqliteDb = new SqliteDb();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    RecyclerView recycler_view;
    public static DcrViewChemistRemakeAdapter dcrViewChemistRemakeAdapter;
    Button btn_ok, btn_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_view_chemist_remake);

        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        dcrViewChemistRemakeAdapter = new DcrViewChemistRemakeAdapter(this,dcrViewChemistArrayList);
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

        loadChemist();

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //-----function to load doctor,chemist,stockist from jsonObject string, code starts-------
    public void loadChemist(){

        //---chemist
        String jsonChemist = "";
        jsonChemist = sqliteDb.fetch(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
        try {
            if(!dcrViewChemistArrayList.isEmpty()){
                dcrViewChemistArrayList.clear();
            }

            if(jsonChemist!=null) {
                Log.d("sqlitejsontest-=>",jsonChemist);
                JSONObject jsonObject = new JSONObject(jsonChemist);

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
                    dcrViewChemistArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
                recycler_view.setAdapter(dcrViewChemistRemakeAdapter);
                dcrViewChemistRemakeAdapter.notifyDataSetChanged();
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
            if (!dcrViewChemistArrayList.isEmpty()) {
                for (int i = 0; i < dcrViewChemistArrayList.size(); i++) {
//                    if (DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.get(i).getStatus().contentEquals("1")) {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("name", dcrViewChemistArrayList.get(i).getName());
                    reqObj.put("id", dcrViewChemistArrayList.get(i).getId());
                    reqObj.put("work_place_id", dcrViewChemistArrayList.get(i).getWork_place_id());
                    reqObj.put("ecl_no", dcrViewChemistArrayList.get(i).getEcl_no());
                    reqObj.put("status", dcrViewChemistArrayList.get(i).getStatus());
                    reqObj.put("work_place_name", "");
                    reqObj.put("amount", "0.00");
                    reqObj.put("last_visit_date", dcrViewChemistArrayList.get(i).getLast_visit_date());
                    reqDctr.put(reqObj);
//                    }
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
                sqliteDb.updateDCR("Chemist",DocumentElementobj1.toString(),userSingletonModel.getUser_id(), userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);

                Intent intent = new Intent(this,DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                sqliteDb.updateDoctor(DocumentElementobj1.toString(),userSingletonModel.getUser_id(),db1);
            }
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
