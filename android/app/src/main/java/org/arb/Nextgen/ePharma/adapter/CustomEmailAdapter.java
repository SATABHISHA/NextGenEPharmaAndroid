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

import org.arb.Nextgen.ePharma.Model.EmailModel;
import org.arb.Nextgen.ePharma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomEmailAdapter extends BaseAdapter {
    ArrayList<EmailModel> arrayList = new ArrayList<>();
    Context context;
    Date date;
    public CustomEmailAdapter(Context context, ArrayList<EmailModel> emailModelArrayList){
        this.context = context;
        this.arrayList = emailModelArrayList;
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
        final EmailModel emailModel = arrayList.get(i);
        TextView tv_email_subject, tv_email_from, tv_date;
        LinearLayout ll_1;
        ImageView img_attachment, img_email;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.list_email_home_row,viewGroup,false);
        tv_email_subject = view.findViewById(R.id.tv_email_subject);
        tv_email_from = view.findViewById(R.id.tv_email_from);
        tv_date = view.findViewById(R.id.tv_date);
        img_attachment = view.findViewById(R.id.img_attachment);
        img_email = view.findViewById(R.id.img_email);
        ll_1 = view.findViewById(R.id.ll_1);



        tv_email_subject.setText(arrayList.get(i).getSubject());
        tv_email_from.setText("From: "+arrayList.get(i).getFrom());

        //-------DateFormat code starts---------
        String date_output="";
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = originalFormat.parse(arrayList.get(i).getDate());
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            date_output = outputFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        //-------DateFormat code ends---------

        tv_date.setText(date_output);

        if(arrayList.get(i).getAttachment_yn().trim().contentEquals("1")){
            img_attachment.setVisibility(View.VISIBLE);
        }else if(!arrayList.get(i).getAttachment_yn().trim().contentEquals("0")){
            img_attachment.setVisibility(View.INVISIBLE);
        }


        if(arrayList.get(i).getRead_yn().trim().contentEquals("1")){
//            img_next.setBackground(ContextCompat.getDrawable(context,R.drawable.menu_arrow));
            img_email.setBackground(ContextCompat.getDrawable(context,R.drawable.emailread));
            ll_1.setBackgroundColor(Color.parseColor("#F8F8F8"));
        }else if(arrayList.get(i).getRead_yn().trim().contentEquals("0")){
//            img_next.setBackground(ContextCompat.getDrawable(context,R.drawable.menu_arrow_gray));
            img_email.setBackground(ContextCompat.getDrawable(context,R.drawable.emailunread));
            ll_1.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        Log.d("Readyn-=>",arrayList.get(i).getRead_yn());
        return view;
    }
}
