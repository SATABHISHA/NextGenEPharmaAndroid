package org.arb.Nextgen.ePharma.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.arb.Nextgen.ePharma.Home.HomeActivity;
import org.arb.Nextgen.ePharma.R;

public class CustomerHomeActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlDoctor, rlChemist, rlStockists;
    public static String customer_type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        rlDoctor = findViewById(R.id.rlDoctor);
        rlChemist = findViewById(R.id.rlChemist);
        rlStockists = findViewById(R.id.rlStockists);

        rlDoctor.setOnClickListener(this);
        rlChemist.setOnClickListener(this);
        rlStockists.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlDoctor:

                customer_type = "Doctors";
                Intent intent_customers_doctor = new Intent(CustomerHomeActivity.this, CustomerDctrStockistChemistListActivity.class);
                intent_customers_doctor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_customers_doctor);

                break;
            case R.id.rlChemist:
                customer_type = "Chemists";
                Intent intent_customers_chemists = new Intent(CustomerHomeActivity.this, CustomerDctrStockistChemistListActivity.class);
                intent_customers_chemists.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_customers_chemists);

                break;
            case R.id.rlStockists:
                customer_type = "Stockists";
                Intent intent_customers_stockists = new Intent(CustomerHomeActivity.this, CustomerDctrStockistChemistListActivity.class);
                intent_customers_stockists.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_customers_stockists);

                break;
            default:
                break;
        }
    }
}
