package org.arb.Nextgen.ePharma.Model;

public class TrackingDetailsMapModel {
    String address, latitude, longitude, time;

    //--------Getter method starts------

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTime() {
        return time;
    }

    //--------Getter method ends------

    //--------Setter method starts------

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //--------Setter method ends------
}
