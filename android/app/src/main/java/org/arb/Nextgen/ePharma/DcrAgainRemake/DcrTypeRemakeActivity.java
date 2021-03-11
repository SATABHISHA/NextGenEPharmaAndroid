package org.arb.Nextgen.ePharma.DcrAgainRemake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.adapter.DcrRemakeAdapter.DcrTypeRemakeAdapter;
import org.arb.Nextgen.ePharma.Model.DcrTypeModel;
import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.R;

import java.util.ArrayList;

public class DcrTypeRemakeActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<DcrTypeModel> dcrTypeModelArrayList = new ArrayList<>();
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
    RecyclerView recycler_view;
    public static DcrTypeRemakeAdapter dcrTypeRemakeAdapter;
    public static String dcr_id="", dcr_type="";
    Button btn_apply, btn_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr_type_remake);
        btn_apply = findViewById(R.id.btn_apply);
        btn_cancel = findViewById(R.id.btn_cancel);
        dcrTypeRemakeAdapter = new DcrTypeRemakeAdapter(this,dcrTypeModelArrayList);

        //==========Recycler code initializing and setting layoutManager starts======
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        //==========Recycler code initializing and setting layoutManager ends======

        loadDcrType();

        btn_apply.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public void loadDcrType(){
        DcrTypeModel dcrTypeModel = new DcrTypeModel();
        dcrTypeModel.setId("0");
        dcrTypeModel.setDcr_type("Field Work");
        dcrTypeModelArrayList.add(dcrTypeModel);

        DcrTypeModel dcrTypeModel1 = new DcrTypeModel();
        dcrTypeModel1.setId("1");
        dcrTypeModel1.setDcr_type("Office Day");
        dcrTypeModelArrayList.add(dcrTypeModel1);

        DcrTypeModel dcrTypeModel2 = new DcrTypeModel();
        dcrTypeModel2.setId("2");
        dcrTypeModel2.setDcr_type("Travel");
        dcrTypeModelArrayList.add(dcrTypeModel2);

        DcrTypeModel dcrTypeModel3 = new DcrTypeModel();
        dcrTypeModel3.setId("3");
        dcrTypeModel3.setDcr_type("On Leave");
        dcrTypeModelArrayList.add(dcrTypeModel3);

        DcrTypeModel dcrTypeModel4 = new DcrTypeModel();
        dcrTypeModel4.setId("4");
        dcrTypeModel4.setDcr_type("Holiday");
        dcrTypeModelArrayList.add(dcrTypeModel4);

        DcrTypeModel dcrTypeModel6 = new DcrTypeModel();
        dcrTypeModel6.setId("6");
        dcrTypeModel6.setDcr_type("Other");
        dcrTypeModelArrayList.add(dcrTypeModel6);

        recycler_view.setAdapter(dcrTypeRemakeAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_apply:
                Intent intent_apply = new Intent(DcrTypeRemakeActivity.this, DcrDetailsRemakeActivity.class);
                intent_apply.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_apply);
                userSingletonModel.setDcr_details_dcr_type_id(dcr_id);
                userSingletonModel.setDcr_details_dcr_type_name(dcr_type);
                Log.d("dcr_id-=>",dcr_id);
                Log.d("dcr_type-=>",dcr_type);
                break;
            case R.id.btn_cancel:
                Intent intent_cancel = new Intent(DcrTypeRemakeActivity.this, DcrDetailsRemakeActivity.class);
                intent_cancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_cancel);
                break;
            default:
                break;
        }
    }
}
