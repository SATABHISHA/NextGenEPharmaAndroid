package org.arb.Nextgen.ePharma.Model;

public class LocationDetailsSqliteDataModel {
    String date, time, longitude, latitude, address, id;

    //-------------Getter Method starts---------
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }
    //-------------Getter Method ends---------

    //-------------Setter method starts--------

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setId(String id) {
        this.id = id;
    }
//-------------Setter method ends--------
}
