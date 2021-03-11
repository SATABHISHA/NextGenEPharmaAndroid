package org.arb.Nextgen.ePharma.Model;

public class DcrSelectDoctorStockistChemistModel {
    String id, ecl_no, name, status, work_place_id;

    //--newly added
    String last_visit_date, amount = "0.00", work_place_name;

    //===============Getter Method, starts===========

    public String getId() {
        return id;
    }

    public String getEcl_no() {
        return ecl_no;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getWork_place_id() {
        return work_place_id;
    }

    public String getLast_visit_date() {
        return last_visit_date;
    }

    public String getAmount() {
        return amount;
    }

    public String getWork_place_name() {
        return work_place_name;
    }
    //===============Getter Method, ends===========

    //===============Setter Method, starts===========

    public void setId(String id) {
        this.id = id;
    }

    public void setEcl_no(String ecl_no) {
        this.ecl_no = ecl_no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWork_place_id(String work_place_id) {
        this.work_place_id = work_place_id;
    }

    public void setLast_visit_date(String last_visit_date) {
        this.last_visit_date = last_visit_date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setWork_place_name(String work_place_name) {
        this.work_place_name = work_place_name;
    }
    //===============Setter Method, ends===========
}
