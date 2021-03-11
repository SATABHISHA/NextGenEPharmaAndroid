package org.arb.Nextgen.ePharma.Model;

public class MWRHomeModel {
    String id, mwrNo, week_date, week_start_date, week_end_date, status, status_desc;

    //=============Getter Method starts========

    public String getId() {
        return id;
    }

    public String getMwrNo() {
        return mwrNo;
    }

    public String getWeek_date() {
        return week_date;
    }

    public String getWeek_start_date() {
        return week_start_date;
    }

    public String getWeek_end_date() {
        return week_end_date;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    //=============Getter Method ends========



    //=============Setter Method starts========

    public void setId(String id) {
        this.id = id;
    }

    public void setMwrNo(String mwrNo) {
        this.mwrNo = mwrNo;
    }

    public void setWeek_date(String week_date) {
        this.week_date = week_date;
    }

    public void setWeek_start_date(String week_start_date) {
        this.week_start_date = week_start_date;
    }

    public void setWeek_end_date(String week_end_date) {
        this.week_end_date = week_end_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    //=============Setter Method ends========
}
