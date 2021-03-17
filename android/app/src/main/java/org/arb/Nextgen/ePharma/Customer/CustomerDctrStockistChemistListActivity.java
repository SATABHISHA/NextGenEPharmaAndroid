package org.arb.Nextgen.ePharma.Customer;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.R;

public class CustomerDctrStockistChemistListActivity extends AppCompatActivity {
    TextView tv_bar_item_title, tv_bar_item_count;
    RecyclerView recycler_view;
    LinearLayout ll_recycler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dctr_stckst_chemist_details);

        ll_recycler = findViewById(R.id.ll_recycler);
        tv_bar_item_title = findViewById(R.id.tv_bar_item_title);
        tv_bar_item_title.setText(CustomerHomeActivity.customer_type);

        tv_bar_item_count = findViewById(R.id.tv_bar_item_count);

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======
    }
}
