package org.arb.Nextgen.ePharma.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.arb.Nextgen.ePharma.DCR.DcrHome;
import org.arb.Nextgen.ePharma.DCR.DcrSummary;
import org.arb.Nextgen.ePharma.Model.DcrSummaryContentModel;
import org.arb.Nextgen.ePharma.Model.DcrSummaryGroupNameModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpandableListDcrSummaryAdapter extends BaseExpandableListAdapter {
    Context context;
    public static ArrayList<String> arrayListEmail = new ArrayList<>();
    public static String weekDate;

    ArrayList<DcrSummaryGroupNameModel> dcrSummaryGroupNameModelArrayList;
    private HashMap<DcrSummaryGroupNameModel, ArrayList<DcrSummaryContentModel>> dcrSummaryContentArrayList;
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();

    public ExpandableListDcrSummaryAdapter(DcrSummary context, ArrayList<DcrSummaryGroupNameModel> dcrSummaryGroupNameModelArrayList,
                                                       HashMap<DcrSummaryGroupNameModel, ArrayList<DcrSummaryContentModel>> dcrSummaryContentArrayList) {
        this.context = (Context) context;
        this.dcrSummaryGroupNameModelArrayList = dcrSummaryGroupNameModelArrayList;
        this.dcrSummaryContentArrayList = dcrSummaryContentArrayList;
    }


    public int getGroupCount() {
        return dcrSummaryGroupNameModelArrayList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.dcrSummaryContentArrayList.get(this.dcrSummaryGroupNameModelArrayList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.dcrSummaryGroupNameModelArrayList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.dcrSummaryContentArrayList.get(this.dcrSummaryGroupNameModelArrayList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
//        String headerTitle = (String) getGroup(i);
        final DcrSummaryGroupNameModel dcrSummaryGroupNameModel = (DcrSummaryGroupNameModel) getGroup(i);
       /* if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.listview_select_day_row_group, null);
        }*/

        LayoutInflater infalInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = infalInflater.inflate(R.layout.custom_row_group_dcr_summary, null);

        TextView tv_customer_type = (TextView)view.findViewById(R.id.tv_customer_type);
        TextView tv_gift_amount = (TextView)view.findViewById(R.id.tv_gift_amount);

        tv_customer_type.setText(dcrSummaryGroupNameModel.getGroup_name()+"(s)");
        tv_gift_amount.setText(dcrSummaryGroupNameModel.getGroup_amount());


        return view;
    }
    static class ViewHolder {
        protected View et;



        public void addView(View et){
            this.et = et;
        }
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
//        final String childText = (String) getChild(i, i1);
        final DcrSummaryContentModel dcrSummaryContentModel = (DcrSummaryContentModel) getChild(i,i1);
        final DcrSummaryGroupNameModel dcrSummaryGroupNameModel = (DcrSummaryGroupNameModel)getGroup(i);

        /*ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.custom_row_pending_item_not_started, null);
            viewHolder = new ViewHolder();
            viewHolder.addView(view
                    .findViewById(R.id.ed_amount));
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }*/
        LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = infalInflater.inflate(R.layout.custom_row_dcr_summary, null);
        TextView tv_name, tv_last_visited, tv_workplace ;
        final EditText ed_amount;
        RelativeLayout relative_layout;
        Button imgbtn_not_started, imgbtn_view;
        ImageView img_not_started_profile_pic;
        CoordinatorLayout coordinator_layout_pendingItems;

        ImageButton imgbtn_add;
        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_last_visited = view.findViewById(R.id.tv_last_visited);
        tv_workplace = view.findViewById(R.id.tv_workplace);
        ed_amount = view.findViewById(R.id.ed_amount);
        ed_amount.setText(dcrSummaryContentModel.getEdit_text_amt());

        if(DcrHome.dcrStatus == 1){

            /*ed_amount.setClickable(false);
            ed_amount.setEnabled(false);
            ed_amount.setFocusable(false);
            ed_amount.setFocusableInTouchMode(false);*/ //--commented on 2nd feb21

            //--added on 2nd feb 21, starts
            if(DcrHome.check_draft_yn_for_summary.contentEquals("N")) {
                ed_amount.setClickable(false);
                ed_amount.setEnabled(false);
                ed_amount.setFocusable(false);
                ed_amount.setFocusableInTouchMode(false);
            }else if(DcrHome.check_draft_yn_for_summary.contentEquals("Y")){
                ed_amount.setClickable(true);
                ed_amount.setEnabled(true);
                ed_amount.setFocusable(true);
                ed_amount.setFocusableInTouchMode(true);
            }
            //--added on 2nd feb 21, ends
        }else if(DcrHome.dcrStatus == 0){
            ed_amount.setClickable(true);
            ed_amount.setEnabled(true);
            ed_amount.setFocusable(true);
            ed_amount.setFocusableInTouchMode(true);
        }

        ed_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dcrSummaryContentModel.setEdit_text_amt(ed_amount.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ed_amount.setText(dcrSummaryContentModel.getEdit_text_amt());
//        tv_name.setText(dcrSummaryContentModel.getName_first().toString() + "\n"+ dcrSummaryContentModel.getName_last().toString());


        /*String currentString = dcrSummaryContentModel.getName();
        String[] separated = currentString.split("\\(");
        tv_name.setText(separated[0]);*/


        /*String currentStringWorkPlace = separated[1];
        String[] separatedWorkPlace = currentStringWorkPlace.split("\\)");
        tv_workplace.setText(separatedWorkPlace[0]);*/
        String serial_no_new_demand = String.valueOf(dcrSummaryContentModel.getSerial_no_new_demand());
        if(DcrHome.tempForDcrSummaryBack == 0){
            String currentString = dcrSummaryContentModel.getName();
            if(currentString.contains("(")){
                String[] separated = currentString.split("\\(");
                tv_name.setText(serial_no_new_demand+") "+separated[0]);

                String currentStringWorkPlace = separated[1];
                String[] separatedWorkPlace = currentStringWorkPlace.split("\\)");
                tv_workplace.setText(separatedWorkPlace[0]);
            }else{
                tv_name.setText(serial_no_new_demand+") "+dcrSummaryContentModel.getName());
                tv_workplace.setText(dcrSummaryContentModel.getWork_place_name());
            }

        }else{
            tv_name.setText(serial_no_new_demand+") "+dcrSummaryContentModel.getName());
            tv_workplace.setText(dcrSummaryContentModel.getWork_place_name());
        }



//        tv_name.setText(dcrSummaryContentModel.getName());
//        tv_last_visited.setText("Last visited on 24th-Aug-2019");
//        tv_last_visited.setText("Last visited on : "+userSingletonModel.getSelected_date_calendar_forapi_format());
        if(dcrSummaryContentModel.getDcr_last_day_visit().contentEquals("")){
            tv_last_visited.setVisibility(View.GONE);
        }else {
            tv_last_visited.setVisibility(View.VISIBLE);
            tv_last_visited.setText("Last visited on : " + dcrSummaryContentModel.getDcr_last_day_visit());
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
