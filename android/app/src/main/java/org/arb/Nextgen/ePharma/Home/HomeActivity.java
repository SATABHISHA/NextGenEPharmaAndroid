package org.arb.Nextgen.ePharma.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import org.arb.Nextgen.ePharma.Circular.CircularHomeActivity;
import org.arb.Nextgen.ePharma.Customer.CustomerHomeActivity;
import org.arb.Nextgen.ePharma.Document.DocumentListActivity;
import org.arb.Nextgen.ePharma.Email.EmailHomeActivity;
import org.arb.Nextgen.ePharma.Login.LoginActivity;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.TrackingDetails.TrackingDetailsActivity;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.ConnectivityReceiver;
import org.arb.Nextgen.ePharma.config.MyApplication;
import org.arb.Nextgen.ePharma.config.RSSPullService;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.arb.Nextgen.ePharma.DCR.DcrHome;
import org.arb.Nextgen.ePharma.MWR.MWRHome;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    CoordinatorLayout coordinatorLayout;
    LocationManager locationManager;
    RelativeLayout rlDcr, rlMwr, rlDcrSub, rlMwrSub, rlDcrAll, rlMwrAll, rlDocuments, rlCircular, rlEmails, rlTracking, rl_circulars, rl_circulars1, rlCustomers;
    public static int cancelStatus = 0;
    public static String circular_count = "0";
    NavigationView navigationView;
    TextView tv_circular_count, tvCircular;

    //------variable for version update, code starts
    private AppUpdateManager mAppUpdateManager;
    private int RC_APP_UPDATE = 999;
    private int inAppUpdateType;
    private com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
    private InstallStateUpdatedListener installStateUpdatedListener;
    //------variable for version update, code ends


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epharma_home);

//        Toast.makeText(getApplicationContext(),userSingletonModel.getDesignation_name(),Toast.LENGTH_LONG).show();

        rlDcr = (RelativeLayout)findViewById(R.id.rlDcr);
        rlMwr = findViewById(R.id.rlMwr);
        rlDcrSub = findViewById(R.id.rlDcrSub);
        rlMwrSub = findViewById(R.id.rlMwrSub);
        rlDcrAll = findViewById(R.id.rlDcrAll);
        rlMwrAll = findViewById(R.id.rlMwrAll);
        rlDocuments = findViewById(R.id.rlDocuments);
        rlCircular = findViewById(R.id.rlCircular);
        rlEmails = findViewById(R.id.rlEmails);
        rlTracking = findViewById(R.id.rlTracking);
        tv_circular_count = findViewById(R.id.tv_circular_count);
        tvCircular = findViewById(R.id.tvCircular);
        rl_circulars = findViewById(R.id.rl_circulars);
        rl_circulars1 = findViewById(R.id.rl_circulars1);
        rlCustomers = findViewById(R.id.rlCustomers);

        rlDcr.setOnClickListener(this);
        rlMwr.setOnClickListener(this);
        rlDcrSub.setOnClickListener(this);
        rlMwrSub.setOnClickListener(this);
        rlDcrAll.setOnClickListener(this);
        rlMwrAll.setOnClickListener(this);
        rlDocuments.setOnClickListener(this);
        rlCircular.setOnClickListener(this);
        rlEmails.setOnClickListener(this);
        rlTracking.setOnClickListener(this);
        rlCustomers.setOnClickListener(this);


        //============Navigation drawer and toolbar code starts=============
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("ePharma Dashboard");
        setSupportActionBar(toolbar);
        checkRelativeLayoutVisibleOrInvisible();

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cordinatorLayout);

        TextView textUserName = (TextView)header.findViewById(R.id.text_username);
//        textUserName.setText(userSingletonModel.getEmpName());
//        textUserName.setText("Sudip Laha");
        textUserName.setText(userSingletonModel.getUser_full_name());

        TextView textCompanyName = (TextView)header.findViewById(R.id.text_compname);
//        textCompanyName.setText(userSingletonModel.getCompanyName());
        textCompanyName.setText("Caplet India");
        navigationView.setNavigationItemSelectedListener(this);

        hide_display_navigation_menu_items(); //---calling function to hide/display navigation drawer menu items

        //==============Navigation drawer and toolbar code ends=============


//        startService(new Intent(this.getApplication(), RSSPullService.class));  //calling function to start the background service

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(HomeActivity.this, RSSPullService.class));
        }else{
            startService(new Intent(this, RSSPullService.class));
        }
//        checkConnection();//----function calling to check the internet connection


        //----Using thread to check Internet connection
        final Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isConnected = ConnectivityReceiver.isConnected();
                                if (isConnected == false){
                                    View v = findViewById(R.id.cordinatorLayout);
                                    new Snackbar("Please connect to the iternet",v,Color.parseColor("#ffffff"));
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();

        //---added on 15th July, code starts
        final LocationManager manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
        //---added on 15th July, code ends

        //----added on 20th July for version update, starts----
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        //lambda operation used for below listener
        //For flexible update
        installStateUpdatedListener = installState -> {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        };
        mAppUpdateManager.registerListener(installStateUpdatedListener);

        //For Flexible
        inAppUpdateType = AppUpdateType.FLEXIBLE;//1
        inAppUpdate();
        //----added on 20th July for version update, ends----
    }

    //-------added on 20th July code for version update, starts----


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            //when user clicks update button
            if (resultCode == RESULT_OK) {
                Toast.makeText(HomeActivity.this, "App download starts...", Toast.LENGTH_LONG).show();
            } else if (resultCode != RESULT_CANCELED) {
                //if you want to request the update again just call checkUpdate()
                Toast.makeText(HomeActivity.this, "App download canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
                Toast.makeText(HomeActivity.this, "App download failed.", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onDestroy();
    }

    private void inAppUpdate() {

        try {
            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // For a flexible update, use AppUpdateType.FLEXIBLE
                            && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)) {
                        // Request the update.

                        try {
                            mAppUpdateManager.startUpdateFlowForResult(
                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                    appUpdateInfo,
                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                    inAppUpdateType,
                                    // The current activity making the update request.
                                    HomeActivity.this,
                                    // Include a request code to later monitor this update request.
                                    RC_APP_UPDATE);
                        } catch (IntentSender.SendIntentException ignored) {

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void popupSnackbarForCompleteUpdate() {
        try {
            com.google.android.material.snackbar.Snackbar snackbar =
                    com.google.android.material.snackbar.Snackbar.make(
                            findViewById(R.id.cordinatorLayout),
                            "An update has just been downloaded.\nRestart to update",
                            com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("INSTALL", view -> {
                if (mAppUpdateManager != null){
                    mAppUpdateManager.completeUpdate();
                }
            });
            snackbar.setActionTextColor(Color.parseColor("#ffffff"));
            snackbar.show();

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("An update has just been downloaded.\nRestart to update")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.completeUpdate();
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/
    }
    //-------added on 20th July code for version update, ends----


    //---added on 6th July, code starts----
    @Override
    protected void onRestart() {
        super.onRestart();
        //---for version update , added on 20th july
       /* inAppUpdateType = AppUpdateType.FLEXIBLE;//1
        inAppUpdate();*/
       recreate();

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

    public void hide_display_navigation_menu_items(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        //---added on 6th july as per client requirements, code starts------
        String designation_names = "MSR/LC/BE/ABM/ZBM/RBM";
        if (userSingletonModel.getDesignation_name().contains("MSR") || userSingletonModel.getDesignation_name().contains("LC")
            || userSingletonModel.getDesignation_name().contains("BE") || userSingletonModel.getDesignation_name().contains("ABM")
            || userSingletonModel.getDesignation_name().contains("ZBM") || userSingletonModel.getDesignation_name().contains("RBM")){
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        }else {
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
        }
        //---added on 6th july as per client requirements, code ends------
        if(userSingletonModel.getMenu_list().contains("|dcr|")){
            nav_Menu.findItem(R.id.nav_dcr).setVisible(true);
        }else{
            nav_Menu.findItem(R.id.nav_dcr).setVisible(false);
        }
        if(userSingletonModel.getMenu_list().contains("|mwr|")){
            nav_Menu.findItem(R.id.nav_mwr).setVisible(true);
        }else{
            nav_Menu.findItem(R.id.nav_mwr).setVisible(false);
        }
        if(userSingletonModel.getMenu_list().contains("|circulars|")){
//            nav_Menu.findItem(R.id.nav_circular).setVisible(true);
            nav_Menu.findItem(R.id.nav_circular).setVisible(true); //--as this section is not completed thats why it's visibility is made false
        }else{
            nav_Menu.findItem(R.id.nav_circular).setVisible(false);
        }
        if(userSingletonModel.getMenu_list().contains("|documents|")){
//            nav_Menu.findItem(R.id.nav_documents).setVisible(true);
            nav_Menu.findItem(R.id.nav_documents).setVisible(true);
        }else{
            nav_Menu.findItem(R.id.nav_documents).setVisible(true);
        }
        if(userSingletonModel.getMenu_list().contains("|emails|")){
            nav_Menu.findItem(R.id.nav_email).setVisible(true);
        }else{
            nav_Menu.findItem(R.id.nav_email).setVisible(true);
        }
    }

    //===============Navigation drawer on Selecting the items, code starts==============
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }else if (id == R.id.nav_dcr){
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Coming Soon")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();*/
            boolean isConnected = ConnectivityReceiver.isConnected();
            if (isConnected == false){
                View v1 = findViewById(R.id.cordinatorLayout);
                new Snackbar("Please connect to the iternet",v1,Color.parseColor("#ffffff"));
            }else {
                getCircularCount();

                //--added on 15th jan as per discussion, code starts
                Intent intent = new Intent(HomeActivity.this, DcrHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //--added on 15th jan as per discussion, code ends

                /*if (Double.parseDouble(circular_count) == 0) {
                    Intent intent = new Intent(HomeActivity.this, DcrHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
//                    Toast.makeText(getApplicationContext(),"Please see unread circulars first",Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Cannot enter DCR since you have "+ circular_count+" un-read Circular(s). Please read them first and then try again.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }*/ //--commented on 15th jan as per discussion
//                startActivity(new Intent(HomeActivity.this, DcrHome.class));
            }

        }else if (id == R.id.nav_mwr){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Coming Soon")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }else if (id == R.id.nav_documents){
          /*  AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Coming Soon")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show(); */

            startActivity(new Intent(HomeActivity.this, DocumentListActivity.class));

        }else if (id == R.id.nav_circular){
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Coming Soon")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();*/
            startActivity(new Intent(HomeActivity.this, CircularHomeActivity.class));
        }else if(id == R.id.nav_email){
            startActivity(new Intent(HomeActivity.this, EmailHomeActivity.class));
        }
        /*else if (id == R.id.nav_vacation_request){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Coming Soon")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }*/ else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            removeSharedPref();
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
//                            HomeActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
           /* WindowManager.LayoutParams lp = alert.getWindow().getAttributes();
            lp.dimAmount=0.7f;
            alert.getWindow().setAttributes(lp);
            alert.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);*/

        } else if(id == R.id.nav_change_pswd){
            //--------adding custom dialog on 14th may starts------
            LayoutInflater li2 = LayoutInflater.from(this);
            View dialog = li2.inflate(R.layout.dialog_change_password, null);
            final EditText ed_current_password = dialog.findViewById(R.id.ed_current_password);
            final EditText edt_new_password = dialog.findViewById(R.id.edt_new_password);
            final EditText edt_retype_password = dialog.findViewById(R.id.edt_retype_password);
            final TextView tv_pswd_chk = dialog.findViewById(R.id.tv_pswd_chk);
            final TextView tv_submit = dialog.findViewById(R.id.tv_submit);
            RelativeLayout rl_cancel = dialog.findViewById(R.id.rl_cancel);
            final RelativeLayout rl_submit = dialog.findViewById(R.id.rl_submit);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setView(dialog);
//                        alert.setCancelable(false);
            //Creating an alert dialog
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();
            rl_submit.setClickable(false);
//            tv_submit.setAlpha(0.5f);
            rl_submit.setAlpha(0.5f);
            rl_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                }
            });
            edt_retype_password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(edt_new_password.getText().toString().contentEquals(charSequence)){
                        tv_pswd_chk.setVisibility(View.VISIBLE);
                        tv_pswd_chk.setTextColor(Color.parseColor("#00AE00"));
                        tv_pswd_chk.setText("Correct Password");
//                        tv_submit.setAlpha(1.0f);
                        rl_submit.setAlpha(1.0f);
                        rl_submit.setClickable(true);
                    }else {
                        tv_pswd_chk.setVisibility(View.VISIBLE);
                        tv_pswd_chk.setTextColor(Color.parseColor("#AE0000"));
                        rl_submit.setClickable(false);
//                        tv_submit.setAlpha(0.5f);
                        rl_submit.setAlpha(0.5f);
                        tv_pswd_chk.setText("Incorrect Password");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            rl_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ed_current_password.getText().toString().contentEquals("") || edt_retype_password.getText().toString().contentEquals("") ){
                        //----to display message in snackbar, code starts
                        String message_notf = "Field cannot be left blank";
                        int color = Color.parseColor("#FFFFFF");
                        View v1 = findViewById(R.id.cordinatorLayout);
                        new Snackbar(message_notf,v1,color);
                        //----to display message in snackbar, code ends
                    }else{
//                        changePswd(ed_current_password.getText().toString(),edt_new_password.getText().toString(),ed_password_hint.getText().toString());
                        change_password(ed_current_password.getText().toString(),edt_new_password.getText().toString());
                        alertDialog.dismiss();
                    }
                }
            });
        }
        /* else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //===============Navigation drawer on Selecting the items, code ends==============


    //============function to change password, code starts========
      public void change_password(String old_pswd, String new_pswd){
        try {
            final JSONObject DocumentElementobj = new JSONObject();
            DocumentElementobj.put("id_user", Integer.parseInt(userSingletonModel.getUser_id()));
            DocumentElementobj.put("old_pwd", old_pswd);
            DocumentElementobj.put("new_pwd", new_pswd);

            Log.d("jsonObjectTest",DocumentElementobj.toString());
            final String URL = Config.BaseUrlEpharma+"user/changePassword";

            JsonObjectRequest request_json = null;
            request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(DocumentElementobj.toString()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Process os success response
                                JSONObject jsonObj = null;
                                try{
                                    String responseData = response.toString();
                                    JSONObject resobj = new JSONObject(responseData);
                                    JSONObject jsonObject = resobj.getJSONObject("response");
                                    Log.d("getData",resobj.toString());

                                    if(jsonObject.getString("status").contentEquals("1")){
//                                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                        //---------Alert dialog code starts(added on 21st nov)--------
                                        final AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
                                        alertDialogBuilder.setMessage(jsonObject.getString("message"));
                                        alertDialogBuilder.setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        //-----following code is commented on 6th dec to get the calender saved state data------
                                                        alertDialogBuilder.setCancelable(true);
                                                    }
                                                });
                                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();

                                        //--------Alert dialog code ends--------
                                    }else{
//                                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                        //---------Alert dialog code starts(added on 21st nov)--------
                                        final AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
                                        alertDialogBuilder.setMessage(jsonObject.getString("message"));
                                        alertDialogBuilder.setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        //-----following code is commented on 6th dec to get the calender saved state data------
                                                        alertDialogBuilder.setCancelable(true);
                                                    }
                                                });
                                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();

                                        //--------Alert dialog code ends--------
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
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request_json);
        }catch (JSONException e){
            e.printStackTrace();
        }

      }
    //============function to change password, code ends========


    //===============function to cherck relativeLayout visible/invisible, code starts=====
    public void checkRelativeLayoutVisibleOrInvisible(){
        if(userSingletonModel.getMenu_list().contains("|dcr|")){
            rlDcr.setVisibility(View.VISIBLE);
        }if(userSingletonModel.getMenu_list().contains("|mwr|")){
            rlMwr.setVisibility(View.VISIBLE);
        }if(userSingletonModel.getMenu_list().contains("|dcr_sub|")){
//           rlDcrSub.setVisibility(View.VISIBLE);
            rlDcrSub.setVisibility(View.GONE);
        }if(userSingletonModel.getMenu_list().contains("|mwr_sub|")){
//           rlMwrSub.setVisibility(View.VISIBLE);
            rlMwrSub.setVisibility(View.GONE);
        }if(userSingletonModel.getMenu_list().contains("|dcr_report|")){
            rlDcrAll.setVisibility(View.VISIBLE);
        }if(userSingletonModel.getMenu_list().contains("|mwr_report|")){
            rlMwrAll.setVisibility(View.VISIBLE);
        }if(userSingletonModel.getMenu_list().contains("|documents|")){
            rlDocuments.setVisibility(View.VISIBLE);
//            rlDocuments.setVisibility(View.GONE);//---as this section is not completed that's why it's visibility is made "GONE"
        }if(userSingletonModel.getMenu_list().contains("|circulars|")){
           rlCircular.setVisibility(View.VISIBLE); //--uncommented 18th march
            rl_circulars.setVisibility(View.VISIBLE);
            rl_circulars1.setVisibility(View.GONE);
            getCircularCount(); //--added on 6th April 2020
//            rlCircular.setVisibility(View.GONE);
        }if(userSingletonModel.getMenu_list().contains("|emails|")){
           rlEmails.setVisibility(View.VISIBLE); //--uncommented on 20th march
//            rlEmails.setVisibility(View.GONE);
        }if(userSingletonModel.getMenu_list().contains("|track_msr|")){
            rlTracking.setVisibility(View.VISIBLE);
        }if(userSingletonModel.getMenu_list().contains("|upload_document|")){
            LoginActivity.chck_menulist_upload_document = 1;
        }if(!userSingletonModel.getMenu_list().contains("|upload_document|")){
            LoginActivity.chck_menulist_upload_document = 0;
        }
        Log.d("Menu String-=>",userSingletonModel.getMenu_list());
    }
    //===============function to cherck relativeLayout visible/invisible, code ends=====

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rlDcr:
                boolean isConnected = ConnectivityReceiver.isConnected();
                if (isConnected == false){
                    View v1 = findViewById(R.id.cordinatorLayout);
                    new Snackbar("Please connect to the iternet",v1,Color.parseColor("#ffffff"));
                }else {
                    getCircularCount();

                    //--added on 15th jan, code starts
                    Intent intent = new Intent(HomeActivity.this, DcrHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    //--added on 15th jan, code ends

                   /* if (Double.parseDouble(circular_count) == 0) {
                        Intent intent = new Intent(HomeActivity.this, DcrHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
//                startActivity(new Intent(HomeActivity.this, DcrHome.class));
                    }else{
//                        Toast.makeText(getApplicationContext(), "Please see unread circulars first",Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Cannot enter DCR since you have "+ circular_count+" un-read Circular(s). Please read them first and then try again.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }*/ //--commented on 15th jan as per discussion
                }
                break;
            case R.id.rlMwr:
               /* String message = "Work in progress";
                View v1 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message,v1,Color.parseColor("#ff0000"));*/
                boolean isConnected1 = ConnectivityReceiver.isConnected();
                if (isConnected1 == false){
                    View v1 = findViewById(R.id.cordinatorLayout);
                    new Snackbar("Please connect to the iternet",v1,Color.parseColor("#ffffff"));
                }else {
                    Intent intent = new Intent(HomeActivity.this, MWRHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                startActivity(new Intent(HomeActivity.this, DcrHome.class));
                }
                break;
            case R.id.rlDcrSub:
                String message2 = "Work in progress";
                View v2 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message2,v2,Color.parseColor("#ff0000"));
                break;
            case R.id.rlMwrSub:
                String message3 = "Work in progress";
                View v3 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message3,v3,Color.parseColor("#ff0000"));
                break;
            case R.id.rlDcrAll:
                String message4 = "Work in progress";
                View v4 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message4,v4,Color.parseColor("#ff0000"));
                break;
            case R.id.rlMwrAll:
                String message5 = "Work in progress";
                View v5 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message5,v5,Color.parseColor("#ff0000"));
                break;
            case R.id.rlCircular:
                /*String message6 = "Work in progress";
                View v6 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message6,v6,Color.parseColor("#ff0000"));*/
                Intent intent_circular = new Intent(HomeActivity.this, CircularHomeActivity.class);
                intent_circular.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_circular);
                break;
            case R.id.rlEmails:
                /*String message7 = "Work in progress";
                View v7 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message7,v7,Color.parseColor("#ff0000"));*/
                Intent intent_email = new Intent(HomeActivity.this, EmailHomeActivity.class);
                intent_email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_email);
                break;
            case R.id.rlTracking:
                Intent intent = new Intent(HomeActivity.this, TrackingDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                startActivity(new Intent(HomeActivity.this, TrackingDetailsActivity.class));
                break;
            case R.id.rlDocuments:
//                startActivity(new Intent(HomeActivity.this, DocumentListActivity.class));
                Intent intent1 = new Intent(HomeActivity.this,DocumentListActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
               /* String message8 = "Work in progress";
                View v8 = findViewById(R.id.cordinatorLayout);
                new Snackbar(message8,v8,Color.parseColor("#ff0000"));*/
                break;
            case R.id.rlCustomers:
                Intent intent_customers = new Intent(HomeActivity.this, CustomerHomeActivity.class);
                intent_customers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_customers);
                break;

            default:
                break;
        }
    }

    //===============code to clear sharedPref data starts=========
    public void removeSharedPref(){
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("abm_id");
        editor.remove("abm_name");
        editor.remove("designation_id");
        editor.remove("designation_name");
        editor.remove("designation_type");
        editor.remove("hq_id");
        editor.remove("hq_name");
        editor.remove("rbm_id");
        editor.remove("rbm_name");
        editor.remove("sm_id");
        editor.remove("sm_name");
        editor.remove("state");
        editor.remove("user_full_name");
        editor.remove("user_group_id");
        editor.remove("user_id");
        editor.remove("user_name");
        editor.remove("zbm_id");
        editor.remove("zbm_name");
        editor.commit();
    }
    //===============code to clear sharedPref data ends========


    //==============function to get circular count, using volley starts========
    public void getCircularCount(){
        String url = Config.BaseUrlEpharma + "circular/count/"+userSingletonModel.getUser_group_id()+"/"+ userSingletonModel.getUser_id();
        Log.d("url-=>",url);
        final ProgressDialog loading = ProgressDialog.show(HomeActivity.this, "Loading", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCount = jsonObject.getJSONObject("count");
                            tvCircular.setText("Circulars ("+jsonObjectCount.getString("all")+")");

                            circular_count = jsonObjectCount.getString("unread"); //--as per client requirement on 6th july
                            if(jsonObjectCount.getString("unread").trim().contentEquals("0")){
                                rl_circulars1.setVisibility(View.GONE);
                                rl_circulars.setVisibility(View.VISIBLE);
                            }else{
                                rl_circulars1.setVisibility(View.VISIBLE);
                                rl_circulars.setVisibility(View.GONE);

                                tv_circular_count.setText(jsonObjectCount.getString("unread"));
                            }
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
    //==============function to get circular count, using volley ends========

    long back_pressed;
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (back_pressed + 1000 > System.currentTimeMillis()){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else{
            View v7 = findViewById(R.id.cordinatorLayout);
            new Snackbar("Press once again to exit!",v7,Color.parseColor("#ffffff"));
        }
        back_pressed = System.currentTimeMillis();
    }

    //=============Internet checking code starts(added 22nd Nov)=============

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message = "";
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
        new Snackbar(message,v,Color.parseColor("#ffffff"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        //-------code for version update, added on 20th July, starts
        try {
            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                inAppUpdateType,
                                this,
                                RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });


            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                //For flexible update
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        inAppUpdateType = AppUpdateType.FLEXIBLE;//1
        inAppUpdate();

        //-------code for version update, added on 20th July, ends
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
