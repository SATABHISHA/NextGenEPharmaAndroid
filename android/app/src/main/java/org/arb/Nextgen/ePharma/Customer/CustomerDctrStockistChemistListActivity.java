package org.arb.Nextgen.ePharma.Customer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.CustomerListModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.adapter.Customer.CustomCustomerListAdapter;

import java.util.ArrayList;

public class CustomerDctrStockistChemistListActivity extends AppCompatActivity {
    TextView tv_bar_item_title, tv_bar_item_count;
    RecyclerView recycler_view;
    LinearLayout ll_recycler;
    public static CustomCustomerListAdapter customCustomerListAdapter;
    ArrayList<CustomerListModel> customerListModelArrayList = new ArrayList<>();
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();
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
            tv_bar_item_count.setText(String.valueOf(sqliteDb.countMasterData(db)));
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
}
