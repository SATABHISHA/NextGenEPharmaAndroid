package org.arb.Nextgen.ePharma.DcrAgainRemake;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrDetailsWorkedWithViewAdapter;
import org.arb.Nextgen.ePharma.DCR.DcrHome;
import org.arb.Nextgen.ePharma.DCR.DcrSummary;
import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.config.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DcrDetailsRemakeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvMsrName, tvDCRNo, tvHQ, tvDCRDate, tv_dcrtype, tv_wrkd_with, tv_dctr_count, tv_chemist_count, tv_stockist_count;
    public TextView tv_work_place;

    ImageView img_dcr, img_wrkd_with, img_work_place;
    ImageButton imgbtn_view_dctr, imgbtn_add_dctr, imgbtn_view_chemist, imgbtn_add_chemist, imgbtn_view_stockist, imgbtn_add_stockist;
    Button btn_send, btn_draft, btn_cancel;


    SQLiteDatabase db;

    public Context context = this;
    SQLiteDatabase db1, db_masterdata_dctr_stckst_chmst;
    SqliteDb sqliteDb = new SqliteDb();

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

    RecyclerView recycler_view;
    public static boolean draft_y_n;

    LinearLayout ll3, ll_wrkd_with;
    RelativeLayout ll4, ll5, ll6, rl_wrkd_with;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_details_remake);

        tvDCRNo = findViewById(R.id.tvDCRNo);
        tvDCRDate = findViewById(R.id.tvDCRDate);
        tv_dcrtype = findViewById(R.id.tv_dcrtype);
//        tv_wrkd_with = findViewById(R.id.tv_wrkd_with); //--commented on 2nd feb
        tv_dctr_count = findViewById(R.id.tv_dctr_count);
        tv_chemist_count = findViewById(R.id.tv_chemist_count);
        tv_stockist_count = findViewById(R.id.tv_stockist_count);
        tv_work_place = findViewById(R.id.tv_work_place);

        tvMsrName = findViewById(R.id.tvMsrName);
        tvDCRNo = findViewById(R.id.tvDCRNo);
        tvDCRDate = findViewById(R.id.tvDCRDate);
        tvHQ = findViewById(R.id.tvHQ);

        img_dcr = findViewById(R.id.img_dcr);
        img_wrkd_with = findViewById(R.id.img_wrkd_with);
        img_work_place = findViewById(R.id.img_work_place);

        imgbtn_view_dctr = findViewById(R.id.imgbtn_view_dctr);
        imgbtn_add_dctr = findViewById(R.id.imgbtn_add_dctr);
        imgbtn_view_chemist = findViewById(R.id.imgbtn_view_chemist);
        imgbtn_add_chemist = findViewById(R.id.imgbtn_add_chemist);
        imgbtn_view_stockist = findViewById(R.id.imgbtn_view_stockist);
        imgbtn_add_stockist = findViewById(R.id.imgbtn_add_stockist);

        ll3 = findViewById(R.id.ll3);
        ll4 = findViewById(R.id.ll4);
        ll5 = findViewById(R.id.ll5);
        ll6 = findViewById(R.id.ll6);
        rl_wrkd_with = findViewById(R.id.rl_wrkd_with);
        ll_wrkd_with = findViewById(R.id.ll_wrkd_with);

        btn_send = findViewById(R.id.btn_send);
        btn_draft = findViewById(R.id.btn_draft);
        btn_cancel = findViewById(R.id.btn_cancel);

        //----setting value to text, code starts-----
        tvMsrName.setText(userSingletonModel.getUser_full_name());
//        tvDCRDate.setText(userSingletonModel.getSelected_date_calendar());
        tvDCRDate.setText(userSingletonModel.getSelected_date_calendar());
//        tvDCRNo.setText("3216");
        tvDCRNo.setText(userSingletonModel.getDcr_no_for_dcr_summary());
        tvHQ.setText(userSingletonModel.getHq_name());
        //----setting value to text, code ends-----

        img_dcr.setOnClickListener(this);
        img_wrkd_with.setOnClickListener(this);
        imgbtn_view_dctr.setOnClickListener(this);
        imgbtn_add_dctr.setOnClickListener(this);
        imgbtn_view_chemist.setOnClickListener(this);
        imgbtn_add_chemist.setOnClickListener(this);
        imgbtn_view_stockist.setOnClickListener(this);
        img_work_place.setOnClickListener(this);
        imgbtn_add_stockist.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_draft.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


        if(!userSingletonModel.getDcr_details_dcr_type_name().contentEquals("")){
            tv_dcrtype.setText(userSingletonModel.getDcr_details_dcr_type_name());
        }


//        tv_dcrtype.setText(userSingletonModel.getDcr_details_dcr_type_name());
//        tv_work_place.setText(userSingletonModel.getBase_work_place_name());
        Log.d("wrkplace-=>",userSingletonModel.getBase_work_place_name());
        //----------creating sqlite database, code starts-------
        try {
            db = openOrCreateDatabase("DCRDEtails", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");

            db.execSQL("CREATE TABLE IF NOT EXISTS TB_CUSTOMER(id integer PRIMARY KEY AUTOINCREMENT, dctr_chemist_stockist_id VARCHAR, ecl_no VARCHAR, name VARCHAR, work_place_id VARCHAR, work_place_name VARCHAR, speciality VARCHAR, customer_class VARCHAR, geo_tagged_yn integer, latitude VARCHAR, longitude VARCHAR, location_address VARCHAR, type VARCHAR, synced_yn integer)"); //--added on 17th march as per requirement
            if(sqliteDb.countMasterData(db) > 0){
                sqliteDb.deleteMasterData(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------creating sqlite database, code ends-------

        //---------initializing sqlitedatabase to get count of dctr/stockist/chemist, code starts---
        try {
            db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase to get count of dctr/stockist/chemist, code ends---


        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        loadDcrData();

        load_wrkd_with_data();


        if(!userSingletonModel.getBase_work_place_name().trim().contentEquals("")){
            tv_work_place.setText(userSingletonModel.getBase_work_place_name());
        }/*else{
            tv_work_place.setText("");
        }*/
        tv_dctr_count.setText(String.valueOf(sqliteDb.count(6,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1)));
        tv_chemist_count.setText(String.valueOf(sqliteDb.count(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1)));
        tv_stockist_count.setText(String.valueOf(sqliteDb.count(8,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1)));

        if (sqliteDb.count(6,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0){
            imgbtn_view_dctr.setVisibility(View.GONE);
        }
        if (sqliteDb.count(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0){
            imgbtn_view_chemist.setVisibility(View.GONE);
        }
        if (sqliteDb.count(8,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0){
            imgbtn_view_stockist.setVisibility(View.GONE);
        }


        //--added on 2nd feb as per requirement, code starts
        if(!userSingletonModel.getDcr_details_dcr_type_name().contentEquals("Field Work")){

            ll3.setVisibility(View.GONE);
            ll4.setVisibility(View.GONE);
            ll5.setVisibility(View.GONE);
            ll6.setVisibility(View.GONE);
            rl_wrkd_with.setVisibility(View.GONE);
            recycler_view.setVisibility(View.GONE);
            ll_wrkd_with.setVisibility(View.GONE);

        }
        //--added on 2nd feb as per requirement, code ends
    }

    public void load_wrkd_with_data(){
        if(!DcrHome.workedWithArrayListManagersForDcrSummary.isEmpty()){
            recycler_view.setAdapter(new DcrDetailsWorkedWithViewAdapter(DcrDetailsRemakeActivity.this, DcrHome.workedWithArrayListManagersForDcrSummary));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                //---------Alert dialog code starts(added on 21st nov)--------
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Unsaved data will be lost. Do you want to continue?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
//                                DcrSummary.removeData();
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
                                Intent intentCancel = new Intent(DcrDetailsRemakeActivity.this, DcrHome.class);
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
            case R.id.img_dcr:
                Intent intent_apply = new Intent(this, DcrTypeRemakeActivity.class);
                intent_apply.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_apply);
                break;
            case R.id.img_wrkd_with:
                Intent intent_wrkd_with = new Intent(this, DcrWorkedWithRemakeActivity.class);
                intent_wrkd_with.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_wrkd_with);
                break;
            case R.id.img_work_place:
                Intent intent_work_place = new Intent(this, DcrSelectBaseWorkPlaceRemakeActivity.class);
                intent_work_place.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_work_place);
                break;
            case R.id.imgbtn_view_dctr:
                Intent intent_view_dctr = new Intent(this, DcrViewDoctorRemake.class);
                intent_view_dctr.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_view_dctr);
                break;
            case R.id.imgbtn_add_dctr:
                Intent intent_add_dctr = new Intent(this, DcrSelectDoctorRemake.class);
                intent_add_dctr.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_add_dctr);
                break;
            case R.id.imgbtn_view_stockist:
                Intent intent_view_stockist = new Intent(this, DcrViewStockistRemakeActivity.class);
                intent_view_stockist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_view_stockist);
                break;
            case R.id.imgbtn_add_stockist:
                Intent intent_add_stockist = new Intent(this, DcrSelectStockistRemakeActivity.class);
                intent_add_stockist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_add_stockist);
                break;
            case R.id.imgbtn_add_chemist:
                Intent intent_add_chemist = new Intent(this, DcrSelectChemistRemakeActivity.class);
                intent_add_chemist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_add_chemist);
                break;
            case R.id.imgbtn_view_chemist:
                Intent intent_view_chemist = new Intent(this, DcrViewChemistRemake.class);
                intent_view_chemist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_view_chemist);
                break;
            case R.id.btn_send:

                if (sqliteDb.count(6,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0 &&
                        sqliteDb.count(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0 &&
                        sqliteDb.count(8,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0){
//--            -------Alert dialog code starts--------
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(DcrDetailsRemakeActivity.this);

                    String message = "Cannot save DCR without at least one Doctor or Chemist or Stockist when DCR Type is Field Work.";


//                                            alertDialogBuilder.setMessage(resobj.getString("message")); //--commented on 2nd feb
                    alertDialogBuilder1.setMessage(message); //--added on 2nd feb
                    alertDialogBuilder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    arg0.cancel();
                                }
                            });
                    AlertDialog alertDialog1 = alertDialogBuilder1.create();
                    alertDialog1.show();


                    //--------Alert dialog code ends--------
                }else {
                    draft_y_n = false;
                    DcrHome.check_draft_yn_for_summary = "N";
                    Intent intent_dcrsummary = new Intent(this, DcrSummary.class);
                    intent_dcrsummary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent_dcrsummary);
                }
                break;
            case R.id.btn_draft:
                if (sqliteDb.count(6,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0 &&
                        sqliteDb.count(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0 &&
                        sqliteDb.count(8,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1) == 0){
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(DcrDetailsRemakeActivity.this);

                    String message = "Cannot save DCR without at least one Doctor or Chemist or Stockist when DCR Type is Field Work.";


//                                            alertDialogBuilder.setMessage(resobj.getString("message")); //--commented on 2nd feb
                    alertDialogBuilder1.setMessage(message); //--added on 2nd feb
                    alertDialogBuilder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    arg0.cancel();
                                }
                            });
                    AlertDialog alertDialog1 = alertDialogBuilder1.create();
                    alertDialog1.show();



                }else {
                    draft_y_n = true;
                    DcrHome.check_draft_yn_for_summary = "Y";
                    Intent intent_dcr_summary_draft = new Intent(this, DcrSummary.class);
                    intent_dcr_summary_draft.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent_dcr_summary_draft);
                }
                break;

        }
    }

    //=============function for DCR Details from api and insert data into sqlite database, code starts...=============
    public void loadDcrData(){
//        String url = Config.BaseUrlEpharma + "msr/dcr-master-data/" + userSingletonModel.getUser_id() + "/7" ;
        String url = Config.BaseUrlEpharma + "epharma/MSR/DCR-Master-Data/" + userSingletonModel.getCorp_id() +"/"+userSingletonModel.getUser_id() + "/" + userSingletonModel.getCalendar_id() ;

        Log.d("dcrurl-=>",url);
        final ProgressDialog loading = ProgressDialog.show(DcrDetailsRemakeActivity.this, "Loading", "Please wait...", true, false);
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
            ContentValues values = new ContentValues();
            values.put("dcrJsonData", jsonObject.toString());
            if ((db.insert("dcrdetail", null, values)) != -1) {
//                Toast.makeText(getApplicationContext(), "Inserted...", Toast.LENGTH_LONG).show();
                Log.d("Test DCRDAta", "Data inserted");
//                fetchDcrData(); //---calling function to load data from sqlite in spinner
            } else {
                Toast.makeText(getApplicationContext(),"Error...",Toast.LENGTH_LONG).show();
                Log.d("Test DCRData", "Data not inserted");
            }

            JSONArray jsonArrayDoctor = jsonObject.getJSONArray("doctors");
            for(int i=0; i<jsonArrayDoctor.length(); i++){
                JSONObject jsonObject1 = jsonArrayDoctor.getJSONObject(i);
                setContentValues_SaveData(jsonObject1,"doctors");
            }

            JSONArray jsonArrayChemist = jsonObject.getJSONArray("chemists");
            for(int i=0; i<jsonArrayChemist.length(); i++){
                JSONObject jsonObject1 = jsonArrayChemist.getJSONObject(i);
                setContentValues_SaveData(jsonObject1,"chemists");
            }

            JSONArray jsonArrayStockist = jsonObject.getJSONArray("stockists");
            for(int i=0; i<jsonArrayStockist.length(); i++){
                JSONObject jsonObject1 = jsonArrayStockist.getJSONObject(i);
                setContentValues_SaveData(jsonObject1,"stockists");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //=============function for DCR Details from api and insert data into sqlite database, code ends...===============

    //---function to save the data in sqlitedatabse as masterdata, code starts
    public void setContentValues_SaveData(JSONObject jsonObject1, String type){
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put("dctr_chemist_stockist_id",jsonObject1.getString("id"));
            contentValues.put("ecl_no",jsonObject1.getString("ecl_no"));
            contentValues.put("name",jsonObject1.getString("name"));
            contentValues.put("work_place_id",jsonObject1.getString("work_place_id"));
            contentValues.put("work_place_name",jsonObject1.getString("work_place_name"));
            if(type.contentEquals("doctors")) {
                contentValues.put("speciality", jsonObject1.getString("speciality"));
                contentValues.put("customer_class",jsonObject1.getString("customer_class"));
            }else{
                contentValues.put("speciality", "NA");
                contentValues.put("customer_class","NA");
            }

            contentValues.put("geo_tagged_yn",jsonObject1.getInt("geo_tagged_yn"));
            contentValues.put("latitude",jsonObject1.getString("latitude"));
            contentValues.put("longitude",jsonObject1.getString("longitude"));
            contentValues.put("location_address",jsonObject1.getString("location_address"));
            contentValues.put("type",type);
//                contentValues.put("synced_yn",jsonObject1.getInt("synced_yn"));
            contentValues.put("synced_yn",1);

            if ((db.insert("TB_CUSTOMER", null, contentValues)) != -1) {
//                Toast.makeText(getApplicationContext(), "Inserted...", Toast.LENGTH_LONG).show();
                Log.d("dctr DCRDAta", "Data inserted");
//                fetchDcrData(); //---calling function to load data from sqlite in spinner
            } else {
                Toast.makeText(getApplicationContext(),"Error...",Toast.LENGTH_LONG).show();
                Log.d("Test DCRData", "Data not inserted");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //---function to save the data in sqlitedatabse as masterdata, code ends


}
