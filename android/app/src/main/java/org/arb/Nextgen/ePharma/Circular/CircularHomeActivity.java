package org.arb.Nextgen.ePharma.Circular;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Model.CircularListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.adapter.CustomCircularListAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CircularHomeActivity extends AppCompatActivity implements View.OnClickListener {
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<CircularListModel> circularListModelArrayList = new ArrayList<>();
    ListView list_circular;
    TextView tv_unread, tv_circular_caption;
    ImageButton imgbtn_arrrow;
    RelativeLayout rl_back_arrow;
    public static String circular_name, description, publish_date, attachment_file, attachment_download_link, id_circular;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circular_home);
        list_circular = findViewById(R.id.list_circular);
        tv_unread = findViewById(R.id.tv_unread);
        tv_circular_caption = findViewById(R.id.tv_circular_caption);
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

        //----commented on 15th July
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
                Intent intent = new Intent(CircularHomeActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.rl_back_arrow:
                Intent intent_bk_arrow = new Intent(CircularHomeActivity.this, HomeActivity.class);
//                intent_bk_arrow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_bk_arrow);
                break;
            default:
                break;
        }
    }
    //===========Code to get data from api using volley and load data to recycler view, starts==========
    public void loadData(){
//        String url = Config.BaseUrlEpharma + "circular/list/by-number/"+userSingletonModel.getUser_group_id()+"/1/"+ userSingletonModel.getUser_id();

        String url = Config.BaseUrlEpharma + "circular/list/by-number/"+userSingletonModel.getUser_group_id()+"/"+ userSingletonModel.getUser_id();
        Log.d("url-=>",url);
        final ProgressDialog loading = ProgressDialog.show(CircularHomeActivity.this, "Loading", "Please wait...", true, false);
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
            if(!circularListModelArrayList.isEmpty()){
                circularListModelArrayList.clear();
            }
            JSONObject jsonObject = new JSONObject(response);
//            tv_unread.setText("Unread ("+jsonObject.getString("unread_count")+")");
            JSONObject jsonObjectCount = jsonObject.getJSONObject("count");
//            tv_circular_caption.setText("Circular(s) - "+jsonObjectCount.getString("all"));
            tv_circular_caption.setText("Circulars ("+jsonObjectCount.getString("all")+")");
            tv_unread.setText("Unread ("+jsonObjectCount.getString("unread")+")");

            Log.d("jsonData-=>",jsonObject.toString());
            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
            if(jsonObject1.getString("status").contentEquals("true")){
                /*ll_recycler.setVisibility(View.VISIBLE);
                tv_nodata.setVisibility(View.GONE);*/
                JSONArray jsonArray = jsonObject.getJSONArray("circulars");
                Log.d("circularNo-=>",String.valueOf(jsonArray.length()));
//                tv_circular_caption.setText("Circular(s) - "+String.valueOf(jsonArray.length()));
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    CircularListModel circularListModel = new CircularListModel();
                    circularListModel.setId_circular(jsonObject2.getString("id_circular"));
                    circularListModel.setCircular_no(jsonObject2.getString("circular_no"));
                    circularListModel.setCircular_name(jsonObject2.getString("circular_name"));
                    circularListModel.setDescription(jsonObject2.getString("description"));
                    circularListModel.setPublish_date(jsonObject2.getString("publish_date"));
                    circularListModel.setExpire_date(jsonObject2.getString("expire_date"));
                    circularListModel.setAttachment_file(jsonObject2.getString("attachment_file"));
                    circularListModel.setAttachment_download_link(jsonObject2.getString("attachment_download_link"));
                    circularListModel.setRead_yn(jsonObject2.getString("read_yn"));

                    circularListModelArrayList.add(circularListModel);

                }
                list_circular.setAdapter(new CustomCircularListAdapter(CircularHomeActivity.this, circularListModelArrayList));
                list_circular.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        circular_name = circularListModelArrayList.get(i).getCircular_name();
                        id_circular = circularListModelArrayList.get(i).getId_circular();
                        description = circularListModelArrayList.get(i).getDescription();
                        publish_date = circularListModelArrayList.get(i).getPublish_date();
                        attachment_file = circularListModelArrayList.get(i).getAttachment_file();
                        attachment_download_link = circularListModelArrayList.get(i).getAttachment_download_link();

                        Intent intent_circular_details = new Intent(CircularHomeActivity.this, CircularDetailsActivity.class);
                        intent_circular_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_circular_details);
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
        Intent intent_circular_details = new Intent(CircularHomeActivity.this, HomeActivity.class);
//        intent_circular_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent_circular_details);
    }


}
