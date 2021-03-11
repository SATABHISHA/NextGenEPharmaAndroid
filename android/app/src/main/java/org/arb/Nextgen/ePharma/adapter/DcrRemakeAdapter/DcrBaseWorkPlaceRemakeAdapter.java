package org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.DcrAgainRemake.DcrSelectBaseWorkPlaceRemakeActivity;
import org.arb.Nextgen.ePharma.Model.DcrDetailsListModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrBaseWorkPlaceRemakeAdapter extends RecyclerView.Adapter<DcrBaseWorkPlaceRemakeAdapter.MyViewHolder> {
    public LayoutInflater inflater;
    public static ArrayList<DcrDetailsListModel> dcrTypeModelArrayList;
    private Context context;
    private int lastCheckedPosition = -1;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

//    public static String od_request_id = "";
//    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

//    public static ProgressDialog loading;
//    public static TextView tv_download;


    public DcrBaseWorkPlaceRemakeAdapter(Context ctx, ArrayList<DcrDetailsListModel> dcrTypeModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.dcrTypeModelArrayList = dcrTypeModelArrayList;
    }
    @Override
    public DcrBaseWorkPlaceRemakeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_dcr_base_work_place_remake_row, parent, false);
        DcrBaseWorkPlaceRemakeAdapter.MyViewHolder holder = new DcrBaseWorkPlaceRemakeAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(DcrBaseWorkPlaceRemakeAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(dcrTypeModelArrayList.get(position));

        holder.tv_base_wrk_place.setText(dcrTypeModelArrayList.get(position).getName());
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
        public TextView tv_base_wrk_place;
        RadioButton radio_btn;
        RelativeLayout relative_layout;



        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_base_wrk_place = itemView.findViewById(R.id.tv_base_wrk_place);

            relative_layout = itemView.findViewById(R.id.relative_layout);
            radio_btn = itemView.findViewById(R.id.radio_btn);

            relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();


                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, dcrTypeModelArrayList.size());

                   /* userSingletonModel.setBase_work_place_id(dcrTypeModelArrayList.get(position).getId());
                    userSingletonModel.setBase_work_place_name(dcrTypeModelArrayList.get(position).getName());*/

                    DcrSelectBaseWorkPlaceRemakeActivity.base_wrk_place_name = dcrTypeModelArrayList.get(position).getName();
                    DcrSelectBaseWorkPlaceRemakeActivity.base_wrk_place_id = dcrTypeModelArrayList.get(position).getId();

//                    DcrDetailsRemakeActivity.tv_work_place.setText(userSingletonModel.getBase_work_place_name());

//                    Log.d("wrkplace-=>",userSingletonModel.getBase_work_place_name());
                }
            });
            radio_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, dcrTypeModelArrayList.size());

                    /*userSingletonModel.setBase_work_place_id(dcrTypeModelArrayList.get(lastCheckedPosition).getId());
                    userSingletonModel.setBase_work_place_name(dcrTypeModelArrayList.get(lastCheckedPosition).getName());*/

                    DcrSelectBaseWorkPlaceRemakeActivity.base_wrk_place_name = dcrTypeModelArrayList.get(lastCheckedPosition).getName();
                    DcrSelectBaseWorkPlaceRemakeActivity.base_wrk_place_id = dcrTypeModelArrayList.get(lastCheckedPosition).getId();
//                    DcrDetailsRemakeActivity.tv_work_place.setText(userSingletonModel.getBase_work_place_name());

//                    Log.d("wrkplace-=>",userSingletonModel.getBase_work_place_name());
                }
            });

        }


    }



}
