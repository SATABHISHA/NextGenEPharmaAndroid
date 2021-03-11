package org.arb.Nextgen.ePharma.Model;

public class DocumentListModel {
    String doc_name, description, extension, size, download_link, upload_date, cal_year_id;

    //==============Getter method starts=======


    public String getDoc_name() {
        return doc_name;
    }

    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }

    public String getSize() {
        return size;
    }

    public String getDownload_link() {
        return download_link;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public String getCal_year_id() {
        return cal_year_id;
    }

    //==============Getter method ends=======

    //==============Setter method starts=======


    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public void setCal_year_id(String cal_year_id) {
        this.cal_year_id = cal_year_id;
    }

    //==============Setter method ends=======
}
