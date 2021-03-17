package org.arb.Nextgen.ePharma.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.arb.Nextgen.ePharma.Model.UserSingletonModel;
import org.arb.Nextgen.ePharma.adapter.DcrSelectChemistAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrSelectDoctorAdapter;
import org.arb.Nextgen.ePharma.adapter.DcrSelectStockistAdapter;
import org.arb.Nextgen.ePharma.DCR.DcrHome;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SqliteDb {
    UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
//    public  SQLiteDatabase db;
    public  void createDatabase(SQLiteDatabase db){
        try {
            db = db.openOrCreateDatabase("EPharmaDb", null);
            db.execSQL("CREATE TABLE IF NOT EXISTS DCR(id integer PRIMARY KEY AUTOINCREMENT, dcr_id VARCHAR, dcr_no VARCHAR, msr_id VARCHAR, dcr_date VARCHAR, cal_year_id VARCHAR, json_doctor VARCHAR, json_chemist VARCHAR, json_stockist VARCHAR, final_json VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDatabaseMWR(SQLiteDatabase db){
        try{
            db = db.openOrCreateDatabase("EPharmaDbMWR",null);
            db.execSQL("CREATE TABLE IF NOT EXISTS MWR(id INTEGER PRIMARY KEY AUTOINCREMENT, mwr_id VARCHAR, mwr_no VARCHAR, mwr_date VARCHAR, week_day VARCHAR, manager_id VARCHAR, cal_year_id VARCHAR, base_work_place_id VARCHAR, msr_1_id VARCHAR, msr_1_work_place VARCHAR, msr_1_doctor VARCHAR, msr_2_id VARCHAR, msr_2_work_place VARCHAR, msr_2_doctor, json VARCHAR, draft_yn VARCHAR)");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //---function for dcr code starts-----

    public void insertDataDCR(String dcr_id, String dcr_no, String msr_id, String dcr_date, String cal_year_id, String json_doctor, String json_chemist, String json_stockist, String final_json, String draft_yn, String misc, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("dcr_id", dcr_id);
        values.put("dcr_no", dcr_no);
        values.put("msr_id", msr_id);
        values.put("dcr_date", dcr_date);
        values.put("cal_year_id", cal_year_id);
        values.put("json_doctor", json_doctor);
        values.put("json_chemist", json_chemist);
        values.put("json_stockist", json_stockist);
        values.put("final_json", final_json);
        values.put("draft_yn", draft_yn);
        values.put("misc", misc);
        if ((db.insert("DCR", null, values)) != -1) {
//                Toast.makeText(getApplicationContext(), "Inserted...", Toast.LENGTH_LONG).show();
            Log.d("Test Db", "Data Doctor/Chemist/Stockist inserted");
        } else {
//                Toast.makeText(getApplicationContext(),"Error...",Toast.LENGTH_LONG).show();
            Log.d("Test Db", "Data Doctor not inserted");
        }
    }

    public void updateDCR(String type, String json, String msr_id, String dcr_id, String dcr_no, String dcr_date, String cal_year_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();

        if(type == "Doctor"){
            cv.put("json_doctor",json); //These Fields should be your String values of actual column names
           /* String sql = "UPDATE DCR SET json_doctor = '"+json+"'  WHERE msr_id ="+msr_id;
            db.execSQL(sql);*/
        }else if(type == "Chemist"){
            cv.put("json_chemist",json);
           /* String sql = "UPDATE DCR SET json_chemist = '"+json+"'  WHERE msr_id ="+msr_id;
            db.execSQL(sql);*/
        }else if(type == "Stockist"){
            cv.put("json_stockist",json);
            /*String sql = "UPDATE DCR SET json_stockist = '"+json+"'  WHERE msr_id ="+msr_id;
            db.execSQL(sql);*/
        }else if(type == "FinalJson"){
            cv.put("final_json",json);
            cv.put("draft_yn","Y");
        }else if(type == "Misc"){
            cv.put("misc",json);
        }
//        db.updateDCR("DCR", cv, "msr_id = ?" , new String[] {msr_id});
        db.update("DCR", cv, "msr_id = ? AND dcr_id = ? AND dcr_no = ? AND dcr_date = ? AND cal_year_id = ?" , new String[] {msr_id, dcr_id, dcr_no, dcr_date, cal_year_id});
//        db.updateDCR("DCR", cv, "msr_id="+msr_id, null);
//        db.updateDCR("DCR", cv, msr_id + "= ?", new String[] {msr_id});
    }

    public void updateDoctor(String json, String msr_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("json_doctor",json);
        db.update("DCR", cv, "msr_id="+msr_id, null);
        db.close();
    }

    public void updateChemist(String json, String msr_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("json_chemist",json);
        db.update("DCR", cv, "msr_id="+msr_id, null);
        db.close();
    }

    public void updateStockist(String json, String msr_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("json_stockist",json);
        db.update("DCR", cv, "msr_id="+msr_id, null);
        db.close();
    }
    
    public void deleteDCR(String msr_id, String dcr_id, String dcr_no, String dcr_date, String cal_year_id, SQLiteDatabase db){
        String d = "DELETE FROM DCR where msr_id = '"+msr_id+"' AND dcr_id = '"+dcr_id+"' AND dcr_no = '"+dcr_no+"' AND dcr_date = '"+dcr_date+"' AND cal_year_id = '"+cal_year_id+"'";
        db.execSQL(d);
    }
    public String fetch(int column_index, String msr_id, String dcr_id, String dcr_no, String dcr_date, String cal_year_id, SQLiteDatabase db) {
        String json = "";
        Cursor c = db.rawQuery("SELECT * FROM DCR where msr_id = '"+msr_id+"' AND dcr_id = '"+dcr_id+"' AND dcr_no = '"+dcr_no+"' AND dcr_date = '"+dcr_date+"' AND cal_year_id = '"+cal_year_id+"'", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
       if(c!=null) {
           while (!c.isAfterLast()) {
               json = c.getString(column_index);
               c.moveToNext();
           }
       }
        return json;
    }
//--added on 2nd feb, code starts
    public int count(int column_index, String msr_id, String dcr_id, String dcr_no, String dcr_date, String cal_year_id, SQLiteDatabase db){
        int count = 0;
        String json = "";
        Cursor c = db.rawQuery("SELECT * FROM DCR where msr_id = '"+msr_id+"' AND dcr_id = '"+dcr_id+"' AND dcr_no = '"+dcr_no+"' AND dcr_date = '"+dcr_date+"' AND cal_year_id = '"+cal_year_id+"'", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
        if(c!=null) {
            while (!c.isAfterLast()) {
                json = c.getString(column_index);
                c.moveToNext();
            }
        }
        if(json!=null) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                JSONArray jsonArray = jsonObject.getJSONArray("values");
                /*for (int i = 1; i <= jsonArray.length(); i++) {
                    count = count + i;
                }*/
                count = jsonArray.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
//--added on 2nd feb, code ends

    //-----added on 17th march count of masterdata, so thate we can delete previous data to avoid redundancy, code starts
    public int countMasterData(SQLiteDatabase db){
        int count = 0;
        Cursor c = db.rawQuery("SELECT * FROM TB_CUSTOMER", null);
        c.moveToFirst();
        if(c!=null){
            while (!c.isAfterLast()){
                count = count + 1;
                c.moveToNext();
            }
        }
        return count;
    }

    public void deleteMasterData(SQLiteDatabase db){
        String d = "DELETE FROM TB_CUSTOMER";
        db.execSQL(d);
    }
    //-----added on 17th march count of masterdata, so thate we can delete previous data to avoid redundancy, code ends
    public String fetchDraftDCR(int column_index, String msr_id, String dcr_id, String dcr_no, String dcr_date, String cal_year_id, SQLiteDatabase db){
        String check_draft_yn = "";
        Cursor c = db.rawQuery("SELECT * FROM DCR where msr_id = '"+msr_id+"' AND dcr_id = '"+dcr_id+"' AND dcr_no = '"+dcr_no+"' AND dcr_date = '"+dcr_date+"' AND cal_year_id = '"+cal_year_id+"' AND draft_yn = 'Y' ", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
        if(c!=null) {
            while (!c.isAfterLast()) {
                check_draft_yn = c.getString(column_index);
                c.moveToNext();
            }
        }
        return check_draft_yn;
    }

    public ArrayList<String> fetchDraftDateDCR(ArrayList<String> arrayList, SQLiteDatabase db){
        arrayList = new ArrayList<>();
        if(!arrayList.isEmpty()){
            arrayList.clear();
        }
        Cursor c = db.rawQuery("SELECT * FROM DCR",null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
        if(c!=null) {
            while (!c.isAfterLast()) {
                arrayList.add(c.getString(4));
                c.moveToNext();
            }
        }
        return arrayList;
    }

    public void cleanDataDCR(String msr_id, String dcr_id, String dcr_no, String dcr_date, String cal_year_id, SQLiteDatabase db){
        userSingletonModel.setCheck_draft_saved_last_yn("N");
        String d = "DELETE FROM DCR where msr_id = '"+msr_id+"' AND dcr_id = '"+dcr_id+"' AND dcr_no = '"+dcr_no+"' AND dcr_date = '"+dcr_date+"' AND cal_year_id = '"+cal_year_id+"'";
        db.execSQL(d);
        db.close();
        //---clearing all adapter arraylist
        if(!DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectDoctorAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!DcrSelectChemistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectChemistAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.isEmpty()) {
            DcrSelectStockistAdapter.dcrSelectDoctorStockistChemistModelArrayList.clear();
        }
        if(!DcrHome.workedWithArrayListManagersForDcrSummary.isEmpty()){
            DcrHome.workedWithArrayListManagersForDcrSummary.clear();
        }
    }

     //---function for dcr code ends-----


    //----functions for mwr code starts----

    public void insertDataMWR(String mwr_id, String mwr_no, String mwr_date, String week_day, String manager_id, String cal_year_id, String base_work_place_id, String msr_1_id, String msr_1_work_place, String msr_1_doctor, String msr_2_id, String msr_2_work_place, String msr_2_doctor, String json, String draft_yn, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("mwr_id", mwr_id);
        values.put("mwr_no", mwr_no);
        values.put("mwr_date", mwr_date);
        values.put("week_day", week_day);
        values.put("manager_id", manager_id);
        values.put("cal_year_id", cal_year_id);
        values.put("base_work_place_id", base_work_place_id);
        values.put("msr_1_id", msr_1_id);
        values.put("msr_1_work_place", msr_1_work_place);
        values.put("msr_1_doctor", msr_1_doctor);
        values.put("msr_2_id", msr_2_id);
        values.put("msr_2_work_place", msr_2_work_place);
        values.put("msr_2_doctor", msr_2_doctor);
        values.put("json", json);
        values.put("draft_yn", draft_yn);
        if ((db.insert("MWR", null, values)) != -1) {
//                Toast.makeText(getApplicationContext(), "Inserted...", Toast.LENGTH_LONG).show();
            Log.d("Test Db", "Data Doctor/MWR1/MWR2 inserted");
        } else {
//                Toast.makeText(getApplicationContext(),"Error...",Toast.LENGTH_LONG).show();
            Log.d("Test Db", "Data Doctor/MWR1/MWR2 not inserted");
        }
    }

    public void deleteMWR(SQLiteDatabase db){
        String d = "DELETE FROM MWR";
        db.execSQL(d);
    }

    public void updateMWRDateAndWeekDay(String mwr_no, String mwr_date, String week_day, String manager_id, String cal_year_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("mwr_date",mwr_date); //These Fields should be your String values of actual column names
        cv.put("week_day",week_day); //These Fields should be your String values of actual column names

        db.update("MWR", cv, "mwr_no = ? AND manager_id = ? AND cal_year_id = ?" , new String[] {mwr_no, manager_id, cal_year_id});
    }

    public void updateMWRWorkPlaceId(String mwr_no, String mwr_date, String manager_id, String cal_year_id, String base_work_place_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("base_work_place_id", base_work_place_id); //These Fields should be your String values of actual column names

        db.update("MWR", cv, "mwr_no = ? AND manager_id = ? AND cal_year_id = ? AND mwr_date = ?" , new String[] {mwr_no, manager_id, cal_year_id, mwr_date});
    }

    public void updateMWRMsr1Msr2Name(String type, String mwr_no, String mwr_date, String manager_id, String cal_year_id, String msr_1_or_2_id, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        if(type == "msr_1") {
            cv.put("msr_1_id", msr_1_or_2_id); //These Fields should be your String values of actual column names
        }else if(type == "msr_2"){
            cv.put("msr_2_id", msr_1_or_2_id); //These Fields should be your String values of actual column names
        }

        db.update("MWR", cv, "mwr_no = ? AND manager_id = ? AND cal_year_id = ? AND mwr_date = ?" , new String[] {mwr_no, manager_id, cal_year_id, mwr_date});
    }

    public void updateMWRMsr1Msr2WorkPlaceId(String type, String mwr_no, String mwr_date, String manager_id, String cal_year_id, ArrayList<String> msr_1_2_work_place, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        if(type == "msr_1") {
            cv.put("msr_1_work_place", msr_1_2_work_place.toString()); //These Fields should be your String values of actual column names
        }else if(type == "msr_2"){
            cv.put("msr_2_work_place", msr_1_2_work_place.toString()); //These Fields should be your String values of actual column names
        }

        db.update("MWR", cv, "mwr_no = ? AND manager_id = ? AND cal_year_id = ? AND mwr_date = ?" , new String[] {mwr_no, manager_id, cal_year_id, mwr_date});
    }

    public String fetch_mwr_base_workPlaceId_msr1_msr2_workPlaceId_msr1_msr2Id_mwrId_msr1_msr2_doctor(int column_index, String mwr_no, String mwr_date, String manager_id, String cal_year_id, SQLiteDatabase db){
        String basework_place_msr1_msr2_work_place_id_msr1_msr2_id = "";
        Cursor c = db.rawQuery("SELECT * FROM MWR where mwr_no = '"+mwr_no+"' AND mwr_date = '"+mwr_date+"' AND manager_id = '"+manager_id+"' AND cal_year_id = '"+cal_year_id+"' ", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
        if(c!=null) {
            while (!c.isAfterLast()) {
                if(c.isNull(column_index)){
                    basework_place_msr1_msr2_work_place_id_msr1_msr2_id = "";
                }else {
                    basework_place_msr1_msr2_work_place_id_msr1_msr2_id = c.getString(column_index);
                }
                c.moveToNext();
            }
        }
        return basework_place_msr1_msr2_work_place_id_msr1_msr2_id;
    }

    public void updateMWRMsr1Msr2Doctor(String type, String mwr_no, String mwr_date, String manager_id, String cal_year_id, String msr_1_2_doctor_json, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        if(type == "msr_1") {
            cv.put("msr_1_doctor", msr_1_2_doctor_json); //These Fields should be your String values of actual column names
        }else if(type == "msr_2"){
            cv.put("msr_2_doctor", msr_1_2_doctor_json); //These Fields should be your String values of actual column names
        }

        db.update("MWR", cv, "mwr_no = ? AND manager_id = ? AND cal_year_id = ? AND mwr_date = ?" , new String[] {mwr_no, manager_id, cal_year_id, mwr_date});
    }

    public String fetchMWRMsr1Msr2Doctor(int column_index, String mwr_no, String mwr_date, String manager_id, String cal_year_id, SQLiteDatabase db){
        String msr1_msr2_doctor = "";
        Cursor c = db.rawQuery("SELECT * FROM MWR where mwr_no = '"+mwr_no+"' AND mwr_date = '"+mwr_date+"' AND manager_id = '"+manager_id+"' AND cal_year_id = '"+cal_year_id+"' ", null);

//            TextView v=(TextView)findViewById(R.id.v);
        c.moveToFirst();
        if(c!=null) {
            while (!c.isAfterLast()) {
                if(c.isNull(column_index)){
                    msr1_msr2_doctor = "";
                }else {
                    msr1_msr2_doctor = c.getString(column_index);
                }
                c.moveToNext();
            }
        }
        return msr1_msr2_doctor;
    }
    //----functions for mwr code ends----
}
