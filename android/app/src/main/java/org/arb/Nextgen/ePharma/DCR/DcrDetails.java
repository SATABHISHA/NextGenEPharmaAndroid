package org.arb.Nextgen.ePharma.DCR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.GoogleMap;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.DcrTypeModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.DcrDetailsAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class DcrDetails extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rl_back_arrow;
    ImageButton imgbtn_arrrow;
//    Spinner spinner;
    MaterialSpinner spinner, spinner_type;
    LinearLayout ll2;
    RecyclerView recycler_view;
    ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayList = new ArrayList<>();
    ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers = new ArrayList<>();
//    public static ArrayList<DcrDetailsListModel> workedWithArrayListManagersForDcrSummary = new ArrayList<>();
    ArrayList<DcrTypeModel> dcrTypeModelArrayList = new ArrayList<>();
    public static int tempForDcrType = 1;
    public static int baseWorkPlacetemp = 0;

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    Button  btn_cancel;
    static Button btn_next;
    SQLiteDatabase db;

    public Context context = this;

    TextView tvMsrName, tvDCRNo, tvDCRDate, tvHQ;

    private GoogleMap mMap;
    SQLiteDatabase db1;
    SqliteDb sqliteDb = new SqliteDb();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_details);
        rl_back_arrow = findViewById(R.id.rl_back_arrow);
        ll2 = findViewById(R.id.ll2);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);
        spinner = findViewById(R.id.spinner);
        spinner_type = findViewById(R.id.spinner_type);
        recycler_view = findViewById(R.id.recycler_view);
        tvMsrName = findViewById(R.id.tvMsrName);
        tvDCRNo = findViewById(R.id.tvDCRNo);
        tvDCRDate = findViewById(R.id.tvDCRDate);
        tvHQ = findViewById(R.id.tvHQ);
        btn_next = findViewById(R.id.btn_next);
        btn_cancel = findViewById(R.id.btn_cancel);

        rl_back_arrow.setOnClickListener(this);
        imgbtn_arrrow.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


        //----setting value to text, code starts-----
        tvMsrName.setText(userSingletonModel.getUser_full_name());
//        tvDCRDate.setText(userSingletonModel.getSelected_date_calendar());
        tvDCRDate.setText(userSingletonModel.getSelected_date_calendar());
//        tvDCRNo.setText("3216");
        tvDCRNo.setText(userSingletonModel.getDcr_no_for_dcr_summary());
        tvHQ.setText(userSingletonModel.getHq_name());
        //----setting value to text, code ends-----


        //-------making Next button default non clickable fo work place selection, code starts------
        btn_next.setClickable(false);
        btn_next.setEnabled(false);
        btn_next.setAlpha(0.5f);
//        Toast.makeText(getApplicationContext(),"Please Select Work Place",Toast.LENGTH_LONG).show();
        //-------making Next button default non clickable fo work place selection, code ends------

        // Initializing a String Array
        /*String[] plants = new String[]{
                "Black birch",
                "European weeping birch"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);*/

        spinner_load_dcr_type(); //------calling function to load dcrtype in the spinner with static values


        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
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

//        loadColorData(); //---commenting demo function
        if ((db.rawQuery("SELECT * FROM dcrdetail", null).getCount()) == 0) {
            loadDcrData();
        }else{
            Log.d("SqliteTest-=>","Filled Data");
//            Toast.makeText(getApplicationContext(),"Filled Data",Toast.LENGTH_LONG).show();
            fetchDcrData();
        }


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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back_arrow:
                /*Intent intent = new Intent(DcrDetails.this,DcrHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                startActivity(new Intent(DcrDetails.this,DcrHome.class));
                break;
            case R.id.imgbtn_arrrow:
                //startActivity(new Intent(DcrDetails.this,DcrHome.class)); //--commented on 22nd jan
                //--added on 22nd jan as per discussion, code starts
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
                Intent intentCancel = new Intent(DcrDetails.this, DcrHome.class);
                intentCancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCancel);
                //--added on 22nd jan as per discussion, code ends
                break;
            case R.id.btn_next:
                saveToArrayList();
                if(tempForDcrType == 1) {
//                    startActivity(new Intent(DcrDetails.this, DcrSelectDoctor.class));
                    if(userSingletonModel.getCheck_draft_saved_last_yn().contentEquals("Y")){
                        Intent intent = new Intent(DcrDetails.this, DcrSelectDoctor.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        if (HomeActivity.cancelStatus == 1) {
                            Intent intent = new Intent(DcrDetails.this, DcrSelectDoctor.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (HomeActivity.cancelStatus == 0) {
                            startActivity(new Intent(DcrDetails.this, DcrSelectDoctor.class));
                        }
                    }
                }else if(tempForDcrType == 0){
//                    startActivity(new Intent(DcrDetails.this, DcrSummary.class));
                    Intent intent = new Intent(DcrDetails.this, DcrSummary.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                break;
            case R.id.btn_cancel:
                //---------Alert dialog code starts(added on 21st nov)--------
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DcrDetails.this);
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
                                Intent intentCancel = new Intent(DcrDetails.this, DcrHome.class);
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
                default:
                    break;
        }
    }

    //==============function for DcrType Spinner code starts============
    public void spinner_load_dcr_type(){

       /* userSingletonModel.setDcr_details_dcr_type_id("0");
        userSingletonModel.setDcr_details_dcr_type_name("Field Work");*/

        DcrTypeModel dcrTypeModel = new DcrTypeModel();
        dcrTypeModel.setId("0");
        dcrTypeModel.setDcr_type("Field Work");
        dcrTypeModelArrayList.add(dcrTypeModel);

        DcrTypeModel dcrTypeModel1 = new DcrTypeModel();
        dcrTypeModel1.setId("1");
        dcrTypeModel1.setDcr_type("Office Day");
        dcrTypeModelArrayList.add(dcrTypeModel1);

        DcrTypeModel dcrTypeModel2 = new DcrTypeModel();
        dcrTypeModel2.setId("2");
        dcrTypeModel2.setDcr_type("Travel");
        dcrTypeModelArrayList.add(dcrTypeModel2);

        DcrTypeModel dcrTypeModel3 = new DcrTypeModel();
        dcrTypeModel3.setId("3");
        dcrTypeModel3.setDcr_type("On Leave");
        dcrTypeModelArrayList.add(dcrTypeModel3);

        DcrTypeModel dcrTypeModel4 = new DcrTypeModel();
        dcrTypeModel4.setId("4");
        dcrTypeModel4.setDcr_type("Holiday");
        dcrTypeModelArrayList.add(dcrTypeModel4);

        DcrTypeModel dcrTypeModel6 = new DcrTypeModel();
        dcrTypeModel6.setId("6");
        dcrTypeModel6.setDcr_type("Other");
        dcrTypeModelArrayList.add(dcrTypeModel6);

        List<String> dcr_type = new ArrayList<>();
        for(int i=0; i<dcrTypeModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
            dcr_type.add(dcrTypeModelArrayList.get(i).getDcr_type());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DcrDetails.this, android.R.layout.simple_spinner_item, dcr_type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
        spinner_type.setSelection(1);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == -1){
//                        edt_date_select.setClickable(false);
                }else {
//                    Toast.makeText(getApplicationContext(), "Selected: " + dcrTypeModelArrayList.get(position).getId(), Toast.LENGTH_LONG).show();
                       /* msr_id = Integer.parseInt(trackingDetailsMsrNameModelArrayList.get(position).getMsr_id());
                        edt_date_select.setClickable(true);*/
                     if(!dcrTypeModelArrayList.get(position).getId().contentEquals("0")){
                         btn_next.setText("Summary");
                         recycler_view.setVisibility(View.GONE);
                         ll2.setVisibility(View.GONE);
                         tempForDcrType = 0;
                         userSingletonModel.setDcr_details_dcr_type_id(dcrTypeModelArrayList.get(position).getId());
                         userSingletonModel.setDcr_details_dcr_type_name(dcrTypeModelArrayList.get(position).getDcr_type());
                     }  else{
                         btn_next.setText("Next");
                         ll2.setVisibility(View.VISIBLE);
                         recycler_view.setVisibility(View.VISIBLE);
                         tempForDcrType = 1;
                         userSingletonModel.setDcr_details_dcr_type_id(dcrTypeModelArrayList.get(position).getId());
                         userSingletonModel.setDcr_details_dcr_type_name(dcrTypeModelArrayList.get(position).getDcr_type());
                     }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //==============function for DcrType Spinner code ends============

    //=============function for status color from api, starts.....(It's a sample code)==========
    /*public void loadColorData(){
        String url = Config.BaseUrl+"SubordinateListTimeSheetStatus";
        final ProgressDialog loading = ProgressDialog.show(DcrDetails.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getColorData(response);
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

    public void getColorData(String request){
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(request);
            String responseData = jsonObj.toString();
            String val = "";
            JSONObject resobj = new JSONObject(responseData);
            Log.d("getColor",responseData.toString());
            Iterator<?> keys = resobj.keys();
            while(keys.hasNext() ) {
                String key = (String) keys.next();
                if (resobj.get(key) instanceof JSONObject) {
                    JSONObject xx = new JSONObject(resobj.get(key).toString());
                    val = xx.getString("content");
                    JSONArray jsonArray = new JSONArray(val);
                    Log.d("value",jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        DcrDetailsListModelDemo dcrDetailsListModelDemo = new DcrDetailsListModelDemo();
                        dcrDetailsListModelDemo.setName(jsonObject.getString("name"));
                        dcrDetailsListModelDemo.setColorcode(jsonObject.getString("color_code"));
                        dcrDetailsListModelArrayListDemo.add(dcrDetailsListModelDemo);

                    }
                    recycler_view.setAdapter(new DcrDetailsAdapter(DcrDetails.this, dcrDetailsListModelArrayListDemo));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    //=============function for status color from api, ends.....==========


    //=============function for DCR Details from api and insert data into sqlite database, code starts...=============
    public void loadDcrData(){
//        String url = Config.BaseUrlEpharma + "msr/dcr-master-data/" + userSingletonModel.getUser_id() + "/7" ;
        String url = Config.BaseUrlEpharma + "msr/dcr-master-data/" + userSingletonModel.getUser_id() + "/" + userSingletonModel.getCalendar_id() ;
        final ProgressDialog loading = ProgressDialog.show(DcrDetails.this, "Loading", "Please wait...", true, false);
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
                fetchDcrData(); //---calling function to load data from sqlite in spinner
            } else {
                Toast.makeText(getApplicationContext(),"Error...",Toast.LENGTH_LONG).show();
                Log.d("Test DCRData", "Data not inserted");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //=============function for DCR Details from api and insert data into sqlite database, code ends...===============

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

            //----------------Spinner code starts---------------
            List<String> base_work_place_list = new ArrayList<>();
            for(int i=0; i<dcrDetailsListModelArrayList.size(); i++){
//           stateList  = new ArrayList<>(Arrays.asList(trackingDetailsStateSpinnerModelArrayList.get(i).getStateName()));
                base_work_place_list.add(dcrDetailsListModelArrayList.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DcrDetails.this, android.R.layout.simple_spinner_item, base_work_place_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == -1){
                        btn_next.setClickable(false);
                        btn_next.setEnabled(false);
                        btn_next.setAlpha(0.5f);
//                        Toast.makeText(getApplicationContext(),"Please Select Work Place",Toast.LENGTH_SHORT).show();
//                        edt_date_select.setClickable(false);
                    }else {
                        btn_next.setClickable(true);
                        btn_next.setEnabled(true);
                        btn_next.setAlpha(1.0f);
//                        Toast.makeText(getApplicationContext(), "Selected: " + dcrDetailsListModelArrayList.get(position).getId(), Toast.LENGTH_LONG).show();
                        userSingletonModel.setBase_work_place_id(dcrDetailsListModelArrayList.get(position).getId());
                        userSingletonModel.setBase_work_place_name(dcrDetailsListModelArrayList.get(position).getName());
                       /* msr_id = Integer.parseInt(trackingDetailsMsrNameModelArrayList.get(position).getMsr_id());
                        edt_date_select.setClickable(true);*/
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //--------for work_place, code ends-----
            //---------------Spinner code ends-----------------

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
            recycler_view.setAdapter(new DcrDetailsAdapter(DcrDetails.this, dcrDetailsListModelArrayListManagers));
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
        if(!DcrDetailsAdapter.dcrDetailsListModelArrayListManagers.isEmpty()) {
            for (int i = 0; i < DcrDetailsAdapter.dcrDetailsListModelArrayListManagers.size(); i++) {
                if (DcrDetailsAdapter.dcrDetailsListModelArrayListManagers.get(i).getStatus().contentEquals("1")) {
                    DcrDetailsListModel dcrDetailsListModel = new DcrDetailsListModel();
                    dcrDetailsListModel.setManagers_id(DcrDetailsAdapter.dcrDetailsListModelArrayListManagers.get(i).getManagers_id());
                    dcrDetailsListModel.setManagers_name(DcrDetailsAdapter.dcrDetailsListModelArrayListManagers.get(i).getManagers_name());
                    DcrHome.workedWithArrayListManagersForDcrSummary.add(dcrDetailsListModel);
                }
            }
        }

    }
    //============function to save data in the static arraylist for DcrSummary page, code ends==========

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
