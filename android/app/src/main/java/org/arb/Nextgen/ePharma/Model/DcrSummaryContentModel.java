package org.arb.Nextgen.ePharma.Model;

public class DcrSummaryContentModel {
    String id = "", ecl_no = "", name = "", status = "", edit_text_amt = "", work_place_id = "", type = "", work_place_name = "", dcr_last_day_visit = "";
    Integer serial_no_new_demand;

    //====================Getter Method starts================

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

    public String getEdit_text_amt() {
        return edit_text_amt;
    }

    public String getWork_place_id() {
        return work_place_id;
    }

    public String getType() {
        return type;
    }

    public String getWork_place_name() {
        return work_place_name;
    }

    public String getDcr_last_day_visit() {
        return dcr_last_day_visit;
    }

    public Integer getSerial_no_new_demand() {
        return serial_no_new_demand;
    }

    //====================Getter Method ends================


    //====================Setter Method starts================

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

    public void setEdit_text_amt(String edit_text_amt) {
        this.edit_text_amt = edit_text_amt;
    }

    public void setWork_place_id(String work_place_id) {
        this.work_place_id = work_place_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWork_place_name(String work_place_name) {
        this.work_place_name = work_place_name;
    }

    public void setDcr_last_day_visit(String dcr_last_day_visit) {
        this.dcr_last_day_visit = dcr_last_day_visit;
    }

    public void setSerial_no_new_demand(Integer serial_no_new_demand) {
        this.serial_no_new_demand = serial_no_new_demand;
    }
    //====================Setter Method ends================

}
