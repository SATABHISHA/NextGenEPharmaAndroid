package org.arb.Nextgen.ePharma.Customer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.CustomerListModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.adapter.Customer.CustomCustomerListAdapter;
import org.arb.Nextgen.ePharma.config.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerDctrStockistChemistListActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {
    TextView tv_bar_item_title, tv_bar_item_count;
    RecyclerView recycler_view;
    LinearLayout ll_recycler;
    public static CustomCustomerListAdapter customCustomerListAdapter;
    ArrayList<CustomerListModel> customerListModelArrayList = new ArrayList<>();
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();
    public static final int RequestPermissionCode = 1;
    public static String base64String;

    public static String latitude = "", longitude = "", locationAddress="";
    LocationManager locationManager;
    EditText edtxt_search;

    ImageView img_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dctr_stckst_chemist_details);

        ll_recycler = findViewById(R.id.ll_recycler);
        edtxt_search = findViewById(R.id.edtxt_search);
        img_search = findViewById(R.id.img_search);
        tv_bar_item_title = findViewById(R.id.tv_bar_item_title);
        tv_bar_item_title.setText(CustomerHomeActivity.customer_type);

        tv_bar_item_count = findViewById(R.id.tv_bar_item_count);

        customCustomerListAdapter = new CustomCustomerListAdapter(this,customerListModelArrayList);
        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        //---newly added on 26th march, code starts
        edtxt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


        //---newly added on 26th march, code ends

        getLocation();
        LoadData(CustomerHomeActivity.type);


//        img_search.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.img_search:
                filter(edtxt_search.getText().toString());
                break;
        }
    }

    //----newly added for search on 26th march code starts------
    void filter(String text){
        ArrayList<CustomerListModel> temp = new ArrayList();
        for(CustomerListModel s : customerListModelArrayList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(s.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(s);

            }
        }
        //update recyclerview
        customCustomerListAdapter.updateList(temp);
    }
    //----newly added for search on 26th march code ends------

    public void LoadData(String type){
        if(!customerListModelArrayList.isEmpty()){
            customerListModelArrayList.clear();
        }
        //----------creating sqlite database, code starts-------
        try {
            db = openOrCreateDatabase("DCRDEtails", MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");

            db.execSQL("CREATE TABLE IF NOT EXISTS TB_CUSTOMER(id integer PRIMARY KEY AUTOINCREMENT, dctr_chemist_stockist_id VARCHAR, ecl_no VARCHAR, name VARCHAR, work_place_id VARCHAR, work_place_name VARCHAR, speciality VARCHAR, customer_class VARCHAR, geo_tagged_yn integer, latitude VARCHAR, longitude VARCHAR, location_address VARCHAR, type VARCHAR, synced_yn integer)"); //--added on 17th march as per requirement
           /* if(sqliteDb.countMasterData(db) > 0){
                sqliteDb.deleteMasterData(db);
            }*/
            tv_bar_item_count.setText(String.valueOf(sqliteDb.countMasterData1(db, type)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------creating sqlite database, code ends-------

        Cursor c = db.rawQuery("SELECT * FROM TB_CUSTOMER where type = '"+type+"' ", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
        if(c!=null) {
            while (!c.isAfterLast()) {
                CustomerListModel customerListModel = new CustomerListModel();
                customerListModel.setDctr_chemist_stockist_id(c.getString(1));
                customerListModel.setEcl_no(c.getString(2));
                customerListModel.setName(c.getString(3));
                customerListModel.setWork_place_id(c.getString(4));
                customerListModel.setWork_place_name(c.getString(5));
                customerListModel.setSpeciality(c.getString(6));
                customerListModel.setCustomer_class(c.getString(7));
                customerListModel.setGeo_tagged_yn(c.getInt(8));
                customerListModel.setLatitude(c.getString(9));
                customerListModel.setLongitude(c.getString(10));
                customerListModel.setLocation_address(c.getString(11));
                customerListModel.setType(c.getString(12));
                customerListModel.setSynced_yn(c.getInt(13));

                customerListModelArrayList.add(customerListModel);
                c.moveToNext();
            }
            recycler_view.setAdapter(new CustomCustomerListAdapter(CustomerDctrStockistChemistListActivity.this,customerListModelArrayList));
        }

    }

    //-------------location code starts(added by Satabhisha)--------
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            //---minTime(in millisec), minDistance(in meters)
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
//        Toast.makeText(getApplicationContext(), "Latitude:" + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+ addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
//            Toast.makeText(getApplicationContext(), addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2), Toast.LENGTH_LONG).show();
//            String locationAddress = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2);


            latitude = String.valueOf(location.getLatitude());
//            latitude = "";
            longitude = String.valueOf(location.getLongitude());
//            longitude = "";
            locationAddress = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2);
            Log.d("Location-=>",locationAddress);
            /*Log.d("Latitude:", latitude);
            Log.d("Longitude:",longitude);
            Log.d("Address:",locationAddress);*/
//            final Context context = this;

        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
    //-------------location code ends(added by Satabhisha)--------

    //========Camera code starts=======
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            base64String = ImageUtil.convert(bitmap);
//            Log.d("base64-=>",base64String);
//            Log.d("name-=>",EmployeeImageSettingsAdapter.name);

//            recognize(base64String);
//            Log.d("base64-=>",base64String);
//            EnrollImage(base64String);
        }
    }
    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(CustomerDctrStockistChemistListActivity.this,
                Manifest.permission.CAMERA)) {
//            Toast.makeText(getApplicationContext(),"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CustomerDctrStockistChemistListActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(EmployeeImageSettingsActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CustomerDctrStockistChemistListActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    //========Camera code ends=======

}
