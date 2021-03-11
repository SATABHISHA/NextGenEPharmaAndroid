package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.arb.Nextgen.ePharma.MWR.MWRHome;
import org.arb.Nextgen.ePharma.MWR.MWRWeekDate;
import org.arb.Nextgen.ePharma.Model.MWRHomeModel;
import org.arb.Nextgen.ePharma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomMWRHomeAdapter extends BaseAdapter {
    Date date;
    ArrayList<MWRHomeModel> arrayList = new ArrayList<>();
    Context context;
    public CustomMWRHomeAdapter(Context context, ArrayList<MWRHomeModel> customMWRHomeAdapterArrayList){
        this.context = context;
        this.arrayList = customMWRHomeAdapterArrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return true;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final MWRHomeModel mwrHomeModel = arrayList.get(i);
        RelativeLayout rl_mwr, relative_layout;
        LinearLayout ll_1;
        ImageButton imgbtn_next;
        TextView tv_weekdate, tv_day_date_period, tv_mwr_no_caption, tv_mwr_no;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.list_mwr_home_row,viewGroup,false);
        tv_weekdate = view.findViewById(R.id.tv_weekdate);
        tv_day_date_period = view.findViewById(R.id.tv_day_date_period);
        tv_mwr_no_caption = view.findViewById(R.id.tv_mwr_no_caption);
        tv_mwr_no = view.findViewById(R.id.tv_mwr_no);
        rl_mwr = view.findViewById(R.id.rl_mwr);
        relative_layout = view.findViewById(R.id.relative_layout);
        ll_1 = view.findViewById(R.id.ll_1);
        imgbtn_next = view.findViewById(R.id.imgbtn_next);

        //--making background color w.r.t status, code starts----
        if(arrayList.get(i).getStatus().contentEquals("0")){
            ll_1.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(arrayList.get(i).getStatus().contentEquals("1")){
            ll_1.setBackgroundColor(Color.parseColor("#ffebf5"));
        }else if(arrayList.get(i).getStatus().contentEquals("2")){
            ll_1.setBackgroundColor(Color.parseColor("#daefc3"));
        }
        //--making background color w.r.t status, code ends----

        String date_output="";
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = originalFormat.parse(arrayList.get(i).getWeek_date());
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            date_output = outputFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        tv_weekdate.setText(date_output);

        Date start_date_original_format = null, end_date_original_format = null;
        String start_date_output="", end_date_output="";
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            start_date_original_format = originalFormat.parse(arrayList.get(i).getWeek_start_date());
            end_date_original_format = originalFormat.parse(arrayList.get(i).getWeek_end_date());

            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            start_date_output = outputFormat.format(start_date_original_format);
            end_date_output = outputFormat.format(end_date_original_format);
        }catch (Exception e){
            e.printStackTrace();
        }


        tv_day_date_period.setText(start_date_output+" To "+ end_date_output);
        if(!arrayList.get(i).getMwrNo().trim().contentEquals("")){
            rl_mwr.setVisibility(View.VISIBLE);
            tv_mwr_no.setText(arrayList.get(i).getMwrNo());
        }else{
            rl_mwr.setVisibility(View.INVISIBLE);
        }

        imgbtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MWRHome.week_start_date = mwrHomeModel.getWeek_start_date();
                MWRHome.week_end_date = mwrHomeModel.getWeek_end_date();
                if(mwrHomeModel.getMwrNo().trim().contentEquals("")){
                    MWRHome.mwr_no = "0";
                }else{
                    MWRHome.mwr_no = mwrHomeModel.getMwrNo();
                }

                context.startActivity(new Intent(context,MWRWeekDate.class));
            }
        });

        return view;
    }
}
