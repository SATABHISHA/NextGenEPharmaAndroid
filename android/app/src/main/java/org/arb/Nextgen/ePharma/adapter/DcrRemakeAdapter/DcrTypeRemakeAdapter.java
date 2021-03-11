package org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.DcrAgainRemake.DcrTypeRemakeActivity;
import org.arb.Nextgen.ePharma.Model.DcrTypeModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrTypeRemakeAdapter extends RecyclerView.Adapter<DcrTypeRemakeAdapter.MyViewHolder> {
    public LayoutInflater inflater;
    public static ArrayList<DcrTypeModel> dcrTypeModelArrayList;
    private Context context;
    private int lastCheckedPosition = -1;

//    public static String od_request_id = "";
//    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

//    public static ProgressDialog loading;
//    public static TextView tv_download;


    public DcrTypeRemakeAdapter(Context ctx, ArrayList<DcrTypeModel> dcrTypeModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.dcrTypeModelArrayList = dcrTypeModelArrayList;
    }
    @Override
    public DcrTypeRemakeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_dcr_type_remake_row, parent, false);
        DcrTypeRemakeAdapter.MyViewHolder holder = new DcrTypeRemakeAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrTypeRemakeAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(dcrTypeModelArrayList.get(position));

        holder.tv_dcr_type.setText(dcrTypeModelArrayList.get(position).getDcr_type());
        holder.radio_btn.setChecked(position == lastCheckedPosition);
        if (lastCheckedPosition == position) {
            holder.radio_btn.setChecked(true);
        }else{
            holder.radio_btn.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return dcrTypeModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_dcr_type;
        RadioButton radio_btn;
        RelativeLayout relative_layout;



        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_dcr_type = itemView.findViewById(R.id.tv_dcr_type);

            relative_layout = itemView.findViewById(R.id.relative_layout);
            radio_btn = itemView.findViewById(R.id.radio_btn);

            relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();


                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, dcrTypeModelArrayList.size());

//                    TaskSelectionActivity.taskSelectionAdapter.notifyDataSetChanged();
                   /* OutdoorListActivity.new_create_yn = 0;
                    od_request_id = outDoorListModelArrayList.get(position).getOd_request_id();
                    Intent i = new Intent(context, OutDoorRequestActivity.class);
                    context.startActivity(i);*/

                    DcrTypeRemakeActivity.dcr_id = dcrTypeModelArrayList.get(position).getId();
                    DcrTypeRemakeActivity.dcr_type = dcrTypeModelArrayList.get(position).getDcr_type();
                }
            });
            radio_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, dcrTypeModelArrayList.size());
                    DcrTypeRemakeActivity.dcr_id = dcrTypeModelArrayList.get(lastCheckedPosition).getId();
                    DcrTypeRemakeActivity.dcr_type = dcrTypeModelArrayList.get(lastCheckedPosition).getDcr_type();

                    DcrTypeRemakeActivity.dcr_id = dcrTypeModelArrayList.get(lastCheckedPosition).getId();
                    DcrTypeRemakeActivity.dcr_type = dcrTypeModelArrayList.get(lastCheckedPosition).getDcr_type();
                }
            });

        }


    }



}
