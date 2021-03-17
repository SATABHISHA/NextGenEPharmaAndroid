package org.arb.Nextgen.ePharma.adapter.Customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.arb.Nextgen.ePharma.Model.CustomerListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomCustomerListAdapter extends RecyclerView.Adapter<CustomCustomerListAdapter.MyViewHolder> {
    public LayoutInflater inflater;
    public static ArrayList<CustomerListModel> customerListModelArrayList;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    private Context context;
//    public static String name, emp_id;


//    public static ProgressDialog loading;
//    public static TextView tv_download;


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

                   /* AlertDialog.Builder alert = new AlertDialog.Builder(context);
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

                    ll_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, 7); //commented for temp
                            //commented for temp
                            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                            ((Activity) context).startActivityForResult(intent, 7);
                            Toast.makeText(context.getApplicationContext(), employeeImageSettingsModelArrayList.get(position).getEmployee_name(), Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();

                            name = employeeImageSettingsModelArrayList.get(position).getEmployee_name();
                            emp_id = employeeImageSettingsModelArrayList.get(position).getId_person();
//                            Log.d("base64-=>",base64String);

                        }
                    });
                    if (employeeImageSettingsModelArrayList.get(position).getAws_action().contentEquals("enroll")) {
                        LayoutInflater li = LayoutInflater.from(context);
                        final View dialog = li.inflate(R.layout.dialog_employee_image_alert, null);
                        final TextView tv_title = dialog.findViewById(R.id.tv_title);
                        final TextView tv_body = dialog.findViewById(R.id.tv_body);
                        final TextView tv_yes = dialog.findViewById(R.id.tv_yes);
                        final LinearLayout ll_yes = dialog.findViewById(R.id.ll_yes);
                        final LinearLayout ll_no = dialog.findViewById(R.id.ll_no);


                        tv_title.setText("Do you want to Enroll face image for "+employeeImageSettingsModelArrayList.get(position).getName_first()+" "+employeeImageSettingsModelArrayList.get(position).getName_last()+" ?");

                        String body = "Tips for better Recognition result: \n1) Individual's face must be seen clearly and focused \n2) Individual's face must be at center of the camera's frame \n3) Avoid dark background";
                        tv_body.setText(body);

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

                        ll_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, 7); //commented for temp
                                //commented for temp
                                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                ((Activity) context).startActivityForResult(intent, 7);
                                Toast.makeText(context.getApplicationContext(), employeeImageSettingsModelArrayList.get(position).getEmployee_name(), Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();

                                name = employeeImageSettingsModelArrayList.get(position).getEmployee_name();
                                emp_id = employeeImageSettingsModelArrayList.get(position).getId_person();
//                            Log.d("base64-=>",base64String);

                            }
                        });
                        tv_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, 7); //commented for temp
                                //commented for temp
                                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                ((Activity) context).startActivityForResult(intent, 7);
                                Toast.makeText(context.getApplicationContext(), employeeImageSettingsModelArrayList.get(position).getEmployee_name(), Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();

                                name = employeeImageSettingsModelArrayList.get(position).getEmployee_name();
                                emp_id = employeeImageSettingsModelArrayList.get(position).getId_person();
//                            Log.d("base64-=>",base64String);
                            }
                        });
                    }else if(employeeImageSettingsModelArrayList.get(position).getAws_action().contentEquals("delete")){
                        emp_id = employeeImageSettingsModelArrayList.get(position).getId_person();
//                        DeleteImage(position);
                        Log.d("position-=>",String.valueOf(position));

                        //---custom dialog for delete, starts
                        LayoutInflater li = LayoutInflater.from(context);
                        final View dialog = li.inflate(R.layout.dialog_employee_delete_alert, null);

                        TextView tv_ok = dialog.findViewById(R.id.tv_ok);
                        TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);


                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setView(dialog);
                        alert.setCancelable(false);
                        //Creating an alert dialog
                        final AlertDialog alertDialog = alert.create();
                        alertDialog.show();

                        tv_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DeleteImage(position);
                            }
                        });

                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });


                        //---custom dialog for delete, ends
                    }*/
                }
            });



        }

        /*public void DeleteImage(int position){
            String url = Config.BaseUrl + "KioskService.asmx/DeleteFaces";


            final ProgressDialog loading = ProgressDialog.show(((Activity) context), "Loading", "Please wait while loading data", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj = null;
                            try{
                                jsonObj = XML.toJSONObject(response);
                                String responseData = jsonObj.toString();
                                String val = "";
                                JSONObject resobj = new JSONObject(responseData);
                                Iterator<?> keys = resobj.keys();
                                while(keys.hasNext() ) {
                                    String key = (String)keys.next();
                                    if ( resobj.get(key) instanceof JSONObject ) {
                                        JSONObject xx = new JSONObject(resobj.get(key).toString());
                                        val = xx.getString("content");
                                        Log.d("res1-=>",xx.getString("content"));
                                        JSONObject jsonObject = new JSONObject(val);
                                   *//* String status = jsonObject.getString("status");

                                    Log.d("statusTest",status);*//*

                                        if (jsonObject.getString("Status").contentEquals("true")){
//                                            loadData();
                                            employeeImageSettingsAdapter.notifyDataSetChanged();
//                                            Toast.makeText(context.getApplicationContext(),jsonObject.getString("Message"),Toast.LENGTH_LONG).show();  // commented n 18th feb
                                            Log.d("result-=>",jsonObject.getString("Message"));
                                            ((Activity)context).finish();
                                            ((Activity)context).startActivity(((Activity)context).getIntent());
                                        }else{
//                                            loadData();
                                            employeeImageSettingsAdapter.notifyDataSetChanged();
                                            Log.d("result-=>",jsonObject.getString("Message"));
                                            Toast.makeText(context.getApplicationContext(),jsonObject.getString("Message"),Toast.LENGTH_LONG).show();
                                        }

                                        loading.dismiss();
//                                    Toast.makeText(getApplicationContext(),xx.getString("content"),Toast.LENGTH_LONG).show();
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                loading.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();


                    String message = "Could not connect server";
               *//* int color = Color.parseColor("#ffffff");
                Snackbar snackbar = Snackbar.make(findViewById(R.id.relativeLayout), message, 4000);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(color);
                snackbar.show();*//*

               *//* View v = findViewById(R.id.relativeLayout);
                new org.arb.gst.config.Snackbar(message,v);
                Log.d("Volley Error-=>",error.toString());*//*
                    Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    Log.d("Volley Error-=>",error.toString());

                    loading.dismiss();


                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("CorpId", "arb-kol-dev");
                    params.put("EmployeeId", emp_id);
                    params.put("FaceId", employeeImageSettingsModelArrayList.get(position).getAws_face_id());
                *//*params.put("UserId", String.valueOf(RecognizeHomeActivity.PersonId));
                params.put("deviceType", "1");
                params.put("EmpType", "MAIN");*//*

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }*/

    }
}
