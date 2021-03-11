package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.arb.Nextgen.ePharma.Model.MWRWeekDateModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomMWRWeekDateAdapter extends BaseAdapter {
    Date date;
    /*SQLiteDatabase db;
    SqliteDb sqliteDb = new SqliteDb();*/
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    ArrayList<MWRWeekDateModel> arrayList = new ArrayList<>();
    Context context;
    public CustomMWRWeekDateAdapter(Context context, ArrayList<MWRWeekDateModel> customMWRWeekDateAdapterArrayList){
        this.context = context;
        this.arrayList = customMWRWeekDateAdapterArrayList;
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
        RelativeLayout rl_mwr;
        TextView tv_weekdate, tv_day_name;
//        ImageButton imgbtn_add;
        LinearLayout ll_1;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.list_mwr_week_date,viewGroup,false);
        tv_weekdate = view.findViewById(R.id.tv_weekdate);
        tv_day_name = view.findViewById(R.id.tv_day_name);
        ll_1 = view.findViewById(R.id.ll_1);
//        imgbtn_add = view.findViewById(R.id.imgbtn_add);



        //--making background color w.r.t status, code starts----
        if(arrayList.get(i).getDay_status().contentEquals("0")){
            ll_1.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(arrayList.get(i).getDay_status().contentEquals("1")){
            ll_1.setBackgroundColor(Color.parseColor("#ffebf5"));
        }else if(arrayList.get(i).getDay_status().contentEquals("2")){
            ll_1.setBackgroundColor(Color.parseColor("#daefc3"));
        }
        //--making background color w.r.t status, code ends----


        String date_output="";
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = originalFormat.parse(arrayList.get(i).getMwr_date());
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            date_output = outputFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        tv_weekdate.setText(date_output);
        tv_day_name.setText(arrayList.get(i).getMwr_week_day());

        return view;
    }
}
