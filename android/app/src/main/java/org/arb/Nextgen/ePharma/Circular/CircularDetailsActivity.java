package org.arb.Nextgen.ePharma.Circular;

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

import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CircularDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_circular_name, tv_publish_date, tv_attachment_file, tv_description;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ImageButton imgbtn_dwnld;
    Button btn_back;
    public static long idDownLoad = 0;
    DownloadManager dm;
    public static ProgressDialog loading;
    Date date;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circular_details);

        tv_circular_name = findViewById(R.id.tv_circular_name);
        tv_publish_date = findViewById(R.id.tv_publish_date);
        tv_attachment_file = findViewById(R.id.tv_attachment_file);
        imgbtn_dwnld = findViewById(R.id.imgbtn_dwnld);
        tv_description = findViewById(R.id.tv_description);
        btn_back = findViewById(R.id.btn_back);

        tv_circular_name.setText(CircularHomeActivity.circular_name);
//        tv_publish_date.setText(CircularHomeActivity.publish_date);
        tv_attachment_file.setText(CircularHomeActivity.attachment_file);
        tv_description.setText(CircularHomeActivity.description);

        //-------DateFormat code starts---------
        String date_output="";
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = originalFormat.parse(CircularHomeActivity.publish_date);
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            date_output = outputFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        //-------DateFormat code ends---------
        tv_publish_date.setText(date_output);

        imgbtn_dwnld.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        makeReadusingApi(); //---calling function to make circular read(if it is unread)

//        requestMultiplePermissions();  //---commented on 7th july as it crashing sometimes
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); //---calling broadcast reciver to get notification on download completing
    }

    public void makeReadusingApi(){
        String url = Config.BaseUrlEpharma + "circular/set-as-read/"+ userSingletonModel.getUser_id()+"/"+CircularHomeActivity.id_circular;
        final ProgressDialog loading = ProgressDialog.show(CircularDetailsActivity.this, "Loading", "Please wait...", true, false);
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
         switch (view.getId()){
             case R.id.imgbtn_dwnld:
                 //--------code to download file, create destination folder, starts-----
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
                     String fileUrl = CircularHomeActivity.attachment_download_link;
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
                //--------code to download file, create destination folder, ends-----
                 break;
             case R.id.btn_back:
                 Intent intent_circular_details = new Intent(CircularDetailsActivity.this, CircularHomeActivity.class);
                 intent_circular_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent_circular_details);
             default:
                 break;
         }
    }

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
