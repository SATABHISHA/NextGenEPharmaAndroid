package org.arb.Nextgen.ePharma.Customer;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.arb.Nextgen.ePharma.R;

public class CustomerHomeActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlDoctor, rlChemist, rlStockists;
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
                break;
            case R.id.rlChemist:
                break;
            case R.id.rlStockists:
                break;
            default:
                break;
        }
    }
}
