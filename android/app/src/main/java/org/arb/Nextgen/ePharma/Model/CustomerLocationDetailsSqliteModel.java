package org.arb.Nextgen.ePharma.Model;

public class CustomerLocationDetailsSqliteModel {
    String dctr_chemist_stockist_id, latitude, longitude, address, date, time;
    Integer id;

    //==========Getter method, starts=======

    public Integer getId() {
        return id;
    }

    public String getDctr_chemist_stockist_id() {
        return dctr_chemist_stockist_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    //==========Getter method, starts=======

    //==========Setter method, starts=======

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDctr_chemist_stockist_id(String dctr_chemist_stockist_id) {
        this.dctr_chemist_stockist_id = dctr_chemist_stockist_id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
//==========Setter method, starts=======
}
