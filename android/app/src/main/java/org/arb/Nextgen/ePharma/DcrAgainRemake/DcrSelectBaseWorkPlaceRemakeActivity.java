package org.arb.Nextgen.ePharma.DcrAgainRemake;

import android.content.Context;
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

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrBaseWorkPlaceRemakeAdapter;
import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DcrSelectBaseWorkPlaceRemakeActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayList = new ArrayList<>();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    RecyclerView recycler_view;
    public static DcrBaseWorkPlaceRemakeAdapter dcrBaseWorkPlaceRemakeAdapter;
    public static String dcr_id="", dcr_type="";
    Button btn_apply, btn_cancel;

    public static String base_wrk_place_name = "", base_wrk_place_id = "";

    SQLiteDatabase db;

    public Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_base_work_place_remake);

        btn_apply = findViewById(R.id.btn_apply);
        btn_cancel = findViewById(R.id.btn_cancel);
        dcrBaseWorkPlaceRemakeAdapter = new DcrBaseWorkPlaceRemakeAdapter(this,dcrDetailsListModelArrayList);

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======



        //----------creating sqlite database, code starts-------
        try {
            db = openOrCreateDatabase("DCRDEtails", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------creating sqlite database, code ends-------

        fetchDcrData();

        btn_apply.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }
    //=============fetch sqlite data, code starts...============
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
        if(!dcrDetailsListModelArrayList.isEmpty()){
            dcrDetailsListModelArrayList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);

            //--------for work_place, code starts-----
            JSONArray jsonArray = jsonObject.getJSONArray("work_place");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                DcrDetailsListModel dcrDetailsListModel = new DcrDetailsListModel();
                dcrDetailsListModel.setId(jsonObject1.getString("id"));
                dcrDetailsListModel.setName(jsonObject1.getString("name"));
                dcrDetailsListModel.setHq_id(jsonObject1.getString("hq_id"));
                dcrDetailsListModel.setHq_name(jsonObject1.getString("hq_name"));
                dcrDetailsListModelArrayList.add(dcrDetailsListModel);
            }
            recycler_view.setAdapter(new DcrBaseWorkPlaceRemakeAdapter(this, dcrDetailsListModelArrayList));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //=============fetch sqlite data, code ends...============
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_apply:


                Intent intent = new Intent(this,DcrDetailsRemakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                userSingletonModel.setBase_work_place_id(base_wrk_place_id);
                userSingletonModel.setBase_work_place_name(base_wrk_place_name);

                Log.d("dcr_id-=>",base_wrk_place_id);
                Log.d("dcr_type-=>",base_wrk_place_name);
                break;
            case R.id.btn_cancel:
                Intent intent_cancel = new Intent(this,DcrDetailsRemakeActivity.class);
                intent_cancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_cancel);
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
