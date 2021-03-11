package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.MWR.MWRDetails;
import org.arb.Nextgen.ePharma.MWR.MWRWeekDate;
import org.arb.Nextgen.ePharma.MWR.MWRmsr1;
import org.arb.Nextgen.ePharma.Model.MWRMSR1DoctorsListSavedDataModel;
import org.arb.Nextgen.ePharma.Model.MWRMsr1Msr2DoctorModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MWRMsr1DoctorAdapter extends RecyclerView.Adapter<MWRMsr1DoctorAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<MWRMsr1Msr2DoctorModel> mwrMsr1Msr2DoctorModelArrayList = new ArrayList<>();
    public ArrayList<MWRMSR1DoctorsListSavedDataModel> mwrmsr1DoctorsListSavedDataModelArrayList = new ArrayList<>(); //--wknd added

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public MWRMsr1DoctorAdapter(Context ctx, ArrayList<MWRMsr1Msr2DoctorModel> mwrMsr1Msr2DoctorModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.mwrMsr1Msr2DoctorModelArrayList = mwrMsr1Msr2DoctorModelArrayList;
    }

    @Override
    public MWRMsr1DoctorAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_mwr_msr1_msr2_doctor_row, parent, false);
        MWRMsr1DoctorAdapter.MyViewHolder holder = new MWRMsr1DoctorAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(MWRMsr1DoctorAdapter.MyViewHolder holder, final int position) {
        holder.itemView.setTag(mwrMsr1Msr2DoctorModelArrayList.get(position));
        holder.tv_name.setText(mwrMsr1Msr2DoctorModelArrayList.get(position).getName());

        //----code to load saved data(if available), starts(wknd added)-----
        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1") || (MWRWeekDate.mwr_day_status_for_draft.contentEquals("2"))) {
            try {
                JSONObject jsonObject = new JSONObject(MWRDetails.responseSavedData);
                Log.d("resptest-=>", MWRDetails.responseSavedData);
                JSONArray jsonArray = jsonObject.getJSONArray("msr_1_doctors_list");

                if (!mwrmsr1DoctorsListSavedDataModelArrayList.isEmpty()) {
                    mwrmsr1DoctorsListSavedDataModelArrayList.clear();
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    MWRMSR1DoctorsListSavedDataModel mwrmsr1DoctorsListSavedDataModel = new MWRMSR1DoctorsListSavedDataModel();
                    mwrmsr1DoctorsListSavedDataModel.setWork_place_id(jsonObject1.getString("work_place_id"));
                    mwrmsr1DoctorsListSavedDataModel.setCustomer_id(jsonObject1.getString("customer_id"));
                    mwrmsr1DoctorsListSavedDataModel.setEcl_no(jsonObject1.getString("ecl_no"));

                    mwrmsr1DoctorsListSavedDataModelArrayList.add(mwrmsr1DoctorsListSavedDataModel);
                }

                for (int i = 0; i < mwrMsr1Msr2DoctorModelArrayList.size(); i++) {
                    for (int j = 0; j < mwrmsr1DoctorsListSavedDataModelArrayList.size(); j++) {
                        Log.d("wrkplcIDtest-=>", mwrMsr1Msr2DoctorModelArrayList.get(i).getId());
                        Log.d("wrkplcIDtest1-=>", mwrmsr1DoctorsListSavedDataModelArrayList.get(j).getCustomer_id());
                        if (mwrMsr1Msr2DoctorModelArrayList.get(i).getId().contentEquals(mwrmsr1DoctorsListSavedDataModelArrayList.get(j).getCustomer_id())) {
//                        chckbx_name.setChecked(true);
                            mwrMsr1Msr2DoctorModelArrayList.get(i).setChecked(true);
                            mwrMsr1Msr2DoctorModelArrayList.get(i).setStatus("1");
                            holder.chckbx_name.setChecked(mwrMsr1Msr2DoctorModelArrayList.get(position).getChecked());

                        }/*else{
//                        holder.chckbx_name.setChecked(false);
                        mwrMsr1Msr2WorkPlaceModelArrayList.get(i).setChecked(false);
                        holder.chckbx_name.setChecked(mwrMsr1Msr2WorkPlaceModelArrayList.get(position).getChecked());
                    }*/ //---as this piece of code is making all other chekbox false
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //----code to load saved data(if available), ends(wknd added)-----
    }

    @Override
    public int getItemCount() {
        return mwrMsr1Msr2DoctorModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        CheckBox chckbx_name;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            chckbx_name = itemView.findViewById(R.id.chckbx_name);
            chckbx_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int position = getAdapterPosition();
                    if(isChecked == true){
                        mwrMsr1Msr2DoctorModelArrayList.get(position).setStatus("1");
//                        Toast.makeText(context.getApplicationContext(), dcrSelectDoctorStockistChemistModelArrayList.get(position).getName(),Toast.LENGTH_LONG).show();
                        MWRmsr1.btn_next.setClickable(true);
                        MWRmsr1.btn_next.setEnabled(true);
                        MWRmsr1.btn_next.setAlpha(1.0f);


                    }else if(isChecked == false){
                        mwrMsr1Msr2DoctorModelArrayList.get(position).setStatus("0");

                        //---code to check weather msr1Doctor is empty or not and make button disable/enable accordingly, code starts---
                        int count = 0;
                        if (!MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.isEmpty()) {
                            for (int i = 0; i < MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.size(); i++) {
                                if (MWRMsr1DoctorAdapter.mwrMsr1Msr2DoctorModelArrayList.get(i).getStatus().contentEquals("1")) {
                                    count++;
                                }
                            }

                        }
                        if(count>0){
                            MWRmsr1.btn_next.setClickable(true);
                            MWRmsr1.btn_next.setEnabled(true);
                            MWRmsr1.btn_next.setAlpha(1.0f);
                        }else if(count == 0){
                            MWRmsr1.btn_next.setClickable(false);
                            MWRmsr1.btn_next.setEnabled(false);
                            MWRmsr1.btn_next.setAlpha(0.5f);
                        }
                        //---code to check weather msr1Doctor is empty or not and make button disable/enable accordingly, code ends---

//                        Toast.makeText(context.getApplicationContext(), dcrSelectDoctorStockistChemistModelArrayList.get(position).getName()+" has been unchecked",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    //----code for checkbox position fix, starts----
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    //----code for checkbox position fix, ends----
}
