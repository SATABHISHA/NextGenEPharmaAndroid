package org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrDetailsWorkedWithViewAdapter extends RecyclerView.Adapter<DcrDetailsWorkedWithViewAdapter.MyViewHolder>{
    public LayoutInflater inflater;
    public static ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    public static String sumValue = "0", editTextValue_cancel_Close_YN = "0";
    private Context context;

    public DcrDetailsWorkedWithViewAdapter(Context ctx, ArrayList<DcrDetailsListModel> dcrDetailsListModelArrayListManagers){

        inflater = LayoutInflater.from(ctx);
        this.dcrDetailsListModelArrayListManagers = dcrDetailsListModelArrayListManagers;
    }
    @Override
    public DcrDetailsWorkedWithViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_dcr_details_wrkd_with_view_row, parent, false);
        DcrDetailsWorkedWithViewAdapter.MyViewHolder holder = new DcrDetailsWorkedWithViewAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrDetailsWorkedWithViewAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(dcrDetailsListModelArrayListManagers.get(position));
        holder.tv_worked_with.setText(dcrDetailsListModelArrayListManagers.get(position).getManagers_name());
    }

    @Override
    public int getItemCount() {
        return dcrDetailsListModelArrayListManagers.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_worked_with;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_worked_with = itemView.findViewById(R.id.tv_worked_with);
        }
    }
}