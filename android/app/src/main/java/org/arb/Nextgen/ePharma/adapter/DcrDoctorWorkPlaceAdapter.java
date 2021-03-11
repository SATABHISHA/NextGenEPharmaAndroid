package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Model.DcrDctrChemistStockistWorkPlaceModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrDoctorWorkPlaceAdapter extends RecyclerView.Adapter<DcrDoctorWorkPlaceAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<DcrDctrChemistStockistWorkPlaceModel> dcrDctrChemistStockistWorkPlaceModelArrayList = new ArrayList<>();
    public static ArrayList<DcrDctrChemistStockistWorkPlaceModel> dcrDctrChemistStockistWorkPlaceModelArrayList1;

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public DcrDoctorWorkPlaceAdapter(Context ctx, ArrayList<DcrDctrChemistStockistWorkPlaceModel> dcrDctrChemistStockistWorkPlaceModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.dcrDctrChemistStockistWorkPlaceModelArrayList = dcrDctrChemistStockistWorkPlaceModelArrayList;
    }
    @Override
    public DcrDoctorWorkPlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_dcr_dctr_chemist_stockist_workdplace_row, parent, false);
        DcrDoctorWorkPlaceAdapter.MyViewHolder holder = new DcrDoctorWorkPlaceAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrDoctorWorkPlaceAdapter.MyViewHolder holder, final int position) {
        holder.itemView.setTag(dcrDctrChemistStockistWorkPlaceModelArrayList.get(position));
        holder.tv_name.setText(dcrDctrChemistStockistWorkPlaceModelArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dcrDctrChemistStockistWorkPlaceModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        CheckBox chckbx_name;

        public MyViewHolder(final View itemView) {
            super(itemView);
            dcrDctrChemistStockistWorkPlaceModelArrayList1 = new ArrayList<DcrDctrChemistStockistWorkPlaceModel>();
            tv_name = itemView.findViewById(R.id.tv_wrkdplace_name);
            chckbx_name = itemView.findViewById(R.id.chckbx_wrkdplace_name);
            chckbx_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int position = getAdapterPosition();
                    if(isChecked == true){
                        dcrDctrChemistStockistWorkPlaceModelArrayList.get(position).setStatus("1");
//                        Toast.makeText(context.getApplicationContext(), dcrDctrChemistStockistWorkPlaceModelArrayList.get(position).getName(),Toast.LENGTH_LONG).show();


                    }else if(isChecked == false){
                        dcrDctrChemistStockistWorkPlaceModelArrayList.get(position).setStatus("0");

//                        Toast.makeText(context.getApplicationContext(), dcrDctrChemistStockistWorkPlaceModelArrayList.get(position).getName()+" has been unchecked",Toast.LENGTH_LONG).show();
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
