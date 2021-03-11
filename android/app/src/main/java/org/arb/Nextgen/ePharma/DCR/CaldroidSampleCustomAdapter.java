package org.arb.Nextgen.ePharma.DCR;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import org.arb.Nextgen.ePharma.R;

import java.util.Map;

import hirondelle.date4j.DateTime;

public class CaldroidSampleCustomAdapter extends CaldroidGridAdapter {


    public CaldroidSampleCustomAdapter(Context context, int month, int year,
                                       Map<String, Object> caldroidData,
                                       Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;


        // Get your data here
        //ArrayList data = (ArrayList) extraData.get("hours");
        String data1 = (String) extraData.get("hours");

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_cell, null);
        }

        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
        TextView tv2 = (TextView) cellView.findViewById(R.id.tv2);

        tv1.setTextColor(Color.BLACK); //---changed from white color to black on May2nd 19

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            tv1.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            tv1.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
//                cellView.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
//                ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ffffff"));
//                cellView.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
            }

        } else {
            shouldResetDiabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            cellView.setBackgroundColor(resources.getColor(com.caldroid.R.color.caldroid_sky_blue));
            tv1.setTextColor(Color.BLACK);

        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetDiabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
//                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
                cellView.setBackgroundResource(R.drawable.blue_border);
                tv1.setTextColor(Color.BLACK);
            } else {
//                cellView.setBackgroundResource(com.caldroid.R.drawable.disabled_cell_dark);
                cellView.setBackgroundResource(R.color.cellColor); //--added on man2nd 19

//                cellView.setBackgroundColor(Color.parseColor("#E6E6E6")); //--added on man2nd 19
            }
        }


        tv1.setText("" + dateTime.getDay());
        //tv2.setText(data1);

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);

        // Set custom color if required
//        cellView.setBackgroundColor(Color.parseColor("#E6E6E6")); //--commented on man2nd 19
//        tv1.setTextColor(Color.BLACK); //--commented on man2nd 19
        setCustomResources(dateTime, cellView, tv1);

        return cellView;
    }

}
