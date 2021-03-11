package org.arb.Nextgen.ePharma.Model;

public class MWRMsr1Msr2WorkPlaceModel {
    String id, name, hq_id, hq_name, status;
    Boolean checked;

    //-----Getter method starts---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHq_id() {
        return hq_id;
    }

    public String getHq_name() {
        return hq_name;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getChecked() {
        return checked;
    }

    //-----Getter method ends---


    //-----Setter method starts---

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHq_id(String hq_id) {
        this.hq_id = hq_id;
    }

    public void setHq_name(String hq_name) {
        this.hq_name = hq_name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    //-----Setter method ends---
}
