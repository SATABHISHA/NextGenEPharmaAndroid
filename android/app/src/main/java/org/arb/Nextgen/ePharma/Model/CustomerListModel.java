package org.arb.Nextgen.ePharma.Model;

public class CustomerListModel {
    public String dctr_chemist_stockist_id, ecl_no, name, work_place_id, work_place_name, speciality, customer_class, latitude, longitude, location_address, type;
    public Integer geo_tagged_yn, synced_yn;

    //--------getter method starts------

    public String getDctr_chemist_stockist_id() {
        return dctr_chemist_stockist_id;
    }

    public String getEcl_no() {
        return ecl_no;
    }

    public String getName() {
        return name;
    }

    public String getWork_place_id() {
        return work_place_id;
    }

    public String getWork_place_name() {
        return work_place_name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getCustomer_class() {
        return customer_class;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLocation_address() {
        return location_address;
    }

    public String getType() {
        return type;
    }

    public Integer getGeo_tagged_yn() {
        return geo_tagged_yn;
    }

    public Integer getSynced_yn() {
        return synced_yn;
    }

    //--------getter method ends------

    //--------setter method starts------

    public void setDctr_chemist_stockist_id(String dctr_chemist_stockist_id) {
        this.dctr_chemist_stockist_id = dctr_chemist_stockist_id;
    }

    public void setEcl_no(String ecl_no) {
        this.ecl_no = ecl_no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWork_place_id(String work_place_id) {
        this.work_place_id = work_place_id;
    }

    public void setWork_place_name(String work_place_name) {
        this.work_place_name = work_place_name;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setCustomer_class(String customer_class) {
        this.customer_class = customer_class;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGeo_tagged_yn(Integer geo_tagged_yn) {
        this.geo_tagged_yn = geo_tagged_yn;
    }

    public void setSynced_yn(Integer synced_yn) {
        this.synced_yn = synced_yn;
    }

    //--------setter method ends------
}
