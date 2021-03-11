package org.arb.Nextgen.ePharma.Email;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Model.EmailModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.CustomEmailAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EmailHomeActivity extends AppCompatActivity implements View.OnClickListener {
    ListView list_email;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<EmailModel> emailModelArrayList = new ArrayList<>();
    public static String id_mail_inbox, date;
    ImageButton imgbtn_arrrow;
    RelativeLayout rl_back_arrow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_home);

        list_email = findViewById(R.id.list_email);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);
        rl_back_arrow = findViewById(R.id.rl_back_arrow);

        imgbtn_arrrow.setOnClickListener(this);
        rl_back_arrow.setOnClickListener(this);
        loadData();
    }

    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //------commented on 15th July
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_arrrow:
                Intent intent = new Intent(EmailHomeActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.rl_back_arrow:
                Intent intent_rl_back_arrow = new Intent(EmailHomeActivity.this, HomeActivity.class);
//                intent_rl_back_arrow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_rl_back_arrow);
                break;
            default:
                break;
        }
    }

    //===========Code to get data from api using volley and load data to recycler view, starts==========
    public void loadData(){
        String url = Config.BaseUrlEpharma + "email/inbox/"+ userSingletonModel.getUser_id();
        final ProgressDialog loading = ProgressDialog.show(EmailHomeActivity.this, "Loading", "Please wait...", true, false);
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
            if(!emailModelArrayList.isEmpty()){
                emailModelArrayList.clear();
            }
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonData-=>",jsonObject.toString());
            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
            if(jsonObject1.getString("status").contentEquals("true")){
                /*ll_recycler.setVisibility(View.VISIBLE);
                tv_nodata.setVisibility(View.GONE);*/
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    EmailModel emailModel = new EmailModel();
                    emailModel.setId_mail_inbox(jsonObject2.getString("id_mail_inbox"));
                    emailModel.setFrom(jsonObject2.getString("from"));
                    emailModel.setSubject(jsonObject2.getString("subject"));
                    emailModel.setDate(jsonObject2.getString("date"));
                    emailModel.setRead_yn(jsonObject2.getString("read_yn"));
                    emailModel.setStatus(jsonObject2.getString("status"));
                    emailModel.setAttachment_yn(jsonObject2.getString("attachment_yn"));


                    emailModelArrayList.add(emailModel);

                }
                list_email.setAdapter(new CustomEmailAdapter(EmailHomeActivity.this, emailModelArrayList));
                list_email.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        id_mail_inbox = emailModelArrayList.get(i).getId_mail_inbox();
                        date = emailModelArrayList.get(i).getDate();

                        Intent intent_email_details = new Intent(EmailHomeActivity.this, EmailDetailsActivity.class);
                        intent_email_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_email_details);
                    }
                });
            }else if(jsonObject1.getString("status").contentEquals("false")){
                /*ll_recycler.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);
                tv_nodata.setText(jsonObject1.getString("message"));*/
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //===========Code to get data from api and load data to recycler view, ends==========

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent_circular_details = new Intent(EmailHomeActivity.this, HomeActivity.class);
//        intent_circular_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent_circular_details);
    }


}
