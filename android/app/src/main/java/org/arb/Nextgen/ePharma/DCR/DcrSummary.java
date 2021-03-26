package org.arb.Nextgen.ePharma.DCR;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.DcrAgainRemake.DcrDetailsRemakeActivity;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.Model.DcrSummaryContentModel;
import org.arb.Nextgen.ePharma.Model.DcrSummaryGroupNameModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.DcrSelectChemistAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrSelectDoctorAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrSelectStockistAdapter;
import org.arb.Nextgen.ePharma.adapter.ExpandableListDcrSummaryAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.ConnectivityReceiver;
import org.arb.Nextgen.ePharma.config.MyApplication;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DcrSummary extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    View rootView;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    RelativeLayout rl_back_arrow;
    ImageButton imgbtn_arrrow;
    Button btn_save, btn_edit, btn_cancel, btn_back;

    ArrayList<DcrSummaryGroupNameModel> dcrSummaryGroupNameModelsArrayList = new ArrayList<>();
    ArrayList<DcrSummaryContentModel> dcrSummaryContentModelArrayList = new ArrayList<>();
    ArrayList<DcrSummaryContentModel> dcrSummaryContentModelArrayListForJsonObject = new ArrayList<>();


    public ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorArrayList = new ArrayList<>();
    public ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectChemistArrayList = new ArrayList<>();
    public ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectStockistArrayList = new ArrayList<>();

    HashMap<DcrSummaryGroupNameModel, ArrayList<DcrSummaryContentModel>> listDataChild = new HashMap<DcrSummaryGroupNameModel, ArrayList<DcrSummaryContentModel>>();
    public String jsonString="";  //---disabling static
    SQLiteDatabase db1;
    SqliteDb sqliteDb = new SqliteDb();

    TextView tvHq, tvWorkPlace, tvDcrType, tvWorkedWith, tvWorkedWithCaption, tvWorkPlaceCaption, tv_date, tv_dcr_no, tv_status, tv_required, tvWarningMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_summary);


        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts---
        try {
            db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends---


        loadDoctorChemistStockistJsonDataDetails(); //---calling function to load json data and save to arraylist for doctor,chemist and stockist
       /* rl_back_arrow = findViewById(R.id.rl_back_arrow);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);*/
        btn_save = findViewById(R.id.btn_save);
        tvHq = findViewById(R.id.tvHq);
        tvWorkPlace = findViewById(R.id.tvWorkPlace);
        tvDcrType = findViewById(R.id.tvDcrType);
        tvWorkedWith = findViewById(R.id.tvWorkedWith);
        tvWorkedWithCaption = findViewById(R.id.tvWorkedWithCaption);
        tvWorkPlaceCaption = findViewById(R.id.tvWorkPlaceCaption);
        tvWarningMessage = findViewById(R.id.tvWarningMessage);
//        btn_edit = findViewById(R.id.btn_edit);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_back = findViewById(R.id.btn_back);
        tv_date = findViewById(R.id.tv_date);
        tv_dcr_no = findViewById(R.id.tv_dcr_no);
        tv_status = findViewById(R.id.tv_status);
        tv_required = findViewById(R.id.tv_required);

        if(DcrHome.check_draft_yn_for_summary.contentEquals("Y")){
            tv_status.setText("Status: Draft");
        }else {
            tv_status.setText("Status: " + userSingletonModel.getApproval_status_name());
        }
        tv_dcr_no.setText("DCR No: "+userSingletonModel.getDcr_no_for_dcr_summary());
        tv_date.setText(userSingletonModel.getSelected_date_calendar());

        /*rl_back_arrow.setOnClickListener(this);
        imgbtn_arrrow.setOnClickListener(this);*/
        btn_save.setOnClickListener(this);
//        btn_edit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        //---disabling/enabling edit and save button
        if(DcrHome.check_draft_yn_for_summary.contentEquals("Y")){
            /*btn_back.setEnabled(false);
            btn_back.setClickable(false);
            btn_back.setAlpha(0.5f);

            tvWarningMessage.setVisibility(View.GONE);*/ //commented on 2nd feb

            //--added on 2nd feb, starts
            btn_back.setEnabled(true);
            btn_back.setClickable(true);
            btn_back.setAlpha(1.0f);

            btn_save.setEnabled(true);
            btn_save.setClickable(true);
            btn_save.setAlpha(1);
            //--added on 2nd feb, ends

        }else {
            if (DcrHome.dcrStatus == 1) {
                btn_save.setEnabled(false);
                btn_save.setClickable(false);
                btn_save.setAlpha(0.5f);

            /*btn_edit.setEnabled(false);
            btn_edit.setClickable(false);
            btn_edit.setAlpha(0.5f);*/
            } else if (DcrHome.dcrStatus == 0) {
                if (userSingletonModel.getDcr_details_dcr_type_id().contentEquals("0") &&
                        (dcrSelectDoctorArrayList.isEmpty() &&
                                dcrSelectChemistArrayList.isEmpty() &&
                                dcrSelectStockistArrayList.isEmpty())) {
                    btn_save.setEnabled(false);
                    btn_save.setClickable(false);
                    btn_save.setAlpha(0.5f);

                    tvWarningMessage.setVisibility(View.VISIBLE);

               /* btn_edit.setEnabled(false);
                btn_edit.setClickable(false);
                btn_edit.setAlpha(0.5f);*/
                } else {
                    btn_save.setEnabled(true);
                    btn_save.setClickable(true);
                    btn_save.setAlpha(1);

                    tvWarningMessage.setVisibility(View.GONE);
               /* btn_edit.setEnabled(true);
                btn_edit.setClickable(true);
                btn_edit.setAlpha(1);*/
                }
            }
        }


        //----settin values to textview with sqlite value/normal value
        //--commented on 2nd feb
       /* if(DcrHome.check_draft_yn_for_summary.contentEquals("Y")){
            String json_string = sqliteDb.fetch(11, userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);
            try{
                JSONObject jsonObject = new JSONObject(json_string);
                tvHq.setText(jsonObject.getString("HqName"));
                tvDcrType.setText(jsonObject.getString("DcrType"));

                if (jsonObject.getString("workPlace").contentEquals("")) {
                    tvWorkPlace.setVisibility(View.GONE);
                    tvWorkPlaceCaption.setVisibility(View.GONE);
                } else {
                    tvWorkPlace.setVisibility(View.VISIBLE);
                    tvWorkPlaceCaption.setVisibility(View.VISIBLE);
                    tvWorkPlace.setText(jsonObject.getString("workPlace"));
                }

                if(jsonObject.getString("workedWithName").contentEquals("")){
                    tvWorkedWithCaption.setVisibility(View.GONE);
                    tvWorkedWith.setVisibility(View.GONE);
                }else{
                    tvWorkedWithCaption.setVisibility(View.VISIBLE);
                    tvWorkedWith.setVisibility(View.VISIBLE);
                    tvWorkedWith.setText(jsonObject.getString("workedWithName"));
                }

                tvWorkedWith.setText(jsonObject.getString("workedWithName"));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else {
            tvHq.setText(userSingletonModel.getHq_name());
            tvWorkPlace.setText(userSingletonModel.getBase_work_place_name());
            tvDcrType.setText(userSingletonModel.getDcr_details_dcr_type_name());
            String workedWithName = "", workPlace = "";

            if (DcrHome.tempForDcrSummaryBack == 0) {
                if (DcrHome.workedWithArrayListManagersForDcrSummary.size() > 0) {
                    for (int i = 0; i < DcrHome.workedWithArrayListManagersForDcrSummary.size(); i++) {
                        workedWithName = workedWithName + DcrHome.workedWithArrayListManagersForDcrSummary.get(i).getManagers_name() + "," + "\n";
                        JSONObject reqObj = new JSONObject();

                    }
                    workedWithName = workedWithName.substring(0, workedWithName.length() - 2);
                    Log.d("Names-=-=>", workedWithName);
                    tvWorkedWith.setText(workedWithName);
                    tvWorkedWithCaption.setVisibility(View.VISIBLE);
                    tvWorkedWith.setVisibility(View.VISIBLE);
                } else {
                    tvWorkedWithCaption.setVisibility(View.GONE);
                    tvWorkedWith.setVisibility(View.GONE);
                }
            } else {
            *//*tvWorkedWithCaption.setVisibility(View.GONE);
            tvWorkedWith.setVisibility(View.GONE);*//*
                if (DcrHome.workedWithArrayListManagersForDcrSummary.size() > 0) {
                    for (int i = 0; i < DcrHome.workedWithArrayListManagersForDcrSummary.size(); i++) {
                        workedWithName = workedWithName + DcrHome.workedWithArrayListManagersForDcrSummary.get(i).getManagers_name() + "," + "\n";
                        JSONObject reqObj = new JSONObject();

                    }
                    workedWithName = workedWithName.substring(0, workedWithName.length() - 2);
                    Log.d("Names-=-=>", workedWithName);
                    tvWorkedWith.setText(workedWithName);
                    tvWorkedWithCaption.setVisibility(View.VISIBLE);
                    tvWorkedWith.setVisibility(View.VISIBLE);
                } else {
                    tvWorkedWithCaption.setVisibility(View.GONE);
                    tvWorkedWith.setVisibility(View.GONE);
                }
            }

            if (userSingletonModel.getBase_work_place_name().contentEquals("")) {
                tvWorkPlace.setVisibility(View.GONE);
                tvWorkPlaceCaption.setVisibility(View.GONE);
            } else {
                tvWorkPlace.setVisibility(View.VISIBLE);
                tvWorkPlaceCaption.setVisibility(View.VISIBLE);
                tvWorkPlace.setText(userSingletonModel.getBase_work_place_name());
                workPlace = userSingletonModel.getBase_work_place_name();
            }

            //---making jsonObject for misc column, code starts---
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("HqName", userSingletonModel.getHq_name());
                jsonObject.put("DcrType", userSingletonModel.getDcr_details_dcr_type_name());
                jsonObject.put("workedWithName", workedWithName);
                jsonObject.put("workPlace", userSingletonModel.getBase_work_place_name());
                sqliteDb.updateDCR("Misc",jsonObject.toString(), userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);
                Log.d("Misctest-=>", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }*/ //--commented on 2nd feb
        //---making jsonObject for misc column, code ends---
        //----settin values to textview with sqlite value/normal value, code ends
        //--added on 2nd feb, starts
 tvHq.setText(userSingletonModel.getHq_name());
            tvWorkPlace.setText(userSingletonModel.getBase_work_place_name());
            tvDcrType.setText(userSingletonModel.getDcr_details_dcr_type_name());
    String workedWithName = "", workPlace = "";

            if (DcrHome.tempForDcrSummaryBack == 0) {
        if (DcrHome.workedWithArrayListManagersForDcrSummary.size() > 0) {
            for (int i = 0; i < DcrHome.workedWithArrayListManagersForDcrSummary.size(); i++) {
                workedWithName = workedWithName + DcrHome.workedWithArrayListManagersForDcrSummary.get(i).getManagers_name() + "," + "\n";
                JSONObject reqObj = new JSONObject();

            }
            workedWithName = workedWithName.substring(0, workedWithName.length() - 2);
            Log.d("Names-=-=>", workedWithName);
            tvWorkedWith.setText(workedWithName);
            tvWorkedWithCaption.setVisibility(View.VISIBLE);
            tvWorkedWith.setVisibility(View.VISIBLE);
        } else {
            tvWorkedWithCaption.setVisibility(View.GONE);
            tvWorkedWith.setVisibility(View.GONE);
        }
    } else {
            /*tvWorkedWithCaption.setVisibility(View.GONE);
            tvWorkedWith.setVisibility(View.GONE);*/
        if (DcrHome.workedWithArrayListManagersForDcrSummary.size() > 0) {
            for (int i = 0; i < DcrHome.workedWithArrayListManagersForDcrSummary.size(); i++) {
                workedWithName = workedWithName + DcrHome.workedWithArrayListManagersForDcrSummary.get(i).getManagers_name() + "," + "\n";
                JSONObject reqObj = new JSONObject();

            }
            workedWithName = workedWithName.substring(0, workedWithName.length() - 2);
            Log.d("Names-=-=>", workedWithName);
            tvWorkedWith.setText(workedWithName);
            tvWorkedWithCaption.setVisibility(View.VISIBLE);
            tvWorkedWith.setVisibility(View.VISIBLE);
        } else {
            tvWorkedWithCaption.setVisibility(View.GONE);
            tvWorkedWith.setVisibility(View.GONE);
        }
    }

            if (userSingletonModel.getBase_work_place_name().contentEquals("")) {
        tvWorkPlace.setVisibility(View.GONE);
        tvWorkPlaceCaption.setVisibility(View.GONE);
    } else {
        tvWorkPlace.setVisibility(View.VISIBLE);
        tvWorkPlaceCaption.setVisibility(View.VISIBLE);
        tvWorkPlace.setText(userSingletonModel.getBase_work_place_name());
        workPlace = userSingletonModel.getBase_work_place_name();
    }

    //---making jsonObject for misc column, code starts---
            try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("HqName", userSingletonModel.getHq_name());
        jsonObject.put("DcrType", userSingletonModel.getDcr_details_dcr_type_name());
        jsonObject.put("workedWithName", workedWithName);
        jsonObject.put("workPlace", userSingletonModel.getBase_work_place_name());
        sqliteDb.updateDCR("Misc",jsonObject.toString(), userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);
        Log.d("Misctest-=>", jsonObject.toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }
        //--added on 2nd feb, ends

        makeJsonObject(); //--calling function to make jsonObject
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //---for testing, starts
        if(dcrSelectDoctorArrayList.size()>0) {
            for (int i = 0; i < dcrSelectDoctorArrayList.size(); i++) {
                Log.d("Name test-=-=>",dcrSelectDoctorArrayList.get(i).getName());
            }
        }
        //---for testing, ends

        //--added on 2nd feb, starts--
        if(DcrDetailsRemakeActivity.draft_y_n == true){
            btn_save.setText("Save As \nDraft");
        }else if(DcrDetailsRemakeActivity.draft_y_n == false){
            btn_save.setText("Send");
        }
        //--added on 2nd feb, ends--

    }


    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //--------commented on 15th July
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
    //-----function to load doctor,chemist,stockist from jsonObject string, code starts-------
    public void loadDoctorChemistStockistJsonDataDetails(){

        //---doctor
        String jsonDoctor = "";
        jsonDoctor = sqliteDb.fetch(6,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
        try {
            if(!dcrSelectDoctorArrayList.isEmpty()){
                dcrSelectDoctorArrayList.clear();
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
                    dcrSelectDoctorArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
            }

            //----chemist
            String jsonChemist = "";
            jsonChemist = sqliteDb.fetch(7,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
            if(!dcrSelectChemistArrayList.isEmpty()){
                dcrSelectChemistArrayList.clear();
            }

            if(jsonChemist!=null) {
                Log.d("sqlitejsonChemist-=>",jsonChemist);
                JSONObject jsonObjectChemist = new JSONObject(jsonChemist);

                JSONArray jsonArrayChemist = jsonObjectChemist.getJSONArray("values");
                for (int i = 0; i < jsonArrayChemist.length(); i++) {
                    JSONObject jsonObject1 = jsonArrayChemist.getJSONObject(i);
                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                    dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
//                dcrSelectDoctorStockistChemistModel.setStatus(jsonObject1.getString("status"));
                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("work_place_id"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                    dcrSelectDoctorStockistChemistModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("amount"));
                    dcrSelectChemistArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
            }
            //----stockist
            String jsonStockist = "";
            jsonStockist = sqliteDb.fetch(8,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
            if(!dcrSelectStockistArrayList.isEmpty()){
                dcrSelectStockistArrayList.clear();
            }

            if(jsonStockist != null) {
                Log.d("sqlitejsonStockist-=>",jsonStockist);
                JSONObject jsonObjectStockist = new JSONObject(jsonStockist);

                JSONArray jsonArrayStockist = jsonObjectStockist.getJSONArray("values");
                for (int i = 0; i < jsonArrayStockist.length(); i++) {
                    JSONObject jsonObject1 = jsonArrayStockist.getJSONObject(i);
                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                    dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
//                dcrSelectDoctorStockistChemistModel.setStatus(jsonObject1.getString("status"));
                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("work_place_id"));
                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                    dcrSelectDoctorStockistChemistModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("amount"));
                    dcrSelectStockistArrayList.add(dcrSelectDoctorStockistChemistModel);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //-----function to load doctor,chemist,stockist from jsonObject string, code ends-------


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.rl_back_arrow:
                if(DcrHome.tempForDcrSummaryBack == 0) {
                    if (DcrDetails.tempForDcrType == 1) {
                    *//*Intent intent1 = new Intent(DcrSummary.this, DcrSelectStockist.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);*//*
                    finish();
                    startActivity(new Intent(DcrSummary.this, DcrSelectStockist.class));
                    } else if (DcrDetails.tempForDcrType == 0) {
                    *//*Intent intent1 = new Intent(DcrSummary.this, DcrDetails.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);*//*
                    removeData();
                    startActivity(new Intent(DcrSummary.this, DcrDetails.class));
                    finish();
                    }
                }else{
                    removeData();
                    startActivity(new Intent(DcrSummary.this, DcrHome.class));
                    finish();
                }
                break;
            case R.id.imgbtn_arrrow:
                if(DcrHome.tempForDcrSummaryBack == 0) {
                    if (DcrDetails.tempForDcrType == 1) {
                    *//*Intent intent1 = new Intent(DcrSummary.this, DcrSelectStockist.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);*//*
                    finish();
                    startActivity(new Intent(DcrSummary.this, DcrSelectStockist.class));
                    } else if (DcrDetails.tempForDcrType == 0) {
                    *//*Intent intent1 = new Intent(DcrSummary.this, DcrDetails.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);*//*
                    removeData();
                    startActivity(new Intent(DcrSummary.this, DcrDetails.class));
                    finish();
                    }
                }else{
                    removeData();
                    startActivity(new Intent(DcrSummary.this, DcrHome.class));
                    finish();
                }
                break;*/
            case R.id.btn_back:
              /*  if(DcrHome.tempForDcrSummaryBack == 0) {
                    *//*if (DcrDetails.tempForDcrType == 1) {
                    *//**//*Intent intent1 = new Intent(DcrSummary.this, DcrSelectStockist.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);*//**//*
                        finish();
                        startActivity(new Intent(DcrSummary.this, DcrSelectStockist.class));
                    } else if (DcrDetails.tempForDcrType == 0) {
                    *//**//*Intent intent1 = new Intent(DcrSummary.this, DcrDetails.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);*//**//*
                        removeData();
                        startActivity(new Intent(DcrSummary.this, DcrDetails.class));
                        finish();
                    }*//* //commented on 2nd feb
                    startActivity(new Intent(DcrSummary.this, DcrDetailsRemakeActivity.class)); //added on 2nd feb
                }else{
                    removeData();
                    startActivity(new Intent(DcrSummary.this, DcrHome.class));
                    finish();
                }*/ //--commented on 3rd feb


                //---added on 3rd feb, starts
                if(DcrHome.dcrStatus == 0){
                    startActivity(new Intent(DcrSummary.this, DcrDetailsRemakeActivity.class)); //added on 2nd feb
                }else if (DcrHome.dcrStatus == 1){
                    removeData();
                    startActivity(new Intent(DcrSummary.this, DcrHome.class));
                    finish();
                }else{
                    startActivity(new Intent(DcrSummary.this, DcrDetailsRemakeActivity.class)); //added on 2nd feb
                }
                //---added on 3rd feb, ends
                break;
            /*case R.id.btn_edit:
                startActivity(new Intent(DcrSummary.this, DcrHome.class));
                break;*/
            case R.id.btn_cancel:

                if(DcrHome.check_draft_yn_for_summary.contentEquals("Y")){
                   /* startActivity(new Intent(DcrSummary.this, DcrHome.class));
                    finish();*/ //--commented on 3rd feb

                    //---added on 3rd feb, code starts
                    //---------Alert dialog code starts(added on 21st nov)--------
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DcrSummary.this);
                    alertDialogBuilder.setMessage("Unsaved data will be lost. Do you want to continue?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    removeData();
//                                    jsonString = "";
//                                    HomeActivity.cancelStatus = 1;
                                    startActivity(new Intent(DcrSummary.this, DcrHome.class));
                                    finish();

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
                    //---added on 3rd feb, code ends

                }else{
                    if(DcrHome.dcrStatus == 1){
                        removeData();
                        startActivity(new Intent(DcrSummary.this, DcrHome.class));
                        this.finish();
                    /*Intent intentCancel = new Intent(DcrSummary.this, DcrHome.class);
                    intentCancel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentCancel);*/
                    }else {
                        //---------Alert dialog code starts(added on 21st nov)--------
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DcrSummary.this);
                        alertDialogBuilder.setMessage("Unsaved data will be lost. Do you want to continue?");
                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        removeData();
//                                    jsonString = "";
//                                    HomeActivity.cancelStatus = 1;
                                        HomeActivity.cancelStatus = 0;
                                        startActivity(new Intent(DcrSummary.this, DcrHome.class));
                                        finish();
                                    /*jsonString = "";
                                    Intent intentCancel = new Intent(DcrSummary.this, DcrHome.class);
//                                    intentCancel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intentCancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intentCancel);
                                    DcrSummary.this.finish();

                                    *//*startActivity(new Intent(DcrSummary.this, DcrHome.class));
                                    DcrSummary.this.finish();*/
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
                    }
                }

                break;
            case R.id.btn_save:
//                saveData();
                LayoutInflater li = LayoutInflater.from(this);
                View dialog = li.inflate(R.layout.dialog_dcr_remarks, null);
//                    dialog.setBackgroundResource(android.R.color.transparent);
                TextView text = (TextView) dialog.findViewById(R.id.tv_accountcode);
                final TextView tv_required = (TextView) dialog.findViewById(R.id.tv_required);
                final EditText editText = (EditText) dialog.findViewById(R.id.ed_note);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_sup_cancel);
                Button btn_save = (Button) dialog.findViewById(R.id.btn_sup_save);
//                ImageButton imgbtn_close = (ImageButton) dialog.findViewById(R.id.imgbtn_close);

                //---fetching remarks from sqlite database, if available(for remarks)
                String final_json_from_sqlite = sqliteDb.fetch(9,userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);

                if(final_json_from_sqlite != null){
                    try{
                        JSONObject jsonObject = new JSONObject(final_json_from_sqlite);
                        editText.setText(jsonObject.getString("remarks"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else{
//                    remarks = editText.getText().toString();
                }

                //---added on 2nd feb, starts
                if(!userSingletonModel.getRemarks().contentEquals("")){
                    editText.setText(userSingletonModel.getRemarks());
                }else{
                    editText.setText("");
                }
                //---added on 2nd feb, ends

//                    text.setText("Notes for "+employeeTimesheetListModelArrayList.get(position).getTask()+"("+employeeTimesheetListModelArrayList.get(position).getAccountCode()+")");
//                    editText.setText(employeeTimesheetListModelArrayList.get(position).getEditTextAddNote()); //---if edittext value exists then it will load data to edittext
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setView(dialog);
                alert.setCancelable(false);
                //Creating an alert dialog
                final AlertDialog alertDialog = alert.create();
                alertDialog.show();
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userSingletonModel.getDcr_details_dcr_type_id().contentEquals("0")) {
                            Log.d("remarks-=>",editText.getText().toString());
//                            saveData(editText.getText().toString()); //--commented on 2nd feb

                            //--added on 2nd feb, code starts
                            if(DcrDetailsRemakeActivity.draft_y_n == true){
                                saveData(editText.getText().toString(), 1);
                            }else if(DcrDetailsRemakeActivity.draft_y_n == false){
                                saveData(editText.getText().toString(),2);
                            }
                            //--added on 2nd feb, code ends
                            alertDialog.dismiss();
                        }else if(!userSingletonModel.getDcr_details_dcr_type_id().contentEquals("0")){
                            if(editText.getText().toString().trim().isEmpty()){
                                tv_required.setVisibility(View.VISIBLE);
                                editText.setFocusable(true);
                                editText.setCursorVisible(true);
                            }else{
                                if(!editText.getText().toString().trim().isEmpty()){
//                                    saveData(editText.getText().toString(),2);
                                    //--added on 2nd feb, code starts
                                    if(DcrDetailsRemakeActivity.draft_y_n == true){
                                        saveData(editText.getText().toString(), 1);
                                    }else if(DcrDetailsRemakeActivity.draft_y_n == false){
                                        saveData(editText.getText().toString(),2);
                                    }
                                    //--added on 2nd feb, code ends
                                    alertDialog.dismiss();
                                }
                            }
                        }
//                        alertDialog.dismiss();  //----newly added 12th nov
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
               /* imgbtn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });*/
                break;
            default:
                break;
        }
    }

    //===========function to make JsonObject, code starts============
    public void makeJsonObject(){
        final JSONObject DocumentElementobj1 = new JSONObject();
        final JSONObject DocumentElementobj2 = new JSONObject();
        final JSONObject DocumentElementobj3 = new JSONObject();
        final JSONObject DocumentElementParentobj = new JSONObject();
        JSONArray DocumentElementarray = new JSONArray();
        JSONArray reqDctr = new JSONArray();
        JSONArray reqChemist = new JSONArray();
        JSONArray reqStockist = new JSONArray();
        JSONObject reqObjdt = new JSONObject();
        try {

            if(dcrSelectDoctorArrayList.size()>0) {
                for (int i = 0; i < dcrSelectDoctorArrayList.size(); i++) {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("name", dcrSelectDoctorArrayList.get(i).getName());
                    reqObj.put("id", dcrSelectDoctorArrayList.get(i).getId());
                    reqObj.put("work_place_id", dcrSelectDoctorArrayList.get(i).getWork_place_id());
                    reqObj.put("ecl_no", dcrSelectDoctorArrayList.get(i).getEcl_no());
                    reqObj.put("work_place_name", dcrSelectDoctorArrayList.get(i).getWork_place_name());
                    reqObj.put("amount", dcrSelectDoctorArrayList.get(i).getAmount());
                    reqObj.put("last_visit_date", dcrSelectDoctorArrayList.get(i).getLast_visit_date());
                    reqDctr.put(reqObj);
                }
            }

            if(dcrSelectChemistArrayList.size()>0) {
                for (int i = 0; i < dcrSelectChemistArrayList.size(); i++) {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("name", dcrSelectChemistArrayList.get(i).getName());
                    reqObj.put("id", dcrSelectChemistArrayList.get(i).getId());
                    reqObj.put("work_place_id", dcrSelectChemistArrayList.get(i).getWork_place_id());
                    reqObj.put("ecl_no", dcrSelectChemistArrayList.get(i).getEcl_no());
                    reqObj.put("work_place_name", dcrSelectChemistArrayList.get(i).getWork_place_name());
                    reqObj.put("amount", dcrSelectChemistArrayList.get(i).getAmount());
                    reqObj.put("last_visit_date", dcrSelectChemistArrayList.get(i).getLast_visit_date());
                    reqChemist.put(reqObj);
                }
            }

            if(dcrSelectStockistArrayList.size()>0) {
                for (int i = 0; i < dcrSelectStockistArrayList.size(); i++) {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("name", dcrSelectStockistArrayList.get(i).getName());
                    reqObj.put("id", dcrSelectStockistArrayList.get(i).getId());
                    reqObj.put("work_place_id", dcrSelectStockistArrayList.get(i).getWork_place_id());
                    reqObj.put("ecl_no", dcrSelectStockistArrayList.get(i).getEcl_no());
                    reqObj.put("work_place_name", dcrSelectStockistArrayList.get(i).getWork_place_name());
                    reqObj.put("amount", dcrSelectStockistArrayList.get(i).getAmount());
                    reqObj.put("last_visit_date", dcrSelectStockistArrayList.get(i).getLast_visit_date());
                    reqStockist.put(reqObj);
                }
            }


            /*if(DcrSelectDoctor.dcrSelectDoctorArrayList.size()>0) {
                DocumentElementobj.put("Doctor", reqDctr);
            }
            if(DcrSelectChemist.dcrSelectChemistArrayList.size()>0) {
                DocumentElementobj.put("Chemist", reqChemist);
            }
            if(DcrSelectStockist.dcrSelectStockistArrayList.size()>0) {
                DocumentElementobj.put("Stockist", reqStockist);
            }*/

            /*DocumentElementobj.put("Doctor", reqDctr);
            DocumentElementobj.put("Chemist", reqChemist);
            DocumentElementobj.put("Stockist", reqStockist);
            DocumentElementarray.put(DocumentElementobj);
            DocumentElementParentobj.put("values",DocumentElementarray);
            jsonString = DocumentElementobj.toString();
            Log.d("jsonObjectNeed", DocumentElementParentobj.toString());*/

            if(dcrSelectDoctorArrayList.size()>0) {
                DocumentElementobj1.put("type", "Doctor");
                DocumentElementobj1.put("amount_name", "Gift Amount");
                DocumentElementobj1.put("values", reqDctr);
                DocumentElementarray.put(DocumentElementobj1);
                DocumentElementParentobj.put("values", DocumentElementarray);
            }

//            DocumentElementarray = new JSONArray();
            if(dcrSelectChemistArrayList.size()>0) {
                DocumentElementobj2.put("type", "Chemist");
                DocumentElementobj2.put("amount_name", "POB Amount");
                DocumentElementobj2.put("values", reqChemist);
                DocumentElementarray.put(DocumentElementobj2);
                DocumentElementParentobj.put("values", DocumentElementarray);
            }

//            DocumentElementarray = new JSONArray();
            if(dcrSelectStockistArrayList.size()>0) {
                DocumentElementobj3.put("type", "Stockist");
                DocumentElementobj3.put("amount_name", "POB Amount");
                DocumentElementobj3.put("values", reqStockist);
                DocumentElementarray.put(DocumentElementobj3);
                DocumentElementParentobj.put("values", DocumentElementarray);
            }



            jsonString = DocumentElementParentobj.toString();
            Log.d("jsonObjectNeed", DocumentElementParentobj.toString());


            loadData(); //----calling function to load data to Expandable list

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //==============function to get jsonData and load in Expandable listview, code starts=========
    public void loadData(){
        if(!dcrSummaryContentModelArrayList.isEmpty()){
            dcrSummaryContentModelArrayList.clear();
        } if(!dcrSummaryGroupNameModelsArrayList.isEmpty()){
            dcrSummaryGroupNameModelsArrayList.clear();
        }
        if(!listDataChild.isEmpty()){
            listDataChild.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Log.d("string-=>",jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("values");
            for(int i1=0;i1<jsonArray.length();i1++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i1);
                DcrSummaryGroupNameModel dcrSummaryGroupNameModel = new DcrSummaryGroupNameModel();
                dcrSummaryGroupNameModel.setGroup_name(jsonObject1.getString("type"));
                dcrSummaryGroupNameModel.setGroup_amount(jsonObject1.getString("amount_name"));
                dcrSummaryGroupNameModelsArrayList.add(dcrSummaryGroupNameModel);
                dcrSummaryContentModelArrayList = new ArrayList<>();
                JSONArray jsonArray1 = jsonObject1.getJSONArray("values");
                for(int i2=0;i2<jsonArray1.length();i2++){
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(i2);
                    DcrSummaryContentModel dcrSummaryContentModel = new DcrSummaryContentModel();
                    dcrSummaryContentModel.setSerial_no_new_demand(i2+1);
                    dcrSummaryContentModel.setId(jsonObject2.getString("id"));
                    dcrSummaryContentModel.setName(jsonObject2.getString("name"));
                    dcrSummaryContentModel.setEcl_no(jsonObject2.getString("ecl_no"));
                    dcrSummaryContentModel.setWork_place_id(jsonObject2.getString("work_place_id"));
                    dcrSummaryContentModel.setType(jsonObject1.getString("type"));
                    dcrSummaryContentModel.setDcr_last_day_visit(jsonObject2.getString("last_visit_date"));
                    dcrSummaryContentModel.setWork_place_name(jsonObject2.getString("work_place_name"));
//                    dcrSummaryContentModel.setEdit_text_amt("0.00");
                    dcrSummaryContentModel.setEdit_text_amt(jsonObject2.getString("amount"));
                    dcrSummaryContentModelArrayList.add(dcrSummaryContentModel);

                    dcrSummaryContentModelArrayListForJsonObject.add(dcrSummaryContentModel); //---for making JsonObject(to save data)

                    listDataChild.put(dcrSummaryGroupNameModelsArrayList.get(i1),dcrSummaryContentModelArrayList);
                }
            }
            ExpandableListView explistviewData = (ExpandableListView)findViewById(R.id.lvExpPendingItems);
//                    TextView tv_data_status = rootView.findViewById(R.id.tv_data_status);
            explistviewData.setVisibility(View.VISIBLE);
            if(!dcrSummaryGroupNameModelsArrayList.isEmpty()) {
                explistviewData.setVisibility(View.VISIBLE);
                ExpandableListDcrSummaryAdapter expandableListDcrSummaryAdapter;
                expandableListDcrSummaryAdapter = new ExpandableListDcrSummaryAdapter(DcrSummary.this, dcrSummaryGroupNameModelsArrayList, listDataChild);
                explistviewData.setAdapter(expandableListDcrSummaryAdapter);
                //-----code to open child list starts----------
                for (int k = 0; k < explistviewData.getExpandableListAdapter().getGroupCount(); k++) {
                    //Expand group
                    explistviewData.expandGroup(k);
                }
                //-----code to open child list ends----------
            }else{
//                explistviewData.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //==============function to get jsonData and load in Expandable listview, code ends=========


    //==============function to make jsonObject and save data, code starts============
    //---note dcr_status parameter added on 2nd feb
    public void saveData(String remarks, int dcr_status) {
        /*for(int i=0;i<dcrSummaryGroupNameModelsArrayList.size();i++){
            Log.d("GrpName-=>",dcrSummaryGroupNameModelsArrayList.get(i).getGroup_name());
            for(int i1=0;i1<dcrSummaryContentModelArrayList.size();i1++){
//              Log.d("ContentAmt-=>",dcrSummaryContentModelArrayList.get(i1).getEdit_text_amt());
              Log.d("EclNo-=>",dcrSummaryContentModelArrayList.get(i1).getEcl_no());
            }
        }*/
        final JSONObject DocumentElementobj = new JSONObject();
        final JSONObject DocumentElementobj1 = new JSONObject();
        final JSONObject DocumentElementobj2 = new JSONObject();
        final JSONObject DocumentElementobj3 = new JSONObject();

        JSONArray reqDctr = new JSONArray();
        JSONArray reqDctr1 = new JSONArray();

        JSONArray reqChemist = new JSONArray();
        JSONArray reqChemist1 = new JSONArray();

        JSONArray reqStockist = new JSONArray();
        JSONArray reqStockist1 = new JSONArray();

        JSONArray reqWorkedWith = new JSONArray();

        try {
            DocumentElementobj.put("corp_id", userSingletonModel.getCorp_id());
            DocumentElementobj.put("dcr_id", Integer.parseInt(userSingletonModel.getDcr_id_for_dcr_summary()));
            DocumentElementobj.put("dcr_no", userSingletonModel.getDcr_no_for_dcr_summary());
            DocumentElementobj.put("dcr_date", userSingletonModel.getSelected_date_calendar_forapi_format());
            DocumentElementobj.put("msr_id", Integer.parseInt(userSingletonModel.getUser_id()));
            DocumentElementobj.put("id_base_work_place", Integer.parseInt(userSingletonModel.getBase_work_place_id()));
//            DocumentElementobj.put("base_work_place_name", userSingletonModel.getBase_work_place_name()); //--added on 3rd feb
            DocumentElementobj.put("dcr_type", Integer.parseInt(userSingletonModel.getDcr_details_dcr_type_id()));
//            DocumentElementobj.put("dcr_status", Integer.parseInt(userSingletonModel.getSelected_date_calendar_date_status()));
//            DocumentElementobj.put("dcr_status", 2); //--commented on 2nd feb
            DocumentElementobj.put("dcr_status", dcr_status); //--added on 2nd feb
            DocumentElementobj.put("remarks", remarks);
            DocumentElementobj.put("entry_user", userSingletonModel.getUser_name());
//            DocumentElementobj.put("id_cal_year", 7);
            DocumentElementobj.put("id_cal_year", Integer.parseInt(userSingletonModel.getCalendar_id()));
            if (DcrHome.workedWithArrayListManagersForDcrSummary.size() > 0) {
                for (int i = 0; i < DcrHome.workedWithArrayListManagersForDcrSummary.size(); i++) {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("id_user", Integer.parseInt(DcrHome.workedWithArrayListManagersForDcrSummary.get(i).getManagers_id()));
                    reqWorkedWith.put(reqObj);
                }
//                DocumentElementobj.put("worked_with", reqWorkedWith);
            }
            DocumentElementobj.put("worked_with", reqWorkedWith);
        }catch (JSONException e){
            e.printStackTrace();
        }



        if (dcrSummaryContentModelArrayListForJsonObject.size() > 0) {
            for (int i = 0; i < dcrSummaryContentModelArrayListForJsonObject.size(); i++) {
                Log.d("type-=>", dcrSummaryContentModelArrayListForJsonObject.get(i).getType());
                Log.d("Name-=>", dcrSummaryContentModelArrayListForJsonObject.get(i).getName());
                Log.d("Amount-=>", dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt());
                try {
                    if (dcrSummaryContentModelArrayListForJsonObject.get(i).getType().contentEquals("Doctor")) {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("id", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getId()));
                        reqObj.put("id_work_place", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getWork_place_id()));
                        reqObj.put("ecl_no", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getEcl_no()));
                        reqObj.put("last_visit_date", userSingletonModel.getSelected_date_calendar_forapi_format());
                        if(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().isEmpty()||dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().contentEquals("")){
                            reqObj.put("gift_amount", 0.00);
                        }else {
                            reqObj.put("gift_amount", Double.parseDouble(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt()));
                        }
                        reqDctr.put(reqObj);

                        //----updateDCR Select Doctor Sqlite column code starts------
                        JSONObject reqObj1 = new JSONObject();
                        reqObj1.put("name", dcrSummaryContentModelArrayListForJsonObject.get(i).getName());
                        reqObj1.put("id", dcrSummaryContentModelArrayListForJsonObject.get(i).getId());
                        reqObj1.put("work_place_id", dcrSummaryContentModelArrayListForJsonObject.get(i).getWork_place_id());
                        reqObj1.put("ecl_no", dcrSummaryContentModelArrayListForJsonObject.get(i).getEcl_no());
                        reqObj1.put("status", dcrSummaryContentModelArrayListForJsonObject.get(i).getStatus());
                        reqObj1.put("work_place_name", "");
                        if (dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().isEmpty() || dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().contentEquals("")) {
                            reqObj1.put("amount", 0.00);
                        } else {
                            reqObj1.put("amount", Double.parseDouble(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt()));
                        }
                        reqObj1.put("last_visit_date", dcrSelectDoctorArrayList.get(i).getLast_visit_date());
                        reqDctr1.put(reqObj1);

                        //----updateDCR Select Doctor Sqlite column code ends------
                    }
                    if (dcrSummaryContentModelArrayListForJsonObject.get(i).getType().contentEquals("Chemist")) {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("id", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getId()));
                        reqObj.put("id_work_place", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getWork_place_id()));
                        reqObj.put("ecl_no", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getEcl_no()));

                        if(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().isEmpty()||dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().contentEquals("")){
                            reqObj.put("pob_amount", 0.00);
                        }else {
                            reqObj.put("pob_amount", Double.parseDouble(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt()));
                        }
                        reqChemist.put(reqObj);

                        //-----updateDCR select chemist Sqlite column code starts-----
                        JSONObject reqObj1 = new JSONObject();
                        reqObj1.put("name", dcrSummaryContentModelArrayListForJsonObject.get(i).getName());
                        reqObj1.put("id", dcrSummaryContentModelArrayListForJsonObject.get(i).getId());
                        reqObj1.put("work_place_id", dcrSummaryContentModelArrayListForJsonObject.get(i).getWork_place_id());
                        reqObj1.put("ecl_no", dcrSummaryContentModelArrayListForJsonObject.get(i).getEcl_no());
                        reqObj1.put("status", dcrSummaryContentModelArrayListForJsonObject.get(i).getStatus());
                        reqObj1.put("work_place_name", "");
                        if (dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().isEmpty() || dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().contentEquals("")) {
                            reqObj1.put("amount", 0.00);
                        } else {
                            reqObj1.put("amount", Double.parseDouble(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt()));
                        }
                        reqObj1.put("last_visit_date", "");
                        reqChemist1.put(reqObj1);
                        //-----updateDCR select chemist Sqlite column code ends-----

                    }
                    if (dcrSummaryContentModelArrayListForJsonObject.get(i).getType().contentEquals("Stockist")) {
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("id", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getId()));
                        reqObj.put("id_work_place", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getWork_place_id()));
                        reqObj.put("ecl_no", Integer.parseInt(dcrSummaryContentModelArrayListForJsonObject.get(i).getEcl_no()));
                        if(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().isEmpty()||dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().contentEquals("")){
                            reqObj.put("pob_amount", 0.00);
                        }else {
                            reqObj.put("pob_amount", Double.parseDouble(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt()));
                        }
                        reqStockist.put(reqObj);

                        //-----updateDCR select stockist Sqlite column code starts-----
                        JSONObject reqObj1 = new JSONObject();
                        reqObj1.put("name", dcrSummaryContentModelArrayListForJsonObject.get(i).getName());
                        reqObj1.put("id", dcrSummaryContentModelArrayListForJsonObject.get(i).getId());
                        reqObj1.put("work_place_id", dcrSummaryContentModelArrayListForJsonObject.get(i).getWork_place_id());
                        reqObj1.put("ecl_no", dcrSummaryContentModelArrayListForJsonObject.get(i).getEcl_no());
                        reqObj1.put("status", dcrSummaryContentModelArrayListForJsonObject.get(i).getStatus());
                        reqObj1.put("work_place_name", "");
                        if (dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().isEmpty() || dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt().contentEquals("")) {
                            reqObj1.put("amount", 0.00);
                        } else {
                            reqObj1.put("amount", Double.parseDouble(dcrSummaryContentModelArrayListForJsonObject.get(i).getEdit_text_amt()));
                        }
                        reqObj1.put("last_visit_date", "");
                        reqStockist1.put(reqObj1);
                        //-----updateDCR select stockist Sqlite column code ends-----
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            try {
                DocumentElementobj.put("doctors", reqDctr);
                DocumentElementobj.put("chemists", reqChemist);
                DocumentElementobj.put("stockists", reqStockist);

                DocumentElementobj1.put("values", reqDctr1);
                Log.d("updDctr-=>",DocumentElementobj1.toString());
                sqliteDb.updateDCR("Doctor",DocumentElementobj1.toString(),userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);


                DocumentElementobj2.put("values", reqChemist1);
                Log.d("updChemist-=>",DocumentElementobj2.toString());
                sqliteDb.updateDCR("Chemist",DocumentElementobj2.toString(),userSingletonModel.getUser_id(), userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);

                DocumentElementobj3.put("values", reqStockist1);
                Log.d("updStockist-=>",DocumentElementobj3.toString());
                sqliteDb.updateDCR("Stockist",DocumentElementobj3.toString(), userSingletonModel.getUser_id(), userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("JsonObjForSaveData-=-=>",DocumentElementobj.toString());
//        final String URL = Config.BaseUrlEpharma+"dcr/save";
        //commented temporary on 26th
        final String URL = Config.BaseUrlEpharma+"epharma/DCR/Save";
// Post params to be sent to the server

        JsonObjectRequest request_json = null;
        try {
            request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(DocumentElementobj.toString()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("SubmitResponse-=>",response.toString());
                            try {
                                //Process os success response

                                JSONObject jsonObj = null;
                                try{
                                    String responseData = response.toString();
                                    String val = "";
                                    JSONObject resobj = new JSONObject(responseData);
                                    Log.d("getData",resobj.toString());

                                    if(resobj.getString("status").contentEquals("1")){
                                        try {
                                            Toast.makeText(getApplicationContext(),resobj.getString("message"),Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(DcrSummary.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            //---------Alert dialog code starts(added on 21st nov)--------
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DcrSummary.this);
                                            //--added on 2nd feb,code starts---
                                            String message = "";
                                            if(DcrHome.check_draft_yn_for_summary.contentEquals("Y")){
                                                message = "DCR saved as Draft";
                                            }else{
                                                message = "DCR sent successfully";
                                            }
                                            //--added on 2nd feb,code ends---

//                                            alertDialogBuilder.setMessage(resobj.getString("message")); //--commented on 2nd feb
                                            alertDialogBuilder.setMessage(message); //--added on 2nd feb
                                            alertDialogBuilder.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                            removeData();
                                                            //-----following code is commented on 6th dec to get the calender saved state data------
                                                            Intent intent = new Intent(DcrSummary.this,DcrHome.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            DcrSummary.this.finish();
                                                        }
                                                    });
                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();

                                            //--------Alert dialog code ends--------
                                        }catch (SQLiteException e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(),resobj.getString("message"),Toast.LENGTH_LONG).show();
                                    }
                                    Iterator<?> keys = resobj.keys();
                                    while(keys.hasNext() ) {
                                        String key = (String) keys.next();
                                        if (resobj.get(key) instanceof JSONObject) {
                                            JSONObject xx = new JSONObject(resobj.get(key).toString());

                                        }
                                    }

                                }catch (JSONException e){
                                    //                            loading.dismiss();
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                    sqliteDb.updateDCR("FinalJson",DocumentElementobj.toString(), userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(), db1);
//                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                    String message = "";
                    boolean isConnected = ConnectivityReceiver.isConnected();
                    if (isConnected == false){
                        View v = findViewById(R.id.cordinatorLayout);
                        message = "No Internet Available. DCR saved as Draft.";
//                        new Snackbar("No Internet Available. DCR saved as Draft.",v,Color.parseColor("#ffffff"));
                    }else if(isConnected == true){
                        View v = findViewById(R.id.cordinatorLayout);
                        message = "Server Error. DCR saved as Draft.";
//                        new Snackbar("Server Error. DCR saved as Draft.",v,Color.parseColor("#ffffff"));
                    }
                    //---------Alert dialog code starts(added on 21st nov)--------
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DcrSummary.this);
                    alertDialogBuilder.setMessage(message);
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    userSingletonModel.setCheck_draft_saved_last_yn("Y");
                                    //-----following code is commented on 6th dec to get the calender saved state data------
                                    Intent intent = new Intent(DcrSummary.this,HomeActivity.class);
//                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    DcrSummary.this.finish();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    //--------Alert dialog code ends--------
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

// add the request object to the queue to be executed
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request_json);  //commented temporary on 26th

    }

    //==============function to make jsonObject and save data, code ends============


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //===========function to remove data, code starts========
    public void removeData(){
        jsonString = "";

        userSingletonModel.setCheck_draft_saved_last_yn("N");
//        sqliteDb.createDatabase();
        sqliteDb.deleteDCR(userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(), userSingletonModel.getDcr_no_for_dcr_summary(), userSingletonModel.getSelected_date_calendar_forapi_format(), userSingletonModel.getCalendar_id(), db1);


        /*if (!DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()){
            DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!dcrSelectDoctorArrayList.isEmpty()){
            dcrSelectDoctorArrayList.clear();
        }
        if(!DcrHome.dcrSelectChemistArrayList.isEmpty()){
            DcrHome.dcrSelectChemistArrayList.clear();
        }
        if(!DcrHome.dcrSelectStockistArrayList.isEmpty()){
            DcrHome.dcrSelectStockistArrayList.clear();
        }
        if(!DcrHome.workedWithArrayListManagersForDcrSummary.isEmpty()){
            DcrHome.workedWithArrayListManagersForDcrSummary.clear();
        }*/

        if(!DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!DcrSelectChemistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectChemistAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!DcrHome.workedWithArrayListManagersForDcrSummary.isEmpty()){
            DcrHome.workedWithArrayListManagersForDcrSummary.clear();
        }
    }
    //===========function to remove data, code ends========

    //=============Internet checking code starts(added 22nd Nov)=============
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
//            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
           /* color = Color.parseColor("#ffffff");
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.relativeLayout), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();*/

        }
        View v = findViewById(R.id.cordinatorLayout);
//            new org.arb.timesheet_demo.config.Snackbar(message,v);
        new Snackbar(message,v, Color.parseColor("#ffffff"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    //=============Internet checking code ends(added 22nd Nov)=============



}
