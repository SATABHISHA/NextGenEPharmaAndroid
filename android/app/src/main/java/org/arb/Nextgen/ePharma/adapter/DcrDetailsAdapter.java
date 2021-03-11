package org.arb.Nextgen.ePharma.adapter;

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

public class DcrDetailsAdapter extends RecyclerView.Adapter<DcrDetailsAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public DcrDetailsAdapter(Context ctx, ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers){

        inflater = LayoutInflater.from(ctx);
        this.dcrDetailsListModelArrayListManagers = dcrDetailsListModelArrayListManagers;
    }
    @Override
    public DcrDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_dcr_details_row, parent, false);
        DcrDetailsAdapter.MyViewHolder holder = new DcrDetailsAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrDetailsAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(dcrDetailsListModelArrayListManagers.get(position));
        holder.tv_name.setText(dcrDetailsListModelArrayListManagers.get(position).getManagers_name());
    }

    @Override
    public int getItemCount() {
        return dcrDetailsListModelArrayListManagers.size();
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
