package org.arb.Nextgen.ePharma.Customer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.CustomerListModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.adapter.Customer.CustomCustomerListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerDctrStockistChemistListActivity extends AppCompatActivity implements LocationListener{
    TextView tv_bar_item_title, tv_bar_item_count;
    RecyclerView recycler_view;
    LinearLayout ll_recycler;
    public static CustomCustomerListAdapter customCustomerListAdapter;
    ArrayList<CustomerListModel> customerListModelArrayList = new ArrayList<>();
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();

    public static String latitude = "", longitude = "", locationAddress="";
    LocationManager locationManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dctr_stckst_chemist_details);

        ll_recycler = findViewById(R.id.ll_recycler);
        tv_bar_item_title = findViewById(R.id.tv_bar_item_title);
        tv_bar_item_title.setText(CustomerHomeActivity.customer_type);

        tv_bar_item_count = findViewById(R.id.tv_bar_item_count);

        customCustomerListAdapter = new CustomCustomerListAdapter(this,customerListModelArrayList);
        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        getLocation();
        LoadData(CustomerHomeActivity.type);

    }

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

}
