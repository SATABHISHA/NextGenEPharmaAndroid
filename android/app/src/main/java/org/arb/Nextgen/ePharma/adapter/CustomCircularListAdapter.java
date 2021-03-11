package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.arb.Nextgen.ePharma.Model.CircularListModel;
import org.arb.Nextgen.ePharma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomCircularListAdapter extends BaseAdapter {
    ArrayList<CircularListModel> arrayList = new ArrayList<>();
    Context context;
    Date date;
    public CustomCircularListAdapter(Context context, ArrayList<CircularListModel> circularListModelArrayList){
        this.context = context;
        this.arrayList = circularListModelArrayList;
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
        final CircularListModel circularListModel = arrayList.get(i);
        ImageView img_attachments, img_next;
        TextView tv_circular_name, tv_publish_date;
        LinearLayout ll_1;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.list_circular_home_row,viewGroup,false);
        tv_circular_name = view.findViewById(R.id.tv_circular_name);
        tv_publish_date = view.findViewById(R.id.tv_publish_date);
        img_attachments = view.findViewById(R.id.img_attachments);
        img_next = view.findViewById(R.id.img_next);
        ll_1 = view.findViewById(R.id.ll_1);
        img_attachments.setVisibility(View.INVISIBLE);


        tv_circular_name.setText(arrayList.get(i).getCircular_name());

        //-------DateFormat code starts---------
        String date_output="";
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = originalFormat.parse(arrayList.get(i).getPublish_date());
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            date_output = outputFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        //-------DateFormat code ends---------

        tv_publish_date.setText(date_output);

        if(arrayList.get(i).getAttachment_file().trim().contentEquals("")){
            img_attachments.setVisibility(View.INVISIBLE);
        }else if(!arrayList.get(i).getAttachment_file().trim().contentEquals("")){
            img_attachments.setVisibility(View.VISIBLE);
        }


        if(arrayList.get(i).getRead_yn().trim().contentEquals("1")){
            img_next.setBackground(ContextCompat.getDrawable(context,R.drawable.menu_arrow));
            ll_1.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(arrayList.get(i).getRead_yn().trim().contentEquals("0")){
            img_next.setBackground(ContextCompat.getDrawable(context,R.drawable.menu_arrow_gray));
            ll_1.setBackgroundColor(Color.parseColor("#F8F8F8"));
        }
        Log.d("Readyn-=>",arrayList.get(i).getRead_yn());
        return view;
    }
}
