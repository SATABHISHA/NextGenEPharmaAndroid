package org.arb.Nextgen.ePharma.Email;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmailDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_subject, tv_date, tv_description, tv_attachment_file, tv_attachment_file2, tv_attachment_file3, tv_attachment_file4;
    ImageButton imgbtn_dwnld, imgbtn_dwnld2, imgbtn_dwnld3, imgbtn_dwnld4;;
    Button btn_back;
    public static long idDownLoad = 0;
    DownloadManager dm;
    public static ProgressDialog loading;
    Date date;
    LinearLayout ll_line1, ll_line2, ll_line3;
    RelativeLayout rl_attachment1, rl_attachment2, rl_attachment3, rl_attachment4;
    public String attachment_file1_download_link, attachment_file2_download_link, attachment_file3_download_link, attachment_file4_download_link;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_details);

        tv_subject = findViewById(R.id.tv_subject);
        tv_date = findViewById(R.id.tv_date);
        tv_description = findViewById(R.id.tv_description);
        tv_attachment_file = findViewById(R.id.tv_attachment_file);
        tv_attachment_file2 = findViewById(R.id.tv_attachment_file2);
        tv_attachment_file3 = findViewById(R.id.tv_attachment_file3);
        tv_attachment_file4 = findViewById(R.id.tv_attachment_file4);

        rl_attachment1 = findViewById(R.id.rl_attachment1);
        rl_attachment2 = findViewById(R.id.rl_attachment2);
        rl_attachment3 = findViewById(R.id.rl_attachment3);
        rl_attachment4 = findViewById(R.id.rl_attachment4);

        imgbtn_dwnld = findViewById(R.id.imgbtn_dwnld);
        imgbtn_dwnld2 = findViewById(R.id.imgbtn_dwnld2);
        imgbtn_dwnld3 = findViewById(R.id.imgbtn_dwnld3);
        imgbtn_dwnld4 = findViewById(R.id.imgbtn_dwnld4);

        btn_back = findViewById(R.id.btn_back);

        ll_line1 = findViewById(R.id.ll_line1);
        ll_line2 = findViewById(R.id.ll_line2);
        ll_line3 = findViewById(R.id.ll_line3);

        imgbtn_dwnld.setOnClickListener(this);
        imgbtn_dwnld2.setOnClickListener(this);
        imgbtn_dwnld3.setOnClickListener(this);
        imgbtn_dwnld4.setOnClickListener(this);

        btn_back.setOnClickListener(this);

        loadData();

//        requestMultiplePermissions(); //---commenting on 7th july

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); //---calling broadcast reciver to get notification on download completing

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

    public void makeReadusingApi(){
        String url = Config.BaseUrlEpharma + "email/set-as-read/"+ EmailHomeActivity.id_mail_inbox;
        final ProgressDialog loading = ProgressDialog.show(EmailDetailsActivity.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("responseData-=>", response);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
    public void loadData(){
        makeReadusingApi(); //--calling function to make the mail read(if it is unread)
        String url = Config.BaseUrlEpharma + "email/detail/inbox/"+ EmailHomeActivity.id_mail_inbox;
        final ProgressDialog loading = ProgressDialog.show(EmailDetailsActivity.this, "Loading", "Please wait...", true, false);
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
            Log.d("responseData-=>",response);
            tv_subject.setText(jsonObject.getString("subject"));

            //-------DateFormat code starts---------
            String date_output="";
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                date = originalFormat.parse(EmailHomeActivity.date);
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                date_output = outputFormat.format(date);
            }catch (Exception e){
                e.printStackTrace();
            }
            //-------DateFormat code ends---------
            tv_date.setText(date_output);
            tv_description.setText(jsonObject.getString("mail_body"));

            if(!jsonObject.getString("attachment_file1").trim().contentEquals("")){
                ll_line1.setVisibility(View.VISIBLE);
                rl_attachment1.setVisibility(View.VISIBLE);
                tv_attachment_file.setText(jsonObject.getString("attachment_file1"));
                attachment_file1_download_link = jsonObject.getString("attachment_file1_download_link");
            }else{
                ll_line1.setVisibility(View.GONE);
                rl_attachment1.setVisibility(View.GONE);
            }
            if(!jsonObject.getString("attachment_file2").trim().contentEquals("")){
                ll_line2.setVisibility(View.VISIBLE);
                rl_attachment2.setVisibility(View.VISIBLE);
                tv_attachment_file2.setText(jsonObject.getString("attachment_file2"));
                attachment_file2_download_link = jsonObject.getString("attachment_file2_download_link");
            }else{
                ll_line2.setVisibility(View.GONE);
                rl_attachment2.setVisibility(View.GONE);
            }
            if(!jsonObject.getString("attachment_file3").trim().contentEquals("")){
                ll_line3.setVisibility(View.VISIBLE);
                rl_attachment3.setVisibility(View.VISIBLE);
                tv_attachment_file3.setText(jsonObject.getString("attachment_file3"));
                attachment_file3_download_link = jsonObject.getString("attachment_file3_download_link");
            }else{
                ll_line3.setVisibility(View.GONE);
                rl_attachment3.setVisibility(View.GONE);
            }
            if(!jsonObject.getString("attachment_file4").trim().contentEquals("")){
                rl_attachment4.setVisibility(View.VISIBLE);
                tv_attachment_file4.setText(jsonObject.getString("attachment_file4"));
                attachment_file4_download_link = jsonObject.getString("attachment_file4_download_link");
            }else{
                rl_attachment4.setVisibility(View.GONE);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.imgbtn_dwnld:
               downloadFile(attachment_file1_download_link);
               break;
           case R.id.imgbtn_dwnld2:
               downloadFile(attachment_file2_download_link);
               break;
           case R.id.imgbtn_dwnld3:
               downloadFile(attachment_file3_download_link);
               break;
           case R.id.imgbtn_dwnld4:
               downloadFile(attachment_file4_download_link);
               break;
           case R.id.btn_back:
               Intent intent_email_details = new Intent(EmailDetailsActivity.this, EmailHomeActivity.class);
               intent_email_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent_email_details);
               break;
           default:
               break;
       }
    }

    //--------code to download file, create destination folder, starts-----
    public void downloadFile(String download_link){
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Caplet");
                  /*  if (direct.exists()) {
                        if (direct.isDirectory()) {
                            String[] children = direct.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(direct, children[i]).delete();
                            }
                        }
                    }*/
        if (!direct.exists()) {
            direct.mkdirs();
        }
        try {
            String fileUrl = download_link;
//                    String fileName = "caplet";
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);

            // concatinate above fileExtension to fileName
            fileName += "." + fileExtension;

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl))
                    .setTitle(this.getString(R.string.app_name))
                    .setDescription("Downloading " + fileName)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    .setDestinationInExternalPublicDir("/Caplet", fileName);
            dm = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);
//                    dm.enqueue(request);
            idDownLoad = dm.enqueue(request);

                       /* View v1 = itemView.findViewById(R.id.cordinatorLayout);
                        new Snackbar("Downloading",v1, Color.parseColor("#ffffff"));*/

//                        tv_download.setVisibility(View.VISIBLE);
            loading = ProgressDialog.show(this, "Downloading...", "Please wait while downloading document", false, false);


//                        Toast.makeText(context.getApplicationContext(),"Downloading",Toast.LENGTH_LONG).show();
        }catch (Error e){
            View v1 = findViewById(R.id.cordinatorLayout);
            new Snackbar("Download Error",v1, Color.parseColor("#ffffff"));
            loading.dismiss();
        }
    }
    //--------code to download file, create destination folder, ends-----


    //======Code for broadcast receiver, starts-----
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (idDownLoad == id) {
                View v = findViewById(R.id.cordinatorLayout);
                loading.dismiss();
                new Snackbar("Download Completed",v, Color.parseColor("#ffffff"));
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
    //======Code for broadcast receiver, ends-----


    //--------function to request for permission, starts------
    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    //--------function to request for permission, ends------


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
