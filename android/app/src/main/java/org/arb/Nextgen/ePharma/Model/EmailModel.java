package org.arb.Nextgen.ePharma.Model;

public class EmailModel {
    String id_mail_inbox, from, subject, date, read_yn, status, attachment_yn;

    //--------------Getter method starts----------

    public String getId_mail_inbox() {
        return id_mail_inbox;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getRead_yn() {
        return read_yn;
    }

    public String getStatus() {
        return status;
    }

    public String getAttachment_yn() {
        return attachment_yn;
    }

    //--------------Getter method ends----------

    //--------------Setter method starts----------

    public void setId_mail_inbox(String id_mail_inbox) {
        this.id_mail_inbox = id_mail_inbox;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRead_yn(String read_yn) {
        this.read_yn = read_yn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttachment_yn(String attachment_yn) {
        this.attachment_yn = attachment_yn;
    }

    //--------------Setter method ends----------
}
