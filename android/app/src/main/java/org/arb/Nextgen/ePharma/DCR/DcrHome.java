package org.arb.Nextgen.ePharma.DCR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.DcrAgainRemake.DcrDetailsRemakeActivity;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.DatesListModel;
import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.HolidayListWeekDayModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DcrHome extends AppCompatActivity {
   /* public static String calenderStartDate, calenderEndDate;
    private CaldroidFragment caldroidFragment;
    public static Bundle savedInstanceState;*/

    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    public static String dateOnSelectedCalender, check_draft_yn_for_summary = "";
    SimpleDateFormat myFormat = new SimpleDateFormat("MM-dd-yyyy");
    public static Bundle savedInstanceState;
    ArrayList<HolidayListWeekDayModel> holidayListWeekDayModelArrayList = new ArrayList<>();
    ArrayList<DatesListModel> datesListModelArrayList = new ArrayList<>();
    ArrayList<String> datesDraftArraylist = new ArrayList<>();

    /*public ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorArrayList = new ArrayList<>();
    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectChemistArrayList = new ArrayList<>();
    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectStockistArrayList = new ArrayList<>();*/
    public static ArrayList<DcrDetailsListModel> workedWithArrayListManagersForDcrSummary = new ArrayList<>();

    public String jsonDcrSelectDoctor = "", jsonDcrSelectChemist = "", jsonDcrSelectStockist = "";
    SqliteDb sqliteDb = new SqliteDb();

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    String cl, dateString;
    Date selectDate;
    String dateForParameter;
    ArrayList<Date> disableDates = new ArrayList<Date>();
    List<Date> selectedDates = new ArrayList<Date>();
    ImageButton imgbtn_home;

    public static String calenderStartDate, calenderEndDate, startDateForApi, endDateForApi;
    public static int tempForDcrSummaryBack = 0;
    public static int dcrStatus = 0; //---for enabling/disabling button for DcrSummary page

    SQLiteDatabase db, db1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_home);
        imgbtn_home = findViewById(R.id.imgbtn_home);
        imgbtn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DcrHome.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                finish();
            }
        });

        //---------initializing sqlitedatabase, code starts---
        try {
            db = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        sqliteDb.deleteDCR(db);
        //---------initializing sqlitedatabase, code ends---

        //---------initializing sqlitedatabase to save doctor/chemist/stockist, code starts(added on 2nd feb)---
        try {
            db1 = openOrCreateDatabase("EPharmaDb", MODE_PRIVATE, null);
            db1.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR, draft_yn VARCHAR, misc VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
       //---------initializing sqlitedatabase to save doctor/chemist/stockist, code ends(added on 2nd feb)---

        //----------------calender date code for api parameter use, starts...-----------
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = 01;
        int day = 01;
        dateForParameter = String.valueOf(month)+"/"+String.valueOf(day)+"/"+String.valueOf(year);
        //----------------calender date code for api parameter use, ends...-----------

        //==================calender code, starts===============
        Calendar calendarMin = Calendar.getInstance();
        Calendar calendarMax = Calendar.getInstance();
        calendarMin.add(Calendar.MONTH,-5);
        calendarMax.add(Calendar.MONTH, +1);

        int min = calendarMin.getActualMinimum(Calendar.DAY_OF_MONTH);
        int max = calendarMax.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendarMin.set(Calendar.DAY_OF_MONTH, min);
        calendarMax.set(Calendar.DAY_OF_MONTH, max);


        SimpleDateFormat formatter1 = new SimpleDateFormat("MM-dd-yyyy");
        calenderStartDate = formatter1.format(new Date(calendarMin.getTime().toString()));
        calenderEndDate = formatter1.format(new Date(calendarMax.getTime().toString()));

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
//        startDateForApi = formatter2.format(new Date(calendarMin.getTime().toString()));
        startDateForApi = formatter2.format(new Date(userSingletonModel.getCalendar_start_date()));
//        endDateForApi = formatter2.format(new Date(calendarMax.getTime().toString()));
        endDateForApi = formatter2.format(new Date(userSingletonModel.getCalendar_end_date()));


//        String dateString = formatter1.format(calendar.getTime().toString());
//        Log.d("Calender max date",dateString);
        //=======================calender code ends=================================
        Date startDate = null;
        try {
            startDate = (Date) myFormat.parse(calenderStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = (Date) myFormat.parse(calenderEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
        long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
        long curTime = startDate.getTime();
        while (curTime <= endTime) {
            disableDates.add(new Date(curTime));
            curTime += interval;
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");


        //==========Calender code starts===========

        // version, uncomment below line ****
//        holidayDate();  //-----calling holidayDate() to allocate the holidays to the calender dates
        loadDates();  //-----calling loadDates() to allocate the holidays to the calender dates
//        caldroidFragment = new CaldroidSampleCustomFragment();
        caldroidFragment = new CaldroidFragment();
        // Setup arguments

        // If Activity is created after rotation
        this.savedInstanceState = savedInstanceState;
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);


            caldroidFragment.setArguments(args);

        }

        //setCustomResourceForDates();
        // Service Call
        // setCustomResourceForDate();
        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_date_list, caldroidFragment);
        t.commit();


        // Setup listener
        final Date finalEndDate = endDate;
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
               /* Intent intent = new Intent(DcrHome.this, DcrDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                final ProgressDialog loading = ProgressDialog.show(DcrHome.this, "Please Wait", "Please wait...", true, false);
                SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                int temp = 0;
                String dateInFormatted = "", dcr_id = "";

                dateOnSelectedCalender = originalFormat.format(date);
                //===========(Commented on 5th dec, as all dates should be clickable)Code to check, whether selected date is available in the arraylist or not...starts======
                /*for (int i = 0; i<datesListModelArrayList.size(); i++){
                    if(dateOnSelectedCalender.toString().contains(datesListModelArrayList.get(i).getDcr_date())) {
//                   Toast.makeText(getApplicationContext(), holidayListWeekDayModelArrayList.get(i).getName(), Toast.LENGTH_LONG).show();
                        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        DateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");

                        String inputText = datesListModelArrayList.get(i).getDcr_date();
                        Date date1 = null;
                        try {
                            date1 = inputFormat.parse(inputText);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputText = outputFormat.format(date1);
                        String outputText1 = outputFormat1.format(date1);

                        userSingletonModel.setSelected_date_calendar(outputText); //---to access throughout the application
                        userSingletonModel.setSelected_date_calendar_forapi_format(outputText1); //---to access throughout the application
                        userSingletonModel.setSelected_date_calendar_date_status(datesListModelArrayList.get(i).getApproval_status());

//                        Toast.makeText(getApplicationContext(),"Selected Date:"+outputText, Toast.LENGTH_LONG).show();
                        Log.d("Selected Date:",outputText1);
                       *//* tv_holiday_date.setText(outputText);
                        tv_holiday_name.setText(holidayListWeekDayModelArrayList.get(i).getName());*//*

                       loadDcrData(datesListModelArrayList.get(i).getDcr_id(),userSingletonModel.getUser_id(),outputText1); //---calling function to load dcr data if aailabe


                        //---commentin g the below code, as some logic changed
                        *//*Intent intent = new Intent(DcrHome.this, DcrDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*//*
                    }
                }*/
                //===========Code to check, whether selected date is available in the arraylist or not...ends======

                DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                DateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");



                if(!datesListModelArrayList.isEmpty()) {
                    for (int i = 0; i < datesListModelArrayList.size(); i++) {
//                    String inputText = datesListModelArrayList.get(i).getDcr_date(); //commented on 6th dec
                        String inputText = dateOnSelectedCalender;
                        Date date1 = null;
                        try {
                            date1 = inputFormat.parse(inputText);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputText = outputFormat.format(date1);
                        String outputText1 = outputFormat1.format(date1);


                        userSingletonModel.setSelected_date_calendar(outputText); //---to access throughout the application
                        userSingletonModel.setSelected_date_calendar_forapi_format(outputText1); //---to access throughout the application
                        userSingletonModel.setSelected_date_calendar_date_status(datesListModelArrayList.get(i).getApproval_status());
                        Log.d("Selected Date:", outputText1);
                        if (dateOnSelectedCalender.toString().contains(datesListModelArrayList.get(i).getDcr_date())) {
                            temp = 1;
                            dcr_id = datesListModelArrayList.get(i).getDcr_id();
                            userSingletonModel.setApproval_status(datesListModelArrayList.get(i).getApproval_status());
                            if (userSingletonModel.getApproval_status().contentEquals("0")) {
                                userSingletonModel.setApproval_status_name("~");
                            }
                            if (userSingletonModel.getApproval_status().contentEquals("1")) {
                                userSingletonModel.setApproval_status_name("Saved");
                                temp = 0;
                            }
                            if (userSingletonModel.getApproval_status().contentEquals("2")) {
                                userSingletonModel.setApproval_status_name("Sent");
                            }
                            if (userSingletonModel.getApproval_status().contentEquals("3")) {
                                userSingletonModel.setApproval_status_name("Approved");
                            }
                            if (userSingletonModel.getApproval_status().contentEquals("4")) {
                                userSingletonModel.setApproval_status_name("Returned");
                            }
                            dateInFormatted = outputText1;
                            break;
                        } else {
                            //--no need of dcr_id
                            userSingletonModel.setApproval_status("0");
                            userSingletonModel.setApproval_status_name("~");
                            dcr_id = "0";
                            dateInFormatted = outputText1;
                            temp = 0;

                        }
                    }

                    if (temp == 1) {
                        loading.dismiss();
                        dcrStatus = 1;
//                    loadDcrData(datesListModelArrayList.get(i).getDcr_id(), userSingletonModel.getUser_id(), outputText1, outputText1); //---calling function to load dcr data if aailabe
                        loadDcrData(dcr_id, userSingletonModel.getUser_id(), dateInFormatted, dateInFormatted); //---calling function to load dcr data if aailabe

                    } else if (temp == 0) {
                        loading.dismiss();
                        dcrStatus = 0;
                        loadDcrData(dcr_id, userSingletonModel.getUser_id(), dateInFormatted, dateInFormatted);
                    }
                }else{
                    loading.dismiss();
                    String inputText = dateOnSelectedCalender;
                    Date date1 = null;
                    try {
                        date1 = inputFormat.parse(inputText);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputText = outputFormat.format(date1);
                    String outputText1 = outputFormat1.format(date1);
                    dateInFormatted = outputText1;

                    dcrStatus = 0;
                    dcr_id = "0";
                    loadDcrData(dcr_id, userSingletonModel.getUser_id(), dateInFormatted, dateInFormatted);
                }
            }





            @Override
            public void onLongClickDate(Date date, View view) {
                // Toast.makeText(getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    // Toast.makeText(getApplicationContext(), "Caldroid view is created", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);
//                loadDates();
              /*  tv_holiday_date.setVisibility(View.INVISIBLE);
                tv_holiday_name.setVisibility(View.INVISIBLE);*/
//                loadDates();
            }
        };
        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
        //==========Calender code ends===========

       /* if(!dcrSelectDoctorArrayList.isEmpty()){
            dcrSelectDoctorArrayList.clear();
        }
        if(!dcrSelectChemistArrayList.isEmpty()){
            dcrSelectChemistArrayList.clear();
        }
        if(!dcrSelectStockistArrayList.isEmpty()){
            dcrSelectStockistArrayList.clear();
        }
        if(!workedWithArrayListManagersForDcrSummary.isEmpty()){
            workedWithArrayListManagersForDcrSummary.clear();
        }*/

    }


    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //---commented on 15th July
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

    //================to get the menu of dates using volley code starts====================
    public void loadDates(){
        SimpleDateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDateCalendar = null, endDateCalendar = null;
        try {
            startDateCalendar = (Date)startDateFormat.parse(calenderStartDate);
            endDateCalendar = (Date)startDateFormat.parse(calenderEndDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("calendar Start-=>",startDateCalendar.toString());
//        String url = "http://220.225.40.151:9029/api/calendar/"+userSingletonModel.getUser_id()+"/"+startDateForApi+"/"+endDateForApi+"/"+userSingletonModel.getCalendar_id();
        String url = Config.BaseUrlEpharma+"epharma/calendar/"+userSingletonModel.getCorp_id()+"/"+userSingletonModel.getUser_id()+"/"+startDateForApi+"/"+endDateForApi+"/"+userSingletonModel.getCalendar_id();
        final ProgressDialog loading = ProgressDialog.show(this, "Loading", "Please wait...", true, false);

        Log.d("url",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        loading.dismiss();
                        JSONObject jsonObj = null;
                        Log.d("response-=>",response.toString());
                        try{
                            jsonObj = XML.toJSONObject(response);
                            String responseData = jsonObj.toString();
                            String val = "";
                            jsonObj = new JSONObject(response);
                            JSONObject jsonObject = jsonObj.getJSONObject("response");


                            if(jsonObject.getString("status").contentEquals("true")){

                                //---get draft dates and do color, code starts-----
                               /* if(!datesDraftArraylist.isEmpty()){
                                    datesDraftArraylist.clear();
                                }*/
                                if(!datesDraftArraylist.isEmpty()){
                                    datesDraftArraylist.clear();
                                }
                                datesDraftArraylist = sqliteDb.fetchDraftDateDCR(datesDraftArraylist,db);
                                for(int i=0;i<datesDraftArraylist.size();i++){
                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    Date draft_date_current_format = inputFormat.parse(datesDraftArraylist.get(i));
                                    String draft_date_otput_format = outputFormat.format(draft_date_current_format);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    ColorDrawable color = new ColorDrawable(Color.parseColor("#f9b6e9"));
                                    Log.d("DraftDate-=>",draft_date_otput_format.toString());
//                                    caldroidFragment.setBackgroundDrawableForDate(color,dateFormat.parse(draft_date_otput_format)); //--commented on 2nd feb
                                }
                                //---get draft dates and do color, code ends-----

                                JSONArray jsonArray = jsonObj.getJSONArray("calendar");
                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    DatesListModel datesListModel = new DatesListModel();
//                                    datesListModel.setDcr_id(jsonObject1.getString("dcr_id"));
                                    datesListModel.setDcr_id(jsonObject1.getString("id"));
                                    datesListModel.setDcr_date(jsonObject1.getString("cal_date"));
                                    datesListModel.setHoliday_yn(jsonObject1.getString("holiday_yn"));
                                    datesListModel.setHoliday_reason(jsonObject1.getString("holiday_reason"));
//                                    datesListModel.setApproval_status(jsonObject1.getString("approval_status"));
                                    datesListModel.setApproval_status(jsonObject1.getString("dcr_status"));
                                    if (jsonObject1.getString("dcr_status").contentEquals("0")) {
                                        if(datesListModel.getHoliday_yn().contentEquals("1")){
                                            datesListModel.setDates_color("#93B0FD");
                                        }else {
                                            datesListModel.setDates_color("#ffffff");
                                        }
                                    }
                                    if (jsonObject1.getString("dcr_status").contentEquals("1")) {
                                        if(datesListModel.getHoliday_yn().contentEquals("1")){
                                            datesListModel.setDates_color("#93B0FD");
                                        }else {
                                            datesListModel.setDates_color("#f5b1f5");
                                        }
                                    }
                                    if (jsonObject1.getString("dcr_status").contentEquals("2")) {
                                        if(datesListModel.getHoliday_yn().contentEquals("1")){
                                            datesListModel.setDates_color("#93B0FD");
                                        }else {
                                            datesListModel.setDates_color("#bff262");
                                        }
                                    }
                                    if (jsonObject1.getString("dcr_status").contentEquals("3")) {
                                        if(datesListModel.getHoliday_yn().contentEquals("1")){
                                            datesListModel.setDates_color("#93B0FD");
                                        }else {
                                            datesListModel.setDates_color("#c0a7f5");
                                        }
                                    }
                                    if (jsonObject1.getString("dcr_status").contentEquals("4")) {
                                        if(datesListModel.getHoliday_yn().contentEquals("1")){
                                            datesListModel.setDates_color("#93B0FD");
                                        }else {
                                            datesListModel.setDates_color("#fdc084");
                                        }
                                    }
                                    datesListModelArrayList.add(datesListModel);

                                    for (int j = 0; j < datesListModelArrayList.size(); j++) {
                                        dateString = datesListModelArrayList.get(j).getDcr_date();
                                        cl = datesListModelArrayList.get(j).getDates_color();


                                    }

                                    Date cDate = new Date();
                                    String fDate = new SimpleDateFormat("MM-dd-yyyy").format(cDate);

                                    SimpleDateFormat myFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                                    selectDate = (Date) myFormat1.parse(dateString);
                                    Log.d("selected Date-=>", selectDate.toString());
                                    ColorDrawable color = new ColorDrawable(Color.parseColor(cl));
                                    caldroidFragment.setBackgroundDrawableForDate(color, selectDate);
                                    selectedDates.add(selectDate);
                                    disableDates.removeAll(selectedDates);
                                    caldroidFragment.setTextColorForDate(R.color.caldroid_black, selectDate);

                                }

                                //---get sunday dates and do color, code starts----
                                List<String> sundayDates = new ArrayList<>();
                                int dayOfWeek = Calendar.SUNDAY;
                                Calendar cal = new GregorianCalendar();
                                cal.set(2019, 0, 1, 0, 0);
                                cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                                String sdemo;
                                while (cal.get(Calendar.YEAR) == 2019) {
                                    System.out.println(cal.getTime());
//                                    Log.d("Sunday time-=>",cal.getTime().toString());
                                    SimpleDateFormat myFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                                    sdemo = myFormat2.format(cal.getTime());
                                    sundayDates.add(sdemo);
                                    Log.d("Sunday time-=>",sdemo);
                                    cal.add(Calendar.DAY_OF_MONTH, 7);
                                }
                                for(int i=0;i<sundayDates.size();i++){
                                    SimpleDateFormat myFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                                    Date dateSunday=(Date)myFormat2.parse(sundayDates.get(i));
                                    ColorDrawable color = new ColorDrawable(Color.parseColor("#ffa579"));
                                    caldroidFragment.setBackgroundDrawableForDate(color, dateSunday);
                                }
                                //---get sunday dates and do color, code ends----




                                caldroidFragment.refreshView();
//                                loading.dismiss();
                            }
                            loading.dismiss();
                        }catch (JSONException e){
                            loading.dismiss();
                            e.printStackTrace();
                        } catch (ParseException e) {
                            loading.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(DcrHome.this);
        requestQueue.add(stringRequest);
    }

    //================to get the menu of dates using volley code ends===========
    @Override
    protected void onPostResume() {
        super.onPostResume();
//        holidayDate();
        loadDates();
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(DcrHome.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        finish();
//        startActivity(new Intent(DcrHome.this, HomeActivity.class));
    }


    //==============function to load dcrDetails(if available then redirect to DcrSummary), code starts==========
    public void loadDcrData(String dcr_id, final String msr_id, String selectedDate, final String selected_date_for_zero_id) {
        final ProgressDialog loading = ProgressDialog.show(this, "Loading", "Please wait...", true, false);
        final String selectedDateForSql = selectedDate;
        final String msrIdForSql = msr_id;
        if (dcr_id.contentEquals("-1")) {
           loading.dismiss();
        } else {
            String url = Config.BaseUrlEpharma + "epharma/dcr/"+userSingletonModel.getCorp_id()+"/" + dcr_id + "/" + msr_id + "/" + selectedDate + "/" + userSingletonModel.getCalendar_id();
            Log.d("url testing-=>", url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("responseTest-=>",response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("doctors");
                        JSONArray jsonArray1 = jsonObject.getJSONArray("chemist");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("stockist");
                        JSONArray jsonArray3 = jsonObject.getJSONArray("worked_with");
                        if (jsonObject.getInt("dcr_id") > 0) {
                            userSingletonModel.setDcr_id_for_dcr_summary(jsonObject.getString("dcr_id"));
                            userSingletonModel.setDcr_no_for_dcr_summary(jsonObject.getString("dcr_no"));
//                        userSingletonModel.setSelected_date_calendar_forapi_format(jsonObject.getString("dcr_date")); //--commented, as it is giving another format from api
                            userSingletonModel.setBase_work_place_id(jsonObject.getString("id_base_work_place"));
                            userSingletonModel.setDcr_details_dcr_type_id(jsonObject.getString("dcr_type"));
                            userSingletonModel.setRemarks(jsonObject.getString("remarks"));
                            if (jsonObject.getInt("dcr_type") == 0) {
                                userSingletonModel.setDcr_details_dcr_type_name("Field Work");
                            } else if (jsonObject.getInt("dcr_type") == 1) {
                                userSingletonModel.setDcr_details_dcr_type_name("Office Day");
                            } else if (jsonObject.getInt("dcr_type") == 2) {
                                userSingletonModel.setDcr_details_dcr_type_name("Travel");
                            } else if (jsonObject.getInt("dcr_type") == 3) {
                                userSingletonModel.setDcr_details_dcr_type_name("On Leave");
                            } else if (jsonObject.getInt("dcr_type") == 4) {
                                userSingletonModel.setDcr_details_dcr_type_name("Holiday");
                            } else if (jsonObject.getInt("dcr_type") == 6) {
                                userSingletonModel.setDcr_details_dcr_type_name("Other");
                            }
//                            userSingletonModel.setBase_work_place_name("");
                            userSingletonModel.setBase_work_place_name(jsonObject.getString("base_work_place_name"));
                            userSingletonModel.setSelected_date_calendar_date_status(jsonObject.getString("dcr_status")); //---if data is availabe, otherwise calendar status will be set
                            userSingletonModel.setDcr_remarks_for_dcr_summary(jsonObject.getString("remarks"));
                            userSingletonModel.setDcr_entry_user_for_dcr_summary(jsonObject.getString("entry_user"));
                            userSingletonModel.setDcr_id_cal_year_for_dcr_summary(jsonObject.getString("id_cal_year"));

                            if (jsonArray.length() > 0) {
                                //--opened comment on 2nd feb, starts
                               /* if (!dcrSelectDoctorArrayList.isEmpty()) {
                                    dcrSelectDoctorArrayList.clear();
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                                    dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
                                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("id_work_place"));
                                    dcrSelectDoctorStockistChemistModel.setLast_visit_date(jsonObject1.getString("last_visit_date"));
                                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("gift_amount"));
                                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                                    dcrSelectDoctorArrayList.add(dcrSelectDoctorStockistChemistModel);
                                } */ //--opened comment on 2nd feb, ends

                                //---making jsonobject for doctor, code starts----
                                final JSONObject DocumentElementobj1 = new JSONObject();
                                JSONArray reqDctr = new JSONArray();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    JSONObject reqObj = new JSONObject();
                                    reqObj.put("name", jsonObject1.getString("name"));
                                    reqObj.put("id", jsonObject1.getString("id"));
                                    reqObj.put("work_place_id", jsonObject1.getString("id_work_place"));
                                    reqObj.put("ecl_no", jsonObject1.getString("ecl_no"));
                                    reqObj.put("work_place_name", jsonObject1.getString("work_place_name"));
                                    reqObj.put("amount", jsonObject1.getString("gift_amount"));
                                    reqObj.put("last_visit_date", jsonObject1.getString("last_visit_date"));
                                    reqDctr.put(reqObj);
                                }
                                DocumentElementobj1.put("values",reqDctr);
                                jsonDcrSelectDoctor = DocumentElementobj1.toString();
//                                userSingletonCustomJsonModel.setJsonDoctorString(DocumentElementobj1.toString());
                                Log.d("jsonDoctorTest-=>",DocumentElementobj1.toString());
                                //---making jsonobject for doctor, code ends----
                            }



                            if (jsonArray1.length() > 0) {

                                /*if (!DcrHome.dcrSelectChemistArrayList.isEmpty()) {
                                    DcrHome.dcrSelectChemistArrayList.clear();
                                }

                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                                    dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
                                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("id_work_place"));
                                    dcrSelectDoctorStockistChemistModel.setLast_visit_date("");
                                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("pob_amount"));
                                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                                    DcrHome.dcrSelectChemistArrayList.add(dcrSelectDoctorStockistChemistModel);
                                }*/
                                //---making jsonobject for chemist, code starts----
                                final JSONObject DocumentElementobj1 = new JSONObject();
                                JSONArray reqChemist = new JSONArray();

                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    JSONObject reqObj = new JSONObject();
                                    reqObj.put("name", jsonObject1.getString("name"));
                                    reqObj.put("id", jsonObject1.getString("id"));
                                    reqObj.put("work_place_id", jsonObject1.getString("id_work_place"));
                                    reqObj.put("ecl_no", jsonObject1.getString("ecl_no"));
                                    reqObj.put("work_place_name", jsonObject1.getString("work_place_name"));
                                    reqObj.put("amount", jsonObject1.getString("pob_amount"));
                                    reqObj.put("last_visit_date", "");
                                    reqChemist.put(reqObj);
                                }
                                DocumentElementobj1.put("values",reqChemist);
                                jsonDcrSelectChemist = DocumentElementobj1.toString();
//                                userSingletonCustomJsonModel.setJsonDoctorString(DocumentElementobj1.toString());
                                Log.d("jsonChemistTest-=>",DocumentElementobj1.toString());
                                //---making jsonobject for chemist, code ends----
                            }



                            if (jsonArray2.length() > 0) {
                                /*if (!DcrHome.dcrSelectStockistArrayList.isEmpty()) {
                                    DcrHome.dcrSelectStockistArrayList.clear();
                                }
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                                    DcrSelectDoctorStockistChemistModel dcrSelectDoctorStockistChemistModel = new DcrSelectDoctorStockistChemistModel();
                                    dcrSelectDoctorStockistChemistModel.setId(jsonObject1.getString("id"));
                                    dcrSelectDoctorStockistChemistModel.setName(jsonObject1.getString("name"));
                                    dcrSelectDoctorStockistChemistModel.setEcl_no(jsonObject1.getString("ecl_no"));
                                    dcrSelectDoctorStockistChemistModel.setWork_place_id(jsonObject1.getString("id_work_place"));
                                    dcrSelectDoctorStockistChemistModel.setLast_visit_date("");
                                    dcrSelectDoctorStockistChemistModel.setAmount(jsonObject1.getString("pob_amount"));
                                    dcrSelectDoctorStockistChemistModel.setWork_place_name(jsonObject1.getString("work_place_name"));
                                    DcrHome.dcrSelectStockistArrayList.add(dcrSelectDoctorStockistChemistModel);
                                }*/

                                //---making jsonobject for stockist, code starts----
                                final JSONObject DocumentElementobj1 = new JSONObject();
                                JSONArray reqStockist = new JSONArray();

                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                                    JSONObject reqObj = new JSONObject();
                                    reqObj.put("name", jsonObject1.getString("name"));
                                    reqObj.put("id", jsonObject1.getString("id"));
                                    reqObj.put("work_place_id", jsonObject1.getString("id_work_place"));
                                    reqObj.put("ecl_no", jsonObject1.getString("ecl_no"));
                                    reqObj.put("work_place_name", jsonObject1.getString("work_place_name"));
                                    reqObj.put("amount", jsonObject1.getString("pob_amount"));
                                    reqObj.put("last_visit_date", "");
                                    reqStockist.put(reqObj);
                                }
                                DocumentElementobj1.put("values",reqStockist);
                                jsonDcrSelectStockist = DocumentElementobj1.toString();
//                                userSingletonCustomJsonModel.setJsonDoctorString(DocumentElementobj1.toString());
                                Log.d("jsonStockistTest-=>",DocumentElementobj1.toString());
                                //---making jsonobject for stockist, code ends----
                            }


                            if (jsonArray3.length() > 0) {
                                if (!DcrHome.workedWithArrayListManagersForDcrSummary.isEmpty()) {
                                    DcrHome.workedWithArrayListManagersForDcrSummary.clear();
                                }
                                for (int i = 0; i < jsonArray3.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray3.getJSONObject(i);
                                    DcrDetailsListModel dcrDetailsListModel = new DcrDetailsListModel();
                                    dcrDetailsListModel.setManagers_id(jsonObject1.getString("id_user"));
                                    dcrDetailsListModel.setManagers_name(jsonObject1.getString("name"));
                                    workedWithArrayListManagersForDcrSummary.add(dcrDetailsListModel);
                                }
                            }

                            //---calling SqliteDb class to create and insert data, code starts----
//                            sqliteDb.createDatabase();
                            String misc = null;
//                            check_draft_yn_for_summary = "N"; //commented on 2nd feb
//                            sqliteDb.insertDataDCR(jsonObject.getString("dcr_id"),jsonObject.getString("dcr_no"),msr_id, selectedDateForSql, userSingletonModel.getCalendar_id(),jsonDcrSelectDoctor,jsonDcrSelectChemist,jsonDcrSelectStockist,"", "N",misc, db); //--commented on 2nd feb
                            //---calling SqliteDb class to create and insert data, code ends----

                            tempForDcrSummaryBack = 1;
                            loading.dismiss(); //20th april
                            /*Intent intent = new Intent(DcrHome.this, DcrSummary.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);*/ //--commented on 2nd feb

                            //--added on 2nd feb, code starts
                            if(userSingletonModel.getApproval_status().contentEquals("1")){
                                check_draft_yn_for_summary = "Y";
                                sqliteDb.updateDCR("Doctor",jsonDcrSelectDoctor.toString(),userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);
                                sqliteDb.updateDCR("Chemist",jsonDcrSelectChemist.toString(),userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);
                                sqliteDb.updateDCR("Stockist",jsonDcrSelectStockist.toString(),userSingletonModel.getUser_id(),userSingletonModel.getDcr_id_for_dcr_summary(),userSingletonModel.getDcr_no_for_dcr_summary(),userSingletonModel.getSelected_date_calendar_forapi_format(),userSingletonModel.getCalendar_id(),db1);
                                Intent intent = new Intent(DcrHome.this, DcrDetailsRemakeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                sqliteDb.insertDataDCR(jsonObject.getString("dcr_id"),jsonObject.getString("dcr_no"),msr_id, selectedDateForSql, userSingletonModel.getCalendar_id(),jsonDcrSelectDoctor,jsonDcrSelectChemist,jsonDcrSelectStockist,"", "Y",misc, db);
                            }else{
                                check_draft_yn_for_summary = "N";
                                Intent intent = new Intent(DcrHome.this, DcrSummary.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                sqliteDb.insertDataDCR(jsonObject.getString("dcr_id"),jsonObject.getString("dcr_no"),msr_id, selectedDateForSql, userSingletonModel.getCalendar_id(),jsonDcrSelectDoctor,jsonDcrSelectChemist,jsonDcrSelectStockist,"", "N",misc, db);
                            }
                            //--added on 2nd feb, code ends

                        } else if (jsonObject.getInt("dcr_id") == 0) {
                            tempForDcrSummaryBack = 0;

                       /* userSingletonModel.setDcr_id_for_dcr_summary("0");
                        userSingletonModel.setDcr_no_for_dcr_summary("0");
                        userSingletonModel.setSelected_date_calendar_forapi_format(selected_date_for_zero_id);

                        Intent intent = new Intent(DcrHome.this, DcrDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/

                            userSingletonModel.setDcr_id_for_dcr_summary(jsonObject.getString("dcr_id"));
                            userSingletonModel.setDcr_no_for_dcr_summary(jsonObject.getString("dcr_no"));
//                        userSingletonModel.setSelected_date_calendar_forapi_format(jsonObject.getString("dcr_date")); //--commented, as it is giving another format from api
                            userSingletonModel.setBase_work_place_id(jsonObject.getString("id_base_work_place"));
                            userSingletonModel.setDcr_details_dcr_type_id(jsonObject.getString("dcr_type"));
                            if (jsonObject.getInt("dcr_type") == 0) {
                                userSingletonModel.setDcr_details_dcr_type_name("Field Work");
                            } else if (jsonObject.getInt("dcr_type") == 1) {
                                userSingletonModel.setDcr_details_dcr_type_name("Office Day");
                            } else if (jsonObject.getInt("dcr_type") == 2) {
                                userSingletonModel.setDcr_details_dcr_type_name("Travel");
                            } else if (jsonObject.getInt("dcr_type") == 3) {
                                userSingletonModel.setDcr_details_dcr_type_name("On Leave");
                            } else if (jsonObject.getInt("dcr_type") == 4) {
                                userSingletonModel.setDcr_details_dcr_type_name("Holiday");
                            } else if (jsonObject.getInt("dcr_type") == 6) {
                                userSingletonModel.setDcr_details_dcr_type_name("Other");
                            }
                            userSingletonModel.setBase_work_place_name("");
                            userSingletonModel.setSelected_date_calendar_date_status(jsonObject.getString("dcr_status")); //---if data is availabe, otherwise calendar status will be set
                            userSingletonModel.setDcr_remarks_for_dcr_summary(jsonObject.getString("remarks"));
                            userSingletonModel.setDcr_entry_user_for_dcr_summary(jsonObject.getString("entry_user"));
                            userSingletonModel.setDcr_id_cal_year_for_dcr_summary(jsonObject.getString("id_cal_year"));


                            String check_draft_yn = "";
                            check_draft_yn = sqliteDb.fetchDraftDCR(10,userSingletonModel.getUser_id(),jsonObject.getString("dcr_id"),jsonObject.getString("dcr_no"),selectedDateForSql,userSingletonModel.getCalendar_id(),db);

//                            Log.d("Draftresult-=>",check_draft_yn);
                            //--commented on 2nd feb
                            /*if(check_draft_yn.contentEquals("Y")){
                                loading.dismiss(); //20th april
                                check_draft_yn_for_summary = "Y"; //--to be used in DcrSummary page
                                Intent intent = new Intent(DcrHome.this, DcrSummary.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else {
                                //---calling SqliteDb class to create and insert data, code starts----
//                            sqliteDb.createDatabase();
                                loading.dismiss(); //20th april
                                check_draft_yn_for_summary = "N";
                                String json_doctor = null, json_chemist = null, json_stockist = null, json_final = null, misc = null;
                                sqliteDb.insertDataDCR(jsonObject.getString("dcr_id"), jsonObject.getString("dcr_no"), msr_id, selectedDateForSql, userSingletonModel.getCalendar_id(), json_doctor, json_chemist, json_stockist, json_final, misc, "N", db);
                                //---calling SqliteDb class to create and insert data, code ends----

//                                Intent intent = new Intent(DcrHome.this, DcrDetails.class);
                                Intent intent = new Intent(DcrHome.this, DcrDetailsRemakeActivity.class); //again remake 28th jan
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                            }*/ //--commented on 2nd feb

                            loading.dismiss(); //20th april
                            check_draft_yn_for_summary = "N";
                            String json_doctor = null, json_chemist = null, json_stockist = null, json_final = null, misc = null;
                            sqliteDb.insertDataDCR(jsonObject.getString("dcr_id"), jsonObject.getString("dcr_no"), msr_id, selectedDateForSql, userSingletonModel.getCalendar_id(), json_doctor, json_chemist, json_stockist, json_final, misc, "N", db);
                            //---calling SqliteDb class to create and insert data, code ends----

//                                Intent intent = new Intent(DcrHome.this, DcrDetails.class);
                            Intent intent = new Intent(DcrHome.this, DcrDetailsRemakeActivity.class); //again remake 28th jan
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(DcrHome.this);
            requestQueue.add(stringRequest);
        }
    }
    //==============function to load dcrDetails(if available then redirect to DcrSummary), code ends============
}
