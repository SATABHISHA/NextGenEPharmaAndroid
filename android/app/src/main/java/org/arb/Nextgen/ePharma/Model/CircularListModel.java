package org.arb.Nextgen.ePharma.Model;

public class CircularListModel {
    String id_circular, circular_no, circular_name, description, publish_date, expire_date, attachment_file, attachment_download_link, read_yn;

    //------------Getter method starts---------

    public String getId_circular() {
        return id_circular;
    }

    public String getCircular_no() {
        return circular_no;
    }

    public String getCircular_name() {
        return circular_name;
    }

    public String getDescription() {
        return description;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public String getAttachment_file() {
        return attachment_file;
    }

    public String getAttachment_download_link() {
        return attachment_download_link;
    }

    public String getRead_yn() {
        return read_yn;
    }
    //------------Getter method ends---------

    //------------Setter method ends---------

    public void setId_circular(String id_circular) {
        this.id_circular = id_circular;
    }

    public void setCircular_no(String circular_no) {
        this.circular_no = circular_no;
    }

    public void setCircular_name(String circular_name) {
        this.circular_name = circular_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public void setAttachment_file(String attachment_file) {
        this.attachment_file = attachment_file;
    }

    public void setAttachment_download_link(String attachment_download_link) {
        this.attachment_download_link = attachment_download_link;
    }

    public void setRead_yn(String read_yn) {
        this.read_yn = read_yn;
    }
//------------Setter method ends---------
}
