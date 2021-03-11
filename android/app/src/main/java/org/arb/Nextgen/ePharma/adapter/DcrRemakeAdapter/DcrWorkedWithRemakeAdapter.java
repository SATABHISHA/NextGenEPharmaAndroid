package org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrWorkedWithRemakeAdapter extends RecyclerView.Adapter<DcrWorkedWithRemakeAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public DcrWorkedWithRemakeAdapter(Context ctx, ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers){

        inflater = LayoutInflater.from(ctx);
        this.dcrDetailsListModelArrayListManagers = dcrDetailsListModelArrayListManagers;
    }
    @Override
    public DcrWorkedWithRemakeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_dcr_details_worked_with_remake_row, parent, false);
        DcrWorkedWithRemakeAdapter.MyViewHolder holder = new DcrWorkedWithRemakeAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrWorkedWithRemakeAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(dcrDetailsListModelArrayListManagers.get(position));
        holder.tv_worked_with.setText(dcrDetailsListModelArrayListManagers.get(position).getManagers_name());
    }

    @Override
    public int getItemCount() {
        return dcrDetailsListModelArrayListManagers.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_worked_with;
        CheckBox chckbx_wrkdplace_name;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_worked_with = itemView.findViewById(R.id.tv_worked_with);
            chckbx_wrkdplace_name = itemView.findViewById(R.id.chckbx_wrkdplace_name);
            chckbx_wrkdplace_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int position = getAdapterPosition();
                    if(isChecked == true){
                        dcrDetailsListModelArrayListManagers.get(position).setStatus("1");
//                        Toast.makeText(context.getApplicationContext(), dcrDetailsListModelArrayListManagers.get(position).getManagers_name(),Toast.LENGTH_LONG).show();
                    }else if(isChecked == false){
                        dcrDetailsListModelArrayListManagers.get(position).setStatus("0");
//                        Toast.makeText(context.getApplicationContext(), dcrDetailsListModelArrayListManagers.get(position).getManagers_name()+" has been unchecked",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}
