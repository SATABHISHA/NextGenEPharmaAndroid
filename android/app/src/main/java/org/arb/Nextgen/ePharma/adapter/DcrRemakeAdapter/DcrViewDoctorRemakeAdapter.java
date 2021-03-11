package org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Model.DcrSelectDoctorStockistChemistModel;
import org.arb.Nextgen.ePharma.R;
import static org.arb.Nextgen.ePharma.DcrAgainRemake.DcrViewDoctorRemake.dcrViewDoctorRemakeAdapter;

import java.util.ArrayList;

public class DcrViewDoctorRemakeAdapter extends RecyclerView.Adapter<DcrViewDoctorRemakeAdapter.MyViewHolder> {
    public LayoutInflater inflater;
    public static ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList;
    private Context context;
    private int lastCheckedPosition = -1;

//    public static String od_request_id = "";
//    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

//    public static ProgressDialog loading;
//    public static TextView tv_download;


    public DcrViewDoctorRemakeAdapter(Context ctx, ArrayList<DcrSelectDoctorStockistChemistModel> dcrSelectDoctorStockistChemistModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.dcrSelectDoctorStockistChemistModelArrayList = dcrSelectDoctorStockistChemistModelArrayList;
    }
    @Override
    public DcrViewDoctorRemakeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_dcr_view_doctor_remake_row, parent, false);
        DcrViewDoctorRemakeAdapter.MyViewHolder holder = new DcrViewDoctorRemakeAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrViewDoctorRemakeAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(dcrSelectDoctorStockistChemistModelArrayList.get(position));

        holder.tv_name.setText(dcrSelectDoctorStockistChemistModelArrayList.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return dcrSelectDoctorStockistChemistModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        ImageButton img_btn_delete_task;



        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_btn_delete_task = itemView.findViewById(R.id.img_btn_delete_task);

            img_btn_delete_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();


//                    TaskSelectionActivity.taskSelectionAdapter.notifyDataSetChanged();
                   /* OutdoorListActivity.new_create_yn = 0;
                    od_request_id = outDoorListModelArrayList.get(position).getOd_request_id();
                    Intent i = new Intent(context, OutDoorRequestActivity.class);
                    context.startActivity(i);*/

                   /* DcrTypeRemakeActivity.dcr_id = dcrTypeModelArrayList.get(position).getId();
                    DcrTypeRemakeActivity.dcr_type = dcrTypeModelArrayList.get(position).getDcr_type();*/
                    dcrSelectDoctorStockistChemistModelArrayList.remove(position);
                    dcrViewDoctorRemakeAdapter.notifyItemRemoved(position);
//                    Toast.makeText(context,"Deleted"+dcrSelectDoctorStockistChemistModelArrayList.get(position).getName(),Toast.LENGTH_LONG).show();
                }
            });


        }


    }



}
