package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrSelectStockistAdapter extends RecyclerView.Adapter<DcrSelectStockistAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList = new ArrayList<>();

    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public DcrSelectStockistAdapter(Context ctx, ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.dcrSelectDoctorStockistChemistModelArrayList = dcrSelectDoctorStockistChemistModelArrayList;
    }
    @Override
    public DcrSelectStockistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_dcr_dctr_chemist_stockist_workdplace_row, parent, false);
        DcrSelectStockistAdapter.MyViewHolder holder = new DcrSelectStockistAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrSelectStockistAdapter.MyViewHolder holder, final int position) {
        holder.itemView.setTag(dcrSelectDoctorStockistChemistModelArrayList.get(position));
        holder.tv_name.setText(dcrSelectDoctorStockistChemistModelArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dcrSelectDoctorStockistChemistModelArrayList.size();
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
                        dcrSelectDoctorStockistChemistModelArrayList.get(position).setStatus("1");
//                        Toast.makeText(context.getApplicationContext(), dcrSelectDoctorStockistChemistModelArrayList.get(position).getName(),Toast.LENGTH_LONG).show();


                    }else if(isChecked == false){
                        dcrSelectDoctorStockistChemistModelArrayList.get(position).setStatus("0");

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
