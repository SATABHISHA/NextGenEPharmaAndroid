package org.arb.Nextgen.ePharma.DcrAgainRemake;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrWorkedWithRemakeAdapter;
import org.arb.Nextgen.ePharma.DCR.DcrHome;
import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DcrWorkedWithRemakeActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recycler_view;
    ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers = new ArrayList<>();
    ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayList = new ArrayList<>();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    SQLiteDatabase db1;
    Button btn_apply, btn_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_details_worked_with_remake);

        btn_apply = findViewById(R.id.btn_apply);
        btn_cancel = findViewById(R.id.btn_cancel);
        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======
        //----------creating sqlite database, code starts-------
        try {
            db1 = openOrCreateDatabase("DCRDEtails", MODE_PRIVATE, null);
            db1.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------creating sqlite database, code ends-------

        btn_apply.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        fetchDcrData();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_apply:
//                saveData();
                saveToArrayList();
                Intent intent_wrkd_with = new Intent(this, DcrDetailsRemakeActivity.class);
                intent_wrkd_with.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_wrkd_with);
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

    //=============fetch sqlite data, code starts...============
    public void fetchDcrData(){
        Cursor c = db1.rawQuery("SELECT * FROM dcrdetail", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();

        String json = "";
        while(!c.isAfterLast()){
            json = c.getString(1);
            c.moveToNext();
        }

        Log.d("fetchingSqltData-=-=>",json);
        if(!dcrDetailsListModelArrayList.isEmpty()){
            dcrDetailsListModelArrayList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);

            //----------for manager's section, code starts-----
            JSONArray jsonArray1 = jsonObject.getJSONArray("managers");
            for(int i=0;i<jsonArray1.length();i++){
                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                DcrDetailsListModel dcrDetailsListModel = new DcrDetailsListModel();
                dcrDetailsListModel.setManagers_id(jsonObject1.getString("id"));
                dcrDetailsListModel.setManagers_name(jsonObject1.getString("name"));
                dcrDetailsListModel.setManagers_designation(jsonObject1.getString("designation"));
                dcrDetailsListModel.setManagers_designation_id(jsonObject1.getString("designation_id"));
                dcrDetailsListModel.setStatus("0");
                dcrDetailsListModelArrayListManagers.add(dcrDetailsListModel);
            }
            recycler_view.setAdapter(new DcrWorkedWithRemakeAdapter(DcrWorkedWithRemakeActivity.this, dcrDetailsListModelArrayListManagers));
            //----------for manager's section, code ends-----


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //=============fetch sqlite data, code ends...============

    //============function to save data in the static arraylist for DcrSummary page, code starts==========
    public void saveToArrayList(){
        if(!DcrHome.workedWithArrayListManagersForDcrSummary.isEmpty()){
            DcrHome.workedWithArrayListManagersForDcrSummary.clear();
        }
        if(!DcrWorkedWithRemakeAdapter.dcrDetailsListModelArrayListManagers.isEmpty()) {
            for (int i = 0; i < DcrWorkedWithRemakeAdapter.dcrDetailsListModelArrayListManagers.size(); i++) {
                if (DcrWorkedWithRemakeAdapter.dcrDetailsListModelArrayListManagers.get(i).getStatus().contentEquals("1")) {
                    DcrDetailsListModel dcrDetailsListModel = new DcrDetailsListModel();
                    dcrDetailsListModel.setManagers_id(DcrWorkedWithRemakeAdapter.dcrDetailsListModelArrayListManagers.get(i).getManagers_id());
                    dcrDetailsListModel.setManagers_name(DcrWorkedWithRemakeAdapter.dcrDetailsListModelArrayListManagers.get(i).getManagers_name());
                    DcrHome.workedWithArrayListManagersForDcrSummary.add(dcrDetailsListModel);
                }
            }
        }

    }
    //============function to save data in the static arraylist for DcrSummary page, code ends==========
}
