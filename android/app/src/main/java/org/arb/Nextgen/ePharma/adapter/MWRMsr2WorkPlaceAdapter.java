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
import org.arb.Nextgen.ePharma.Model.MWRMSR2DoctorsListSavedDataModel;
import org.arb.Nextgen.ePharma.Model.MWRMsr1Msr2WorkPlaceModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MWRMsr2WorkPlaceAdapter extends RecyclerView.Adapter<MWRMsr2WorkPlaceAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<MWRMsr1Msr2WorkPlaceModel> mwrMsr1Msr2WorkPlaceModelArrayList = new ArrayList<>();
    public ArrayList<MWRMSR2DoctorsListSavedDataModel> mwrmsr2DoctorsListSavedDataModelArrayList = new ArrayList<>(); //added on 27th jan

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public MWRMsr2WorkPlaceAdapter(Context ctx, ArrayList<MWRMsr1Msr2WorkPlaceModel> mwrMsr1Msr2WorkPlaceModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.mwrMsr1Msr2WorkPlaceModelArrayList = mwrMsr1Msr2WorkPlaceModelArrayList;
    }

    @Override
    public MWRMsr2WorkPlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_mwr_msr1_msr2_workplace_row, parent, false);
        MWRMsr2WorkPlaceAdapter.MyViewHolder holder = new MWRMsr2WorkPlaceAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(MWRMsr2WorkPlaceAdapter.MyViewHolder holder, final int position) {
        holder.itemView.setTag(mwrMsr1Msr2WorkPlaceModelArrayList.get(position));
        holder.tv_name.setText(mwrMsr1Msr2WorkPlaceModelArrayList.get(position).getName());

        //----code to load saved data(if available), starts-----
        if(MWRWeekDate.mwr_day_status_for_draft.contentEquals("1") || (MWRWeekDate.mwr_day_status_for_draft.contentEquals("2"))) {
            try {
                JSONObject jsonObject = new JSONObject(MWRDetails.responseSavedData);
                Log.d("resptest-=>", MWRDetails.responseSavedData);
                JSONArray jsonArray = jsonObject.getJSONArray("msr_2_doctors_list");

                if (!mwrmsr2DoctorsListSavedDataModelArrayList.isEmpty()) {
                    mwrmsr2DoctorsListSavedDataModelArrayList.clear();
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    MWRMSR2DoctorsListSavedDataModel mwrmsr2DoctorsListSavedDataModel = new MWRMSR2DoctorsListSavedDataModel();
                    mwrmsr2DoctorsListSavedDataModel.setWork_place_id(jsonObject1.getString("work_place_id"));
                    mwrmsr2DoctorsListSavedDataModel.setCustomer_id(jsonObject1.getString("customer_id"));
                    mwrmsr2DoctorsListSavedDataModel.setEcl_no(jsonObject1.getString("ecl_no"));

                    mwrmsr2DoctorsListSavedDataModelArrayList.add(mwrmsr2DoctorsListSavedDataModel);
                }

                for (int i = 0; i < mwrMsr1Msr2WorkPlaceModelArrayList.size(); i++) {
                    for (int j = 0; j < mwrmsr2DoctorsListSavedDataModelArrayList.size(); j++) {
                        Log.d("wrkplcIDtest-=>", mwrMsr1Msr2WorkPlaceModelArrayList.get(i).getId());
                        Log.d("wrkplcIDtest1-=>", mwrmsr2DoctorsListSavedDataModelArrayList.get(j).getWork_place_id());
                        if (mwrMsr1Msr2WorkPlaceModelArrayList.get(i).getId().contentEquals(mwrmsr2DoctorsListSavedDataModelArrayList.get(j).getWork_place_id())) {
//                        chckbx_name.setChecked(true);
                            mwrMsr1Msr2WorkPlaceModelArrayList.get(i).setChecked(true);
                            mwrMsr1Msr2WorkPlaceModelArrayList.get(i).setStatus("1");
                            holder.chckbx_name.setChecked(mwrMsr1Msr2WorkPlaceModelArrayList.get(position).getChecked());

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
        //----code to load saved data(if available), ends-----
    }

    @Override
    public int getItemCount() {
        return mwrMsr1Msr2WorkPlaceModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        CheckBox chckbx_name;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_wrkdplace_name);
            chckbx_name = itemView.findViewById(R.id.chckbx_wrkdplace_name);
            chckbx_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int position = getAdapterPosition();
                    if(isChecked == true){
                        mwrMsr1Msr2WorkPlaceModelArrayList.get(position).setStatus("1");
//                        Toast.makeText(context.getApplicationContext(), dcrSelectDoctorStockistChemistModelArrayList.get(position).getName(),Toast.LENGTH_LONG).show();


                    }else if(isChecked == false){
                        mwrMsr1Msr2WorkPlaceModelArrayList.get(position).setStatus("0");

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
