package org.arb.Nextgen.ePharma.adapter.Customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Customer.CustomerDctrStockistChemistListActivity;
import org.arb.Nextgen.ePharma.Data.SqliteDb;
import org.arb.Nextgen.ePharma.Model.CustomerListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.config.Config;
import org.arb.Nextgen.ePharma.config.ConnectivityReceiver;
import org.arb.Nextgen.ePharma.config.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomCustomerListAdapter extends RecyclerView.Adapter<CustomCustomerListAdapter.MyViewHolder> {
    public LayoutInflater inflater;
    public static ArrayList<CustomerListModel> customerListModelArrayList;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    private Context context;
    SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();
    int synced_yn = 0;
//    String searchString=""; //added on 26th march
//    public static String name, emp_id;


//    public static ProgressDialog loading;
//    public static TextView tv_download;


//---added on 26th march for searching, code starts---
    public void updateList(ArrayList<CustomerListModel> customerListModelArrayList){
        this.customerListModelArrayList = customerListModelArrayList;
//        this.searchString=searchString; //---for text color
        notifyDataSetChanged();
    }
    //---added on 26th march for searching, code ends---

    public CustomCustomerListAdapter(Context ctx, ArrayList<CustomerListModel> customerListModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.customerListModelArrayList = customerListModelArrayList;
    }

    @Override
    public CustomCustomerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_customer_dctr_stckst_chemist_row, parent, false);
        CustomCustomerListAdapter.MyViewHolder holder = new CustomCustomerListAdapter.MyViewHolder(view);
        context = parent.getContext();

        return holder;
    }

    @Override
    public void onBindViewHolder(CustomCustomerListAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(customerListModelArrayList.get(position));
        holder.tv_name.setText(customerListModelArrayList.get(position).getName());
        /*holder.img_btn_geotag.setBackgroundResource(R.drawable.tagpin);
        holder.img_btn_product.setBackgroundResource(R.drawable.product);*/

        if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 0){
            holder.img_btn_geotag.setBackgroundResource(R.drawable.tagpindisabled);
        }else if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 1){
            holder.img_btn_geotag.setBackgroundResource(R.drawable.tagpin);
        }
        if(customerListModelArrayList.get(position).getType().contentEquals("doctors")){
            holder.tv_speciality.setVisibility(View.VISIBLE);
            holder.tv_customer_class.setVisibility(View.VISIBLE);

            holder.tv_speciality.setText(customerListModelArrayList.get(position).getSpeciality());
            holder.tv_customer_class.setText(customerListModelArrayList.get(position).getCustomer_class());
        }else{
            holder.tv_speciality.setVisibility(View.GONE);
            holder.tv_customer_class.setVisibility(View.GONE);
        }

        holder.tv_work_place.setText(customerListModelArrayList.get(position).getWork_place_name());

    }

    @Override
    public int getItemCount() {
        return customerListModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_speciality, tv_work_place, tv_customer_class;
        LinearLayout ll_enroll;
        ImageView img_btn_geotag, img_btn_product;

        public MyViewHolder(final View itemView) {
            super(itemView);
            final int position = getAdapterPosition();
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_speciality = itemView.findViewById(R.id.tv_speciality);
            tv_work_place = itemView.findViewById(R.id.tv_work_place);
            tv_customer_class = itemView.findViewById(R.id.tv_customer_class);
            img_btn_geotag = itemView.findViewById(R.id.img_btn_geotag);
            img_btn_product = itemView.findViewById(R.id.img_btn_product);
//            ll_enroll = itemView.findViewById(R.id.ll_enroll);





            img_btn_geotag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final int position = getAdapterPosition();

                    LayoutInflater li = LayoutInflater.from(context);
                    final View dialog = li.inflate(R.layout.dialog_customer_list, null);
                    final TextView tv_body = dialog.findViewById(R.id.tv_body);
                    final TextView tv_yes = dialog.findViewById(R.id.tv_yes);
                    final TextView tv_no = dialog.findViewById(R.id.tv_no);
                    final LinearLayout ll_yes = dialog.findViewById(R.id.ll_yes);
                    final LinearLayout ll_no = dialog.findViewById(R.id.ll_no);


                    if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 0) {
                        tv_body.setText("Want to set Geo Tag for " + customerListModelArrayList.get(position).getName() + "?");
                    }else if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 1){
                        tv_body.setText("Are you sure you want to delete Geo Tag for " + customerListModelArrayList.get(position).getName() + "?");
                    }


                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setView(dialog);
                    alert.setCancelable(false);
                    //Creating an alert dialog
                    final AlertDialog alertDialog = alert.create();
                    alertDialog.show();

                    ll_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    tv_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    /*ll_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                            getLocation();
                            *//*Log.d("Latitude:", CustomerDctrStockistChemistListActivity.latitude);
                            Log.d("Longitude:",CustomerDctrStockistChemistListActivity.longitude);
                            Log.d("Address:",CustomerDctrStockistChemistListActivity.locationAddress);*//*
                            Log.d("testPressed:","test");
                            boolean isConnected = ConnectivityReceiver.isConnected();
                            if (isConnected == true){
                                Log.d("status-=>","Internet Available");
                            }else if(isConnected == false){
                                Log.d("status-=>","No Internet");
                            }

                        }
                    });*/

                    tv_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            getLocation();

                            //----------creating sqlite database, code starts-------
                            try {
                                db = context.openOrCreateDatabase("DCRDEtails", context.MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS dcrdetail(id integer PRIMARY KEY AUTOINCREMENT, dcrJsonData VARCHAR)");

                                db.execSQL("CREATE TABLE IF NOT EXISTS TB_CUSTOMER(id integer PRIMARY KEY AUTOINCREMENT, dctr_chemist_stockist_id VARCHAR, ecl_no VARCHAR, name VARCHAR, work_place_id VARCHAR, work_place_name VARCHAR, speciality VARCHAR, customer_class VARCHAR, geo_tagged_yn integer, latitude VARCHAR, longitude VARCHAR, location_address VARCHAR, type VARCHAR, synced_yn integer)"); //--added on 17th march as per requirement

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //----------creating sqlite database, code ends-------
                            Log.d("Latitude:", CustomerDctrStockistChemistListActivity.latitude);
                            Log.d("Longitude:",CustomerDctrStockistChemistListActivity.longitude);
                            Log.d("Address:",CustomerDctrStockistChemistListActivity.locationAddress);
                            Log.d("testPressed:","test");
                            boolean isConnected = ConnectivityReceiver.isConnected();
                            if (isConnected == true){
                               Log.d("status-=>","Internet Available");

                                if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 0) {
                                    save(position);
                                }else if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 1) {
                                    remove(position);
                                }
                            }else if(isConnected == false){
                                synced_yn = 0;
                                if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 0) {
                                    sqliteDb.updateMasterTbCustomer(customerListModelArrayList.get(position).getDctr_chemist_stockist_id(), 1, CustomerDctrStockistChemistListActivity.latitude, CustomerDctrStockistChemistListActivity.longitude, CustomerDctrStockistChemistListActivity.locationAddress, synced_yn, db);
                                }else if(customerListModelArrayList.get(position).getGeo_tagged_yn() == 1) {
                                    sqliteDb.updateMasterTbCustomer(customerListModelArrayList.get(position).getDctr_chemist_stockist_id(), 0, CustomerDctrStockistChemistListActivity.latitude, CustomerDctrStockistChemistListActivity.longitude, CustomerDctrStockistChemistListActivity.locationAddress, synced_yn, db);
                                }
                                Log.d("status-=>","No Internet");
                            }
                            alertDialog.dismiss();
                            Intent intent1=new Intent(context,CustomerDctrStockistChemistListActivity.class);

                            context.startActivity(intent1);
                            ((Activity)context).finish();


                        }
                    });
                }
            });

            img_btn_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, 7); //commented for temp
                    //commented for temp
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    ((Activity) context).startActivityForResult(intent, 7);
                    /*Toast.makeText(context.getApplicationContext(), employeeImageSettingsModelArrayList.get(position).getEmployee_name(), Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();

                    name = employeeImageSettingsModelArrayList.get(position).getEmployee_name();
                    emp_id = employeeImageSettingsModelArrayList.get(position).getId_person();*/
//                            Log.d("base64-=>",base64String);
                }
            });



        }



        public void save(int position){
            final JSONObject DocumentElementobj = new JSONObject();

            final String URL = Config.BaseUrlEpharma+"epharma/msr/customer/geo-tag/set";
            try {
                DocumentElementobj.put("corp_id", userSingletonModel.getCorp_id());
                DocumentElementobj.put("msr_id", Integer.parseInt(userSingletonModel.getUser_id()));
                DocumentElementobj.put("customer_id", Integer.parseInt(customerListModelArrayList.get(position).getDctr_chemist_stockist_id()));
                DocumentElementobj.put("latitude", Double.parseDouble(CustomerDctrStockistChemistListActivity.latitude));
                DocumentElementobj.put("longitude", Double.parseDouble(CustomerDctrStockistChemistListActivity.longitude));
                DocumentElementobj.put("address", CustomerDctrStockistChemistListActivity.locationAddress);

                Log.d("Jsontest-=>",DocumentElementobj.toString());

                JsonObjectRequest request_json = null;
                try {
                    request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(DocumentElementobj.toString()),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        //Process os success response
                                        JSONObject jsonObj = null;
                                        try{
                                            String responseData = response.toString();
                                            String val = "";
                                            JSONObject resobj = new JSONObject(responseData);
                                            Log.d("getData",resobj.toString());

                                            if(resobj.getString("status").contentEquals("true")){

                                                synced_yn = 1;
                                                sqliteDb.updateMasterTbCustomer(customerListModelArrayList.get(position).getDctr_chemist_stockist_id(),1,CustomerDctrStockistChemistListActivity.latitude,CustomerDctrStockistChemistListActivity.longitude,CustomerDctrStockistChemistListActivity.locationAddress,synced_yn,db);

                                            }else {
                                                synced_yn = 0;
                                                sqliteDb.updateMasterTbCustomer(customerListModelArrayList.get(position).getDctr_chemist_stockist_id(),1,CustomerDctrStockistChemistListActivity.latitude,CustomerDctrStockistChemistListActivity.longitude,CustomerDctrStockistChemistListActivity.locationAddress,synced_yn,db);
                                            }


                                        }catch (JSONException e){
                                            //                            loading.dismiss();
                                            e.printStackTrace();
                                            synced_yn = 0;
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

// add the request object to the queue to be executed
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(request_json);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public void remove(int position){
            final JSONObject DocumentElementobj = new JSONObject();

            final String URL = Config.BaseUrlEpharma+"epharma/msr/customer/geo-tag/remove";
            try {
                DocumentElementobj.put("corp_id", userSingletonModel.getCorp_id());
                DocumentElementobj.put("msr_id", Integer.parseInt(userSingletonModel.getUser_id()));
                DocumentElementobj.put("customer_id", Integer.parseInt(customerListModelArrayList.get(position).getDctr_chemist_stockist_id()));


                Log.d("Jsontest-=>",DocumentElementobj.toString());

                JsonObjectRequest request_json = null;
                try {
                    request_json = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(DocumentElementobj.toString()),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        //Process os success response
                                        JSONObject jsonObj = null;
                                        try{
                                            String responseData = response.toString();
                                            String val = "";
                                            JSONObject resobj = new JSONObject(responseData);
                                            Log.d("getData",resobj.toString());

                                            if(resobj.getString("status").contentEquals("true")){

                                                synced_yn = 1;
                                                sqliteDb.updateMasterTbCustomer(customerListModelArrayList.get(position).getDctr_chemist_stockist_id(),0,CustomerDctrStockistChemistListActivity.latitude,CustomerDctrStockistChemistListActivity.longitude,CustomerDctrStockistChemistListActivity.locationAddress,synced_yn,db);
                                            }else {
                                                synced_yn = 0;
                                                sqliteDb.updateMasterTbCustomer(customerListModelArrayList.get(position).getDctr_chemist_stockist_id(),0,CustomerDctrStockistChemistListActivity.latitude,CustomerDctrStockistChemistListActivity.longitude,CustomerDctrStockistChemistListActivity.locationAddress,synced_yn,db);
                                            }


                                        }catch (JSONException e){
                                            //                            loading.dismiss();
                                            e.printStackTrace();
                                            synced_yn = 0;
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

// add the request object to the queue to be executed
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(request_json);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
