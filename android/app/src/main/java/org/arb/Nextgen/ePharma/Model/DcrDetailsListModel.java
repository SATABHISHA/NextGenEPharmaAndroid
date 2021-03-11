package org.arb.Nextgen.ePharma.Model;

public class DcrDetailsListModel {
    String hq_id, hq_name, id, name, managers_id, managers_name, managers_designation_id, managers_designation, status;

    //===============Getter Method starts=========

    //----Work_Place
    public String getHq_id() {
        return hq_id;
    }

    public String getHq_name() {
        return hq_name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    //-----managers

    public String getManagers_id() {
        return managers_id;
    }

    public String getManagers_name() {
        return managers_name;
    }

    public String getManagers_designation_id() {
        return managers_designation_id;
    }

    public String getManagers_designation() {
        return managers_designation;
    }

    public String getStatus() {
        return status;
    }
    //===============Getter Method ends=========


    //===============Setter Method starts=========

    //----Work_Place
    public void setHq_id(String hq_id) {
        this.hq_id = hq_id;
    }

    public void setHq_name(String hq_name) {
        this.hq_name = hq_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    //-----managers

    public void setManagers_id(String managers_id) {
        this.managers_id = managers_id;
    }

    public void setManagers_name(String managers_name) {
        this.managers_name = managers_name;
    }

    public void setManagers_designation_id(String managers_designation_id) {
        this.managers_designation_id = managers_designation_id;
    }

    public void setManagers_designation(String managers_designation) {
        this.managers_designation = managers_designation;
    }

    public void setStatus(String status) {
        this.status = status;
    }
//===============Setter Method ends=========
}
