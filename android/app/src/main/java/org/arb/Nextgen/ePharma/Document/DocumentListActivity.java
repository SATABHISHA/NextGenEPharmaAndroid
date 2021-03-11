package org.arb.Nextgen.ePharma.Document;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.Login.LoginActivity;
import org.arb.Nextgen.ePharma.Model.DocumentListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.adapter.CustomDocumentListAdapter;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.RealPathUtil;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DocumentListActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton imgbtn_arrrow;
    ImageView img_view_upload;
    Button btn_dwnload_test;

    private static final int FILE_SELECT_CODE = 0;
    public String file = "";
    Uri contentUri;
    public String file_to_base64="";
    public String file_desc;

    ArrayList<DocumentListModel> documentListModelArrayList = new ArrayList<>();
    RecyclerView recycler_view;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

    LinearLayout ll_recycler;
    TextView tv_nodata;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentlist);
        imgbtn_arrrow = findViewById(R.id.imgbtn_arrrow);
        img_view_upload = findViewById(R.id.img_view_upload);
        ll_recycler = findViewById(R.id.ll_recycler);
        tv_nodata = findViewById(R.id.tv_nodata);
//        btn_dwnload_test = findViewById(R.id.btn_dwnload_test);
        recycler_view = findViewById(R.id.recycler_view);

        imgbtn_arrrow.setOnClickListener(this);
        img_view_upload.setOnClickListener(this);
//        btn_dwnload_test.setOnClickListener(this);

        //---checking condition for upload button
        Log.d("menutest-=>",userSingletonModel.getMenu_list());
//        img_view_upload.setVisibility(View.INVISIBLE);
        /*if(userSingletonModel.getMenu_list().contains("|upload_document|")){
            img_view_upload.setVisibility(View.VISIBLE);
        }*//*else{
            img_view_upload.setVisibility(View.INVISIBLE);
        }*/
        if(LoginActivity.chck_menulist_upload_document == 1){
            img_view_upload.setVisibility(View.VISIBLE);
        }else if(LoginActivity.chck_menulist_upload_document == 0){
            img_view_upload.setVisibility(View.INVISIBLE);
        }

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        loadData();

//        requestMultiplePermissions();  //---commenting on 7th july

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); //---calling broadcast reciver to get notification on download completing
    }

    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();

        //------commented on 15th July
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
        switch (v.getId()) {
            case R.id.imgbtn_arrrow:
                Intent intent = new Intent(DocumentListActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.img_view_upload:
//                showFileChooser();
                loadPopupForAddDocumentDescription();
                break;
           /* case R.id.btn_dwnload_test:
//                downloadFileDemoTesting();
                downloadFileDemoTesting1();
                break;*/
        }
    }

    //=============upload document data to server, starts===========
    public void uploadDocumentDetails(String file_data, String file_name, String file_desc, String file_ext, Double file_size, String file_upload_date, String file_upload_by, int cal_year_id){
//        final String URL = "http://192.168.10.175:9006/api/Timesheet/HolidayList";
        final String URL = Config.BaseUrlEpharma+"documents/upload";
// Post params to be sent to the server

        final JSONObject DocumentElementobj = new JSONObject();
        try{
           /* DocumentElementobj.put("file", "gst-inc-101");
            DocumentElementobj.put("name", "test 1.xlsx");
            DocumentElementobj.put("description", "test excel file");
            DocumentElementobj.put("extension", "xlsx");
            DocumentElementobj.put("size", 24.0);
            DocumentElementobj.put("upload_date", "2020-03-06");
            DocumentElementobj.put("upload_by", "dip2015");
            DocumentElementobj.put("cal_year_id", 12);*/

            DocumentElementobj.put("file", file_data);
            DocumentElementobj.put("name", file_name);
            DocumentElementobj.put("description", file_desc);
            DocumentElementobj.put("extension", file_ext);
            DocumentElementobj.put("size", file_size);
            DocumentElementobj.put("upload_date", file_upload_date);
            DocumentElementobj.put("upload_by", file_upload_by);
            DocumentElementobj.put("cal_year_id", cal_year_id);
            Log.d("jsontest-=>",DocumentElementobj.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(DocumentElementobj.toString()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("SubmitResponse-=>",response.toString());
                            //Process os success response
                            JSONObject jsonObj = null;
                            try{
                               /* String response1 = response.toString();
                                jsonObj = XML.toJSONObject(response1);
                                String responseData = response.toString();
                                String val = "";
                                JSONObject resobj = new JSONObject(responseData);
                                Log.d("getData",resobj.toString());

                                Iterator<?> keys = resobj.keys();
                                while(keys.hasNext() ) {
                                    String key = (String) keys.next();
                                    if (resobj.get(key) instanceof JSONObject) {
                                        JSONObject xx = new JSONObject(resobj.get(key).toString());
                                        val = xx.getString("content");
                                    }


                                }*/
                               JSONObject jsonObject = new JSONObject(response.toString());
                               if(jsonObject.get("status").equals("true")){
                                   View v = findViewById(R.id.cordinatorLayout);
                                   new Snackbar(jsonObject.get("message").toString(),v, Color.parseColor("#ffffff"));
                                   loadData(); //---to refresh content of recycler listview
                               }else if(jsonObject.get("status").equals("false")){
                                   View v = findViewById(R.id.cordinatorLayout);
                                   new Snackbar(jsonObject.get("message").toString(),v, Color.parseColor("#ffffff"));
                               }else{
                                   View v = findViewById(R.id.cordinatorLayout);
                                   new Snackbar("Error in Download",v, Color.parseColor("#ffffff"));
                               }

                            }catch (Exception e){
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
            }
        });

// add the request object to the queue to be executed
        RequestQueue requestQueue = Volley.newRequestQueue(DocumentListActivity.this);
        requestQueue.add(request_json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //=============upload document data to server, ends===========

    //===============File chooser/upload, code starts==============
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    contentUri = data.getData();
                    Log.d("File Uri: ", uri.toString());
//                    file = uri.toString();
                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
//                    filePath.setText(uri.toString());

                    String file_path = RealPathUtil.getRealPath(DocumentListActivity.this,uri);
                    Log.d("demoTest-=>",file_path);
                    String file_to_base64 = getBase64FromPath(file_path);
                    Log.d("demoTest1Base64-=>",file_to_base64);
//                    Toast.makeText(getApplicationContext(),file_to_base64,Toast.LENGTH_LONG).show();

                    //--get file size, code starts
                    File file = new File(file_path);
                    Double file_size = Double.parseDouble(String.valueOf(file.length()/1024));
                    Log.d("filesize-=>",String.valueOf(file_size));
                    //--get file size, code ends

                    //-----get file extension/name, code starts
                    String fileName = file.getName();
                    String file_ext = "";
                    if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                        file_ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                    }
                    Log.d("file_ext-=>",file_ext);
                    Log.d("file_name-=>",fileName);
                    //-----get file extension/name, code ends

                    //---get current date
                    String upload_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    Log.d("upload_date-=>",upload_date);

                    uploadDocumentDetails(file_to_base64,fileName,file_desc, file_ext,file_size,upload_date,userSingletonModel.getUser_name(),Integer.parseInt(userSingletonModel.getCalendar_id()));


                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //----code to convert file to base64(5th march 2020), starts-----
    public String getBase64FromPath(String path) {
        String base64 = "";
        try {/*from   w w w .  ja  va  2s  .  c om*/
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }
    //----code to convert file to base64(5th march 2020), ends-----

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //---commenting on 7th july as it crashes sometimes
    /*private void requestMultiplePermissions() {
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
    }*/


    public void shareFile() {
//        Uri fileUri = Uri.parse(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentUri.toString()));

        shareIntent.setType("application/*");
//        shareIntent.setDataAndType(contentUri,"application/*");
        Log.d("Testing-=->", contentUri.toString());
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Sharing file..."));
        startActivity(shareIntent);

    }
    //===============File chooser/upload, code ends================

    //=========Code to download file from url, starts=======
    public void downloadFileDemoTesting() {
        String mUrl = "http://192.168.10.175:9005/CompanyData/CompanyDoc/PAYROLL_713_98450766_Reimbursement%20claim.xlsx";
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        try {
                            if (response != null) {

                                FileOutputStream outputStream;
//                                String name=<FILE_NAME_WITH_EXTENSION e.g reference.txt>;
                                String name = "Caplet.xlsx";
                                outputStream = openFileOutput(name, Context.MODE_PRIVATE);
                                outputStream.write(response);
                                outputStream.close();
                                Toast.makeText(DocumentListActivity.this, "Download complete.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);
    }


    public void downloadFileDemoTesting1() {
        String fileUrl = "http://192.168.10.175:9005/CompanyData/CompanyDoc/PAYROLL_713_98450766_Reimbursement%20claim.xlsx";
        String fileName = "caplet";

        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);

// concatinate above fileExtension to fileName
        fileName += "." + fileExtension;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl))
                .setTitle(this.getString(R.string.app_name))
                .setDescription("Downloading " + fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }


    public void downloadFileDemoTesting2() {
     /*   try {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_), "myfolder");


            if (!imageStorageDir.exists()) {
//noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
            }

// default image extension
            String imgExtension = ".jpg";

            if (image_uri.toString().contains(".gif"))
                imgExtension = ".gif";
            else if (image_uri.toString().contains(".png"))
                imgExtension = ".png";
            else if (image_uri.toString().contains(".3gp"))
                imgExtension = ".3gp";

            String date = DateFormat.getDateTimeInstance().format(new Date());
            String file = getString(R.string.app_name) + "-image-" + date.replace(" ", "").replace(":", "").replace(".", "") + imgExtension;
        }*/
        //=========Code to download file from url, ends=======

    }


    //===========Code to get data from api using volley and load data to recycler view, starts==========
    public void loadData(){
        String url = Config.BaseUrlEpharma + "documents/list" ;
        final ProgressDialog loading = ProgressDialog.show(DocumentListActivity.this, "Loading", "Please wait...", true, false);
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
            if(!documentListModelArrayList.isEmpty()){
                documentListModelArrayList.clear();
            }
            JSONObject jsonObject = new JSONObject(response);
            Log.d("jsonData-=>",jsonObject.toString());
            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
            if(jsonObject1.getString("status").contentEquals("true")){
                ll_recycler.setVisibility(View.VISIBLE);
                tv_nodata.setVisibility(View.GONE);
                JSONArray jsonArray = jsonObject.getJSONArray("documents");
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    DocumentListModel documentListModel = new DocumentListModel();
                    documentListModel.setDoc_name(jsonObject2.getString("name"));
                    documentListModel.setDescription(jsonObject2.getString("description"));
                    documentListModel.setExtension(jsonObject2.getString("extension"));
                    documentListModel.setDownload_link(jsonObject2.getString("download_link"));
                    documentListModel.setUpload_date(jsonObject2.getString("upload_date"));
                    documentListModel.setCal_year_id(jsonObject2.getString("cal_year_id"));
                    documentListModel.setSize(jsonObject2.getString("size"));
                    documentListModelArrayList.add(documentListModel);

                }
                recycler_view.setAdapter(new CustomDocumentListAdapter(DocumentListActivity.this, documentListModelArrayList));
            }else if(jsonObject1.getString("status").contentEquals("false")){
                ll_recycler.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);
                tv_nodata.setText(jsonObject1.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //===========Code to get data from api and load data to recycler view, ends==========


    //======Code for broadcast receiver, starts-----
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (CustomDocumentListAdapter.idDownLoad == id) {
                View v = findViewById(R.id.cordinatorLayout);
//                CustomDocumentListAdapter.tv_download.setVisibility(View.GONE);
                loadData();
                CustomDocumentListAdapter.loading.dismiss();
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

    //==========dialog popup for document details before upload, code starts=======
    public void loadPopupForAddDocumentDescription(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialog = li.inflate(R.layout.dialog_upload_documentlist, null);
        final EditText editText = (EditText) dialog.findViewById(R.id.ed_desc);
        RelativeLayout rl_cancel = dialog.findViewById(R.id.rl_cancel);
        RelativeLayout rl_skip_proceed = dialog.findViewById(R.id.rl_skip_proceed);
        final TextView tv_skip_proceed = dialog.findViewById(R.id.tv_skip_proceed);
//        RelativeLayout rl_proceed = dialog.findViewById(R.id.rl_proceed);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(dialog);
        alert.setCancelable(false);
        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

         editText.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 if(editText.getText().equals("")){
                     tv_skip_proceed.setText("Skip");
                 }
             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if(editText.getText().toString().trim().equals("")){
                     //write your code here
                     tv_skip_proceed.setText("Skip");
                 }else{
                     tv_skip_proceed.setText("Proceed");
                 }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });

        //---newly added on 8th dec to add the condition check for editable/non editable mode edittext code ends----
        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        rl_skip_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_desc = editText.getText().toString();
                showFileChooser();
                alertDialog.dismiss();
            }
        });
        /*rl_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });*/
    }
    //==========dialog popup for document details before upload, code ends=======

}
