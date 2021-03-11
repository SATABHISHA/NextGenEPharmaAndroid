package org.arb.Nextgen.ePharma.Model;

public class MWRMsr1Msr2DoctorModel {
    String id, ecl_no, name, last_visit_date, status, work_place_id;
    Boolean checked;

    //----Getter method, starts-----

    public String getId() {
        return id;
    }

    public String getEcl_no() {
        return ecl_no;
    }

    public String getName() {
        return name;
    }

    public String getLast_visit_date() {
        return last_visit_date;
    }

    public String getStatus() {
        return status;
    }

    public String getWork_place_id() {
        return work_place_id;
    }

    public Boolean getChecked() {
        return checked;
    }
    //----Getter method, ends-----

    //----Setter method, starts-----

    public void setId(String id) {
        this.id = id;
    }

    public void setEcl_no(String ecl_no) {
        this.ecl_no = ecl_no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLast_visit_date(String last_visit_date) {
        this.last_visit_date = last_visit_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWork_place_id(String work_place_id) {
        this.work_place_id = work_place_id;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
    //----Setter method, ends-----
}
