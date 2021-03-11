package org.arb.Nextgen.ePharma.Model;

public class UserSingletonModel {
    String UserID, UserName, CompID, CorpID, CompanyName, SupervisorId, UserRole, AdminYN, PayableClerkYN, SupervisorYN, PurchaseYN,
            PayrollClerkYN, EmpName, UserType, EmailId, PwdSetterId, FinYearID, user_id_rss_pull_service;
    String abm_id, abm_name, designation_id, designation_name, designation_type, hq_id, hq_name, rbm_id, rbm_name, sm_id,
            sm_name, state, user_full_name, user_group_id, user_id, user_name, zbm_id, zbm_name, menu_list, selected_date_calendar, selected_date_calendar_forapi_format, base_work_place_id, base_work_place_name, selected_date_calendar_date_status,
            dcr_details_dcr_type_name, dcr_details_dcr_type_id, dcr_id_for_dcr_summary, dcr_no_for_dcr_summary, dcr_remarks_for_dcr_summary, dcr_id_cal_year_for_dcr_summary, dcr_entry_user_for_dcr_summary, calendar_id, calendar_year, calendar_start_date, calendar_end_date, approval_status, approval_status_name, check_draft_saved_last_yn = "",
            mwr_details_mwr_type_name, mwr_details_mwr_type_id, remarks = "";
    public static void setInstance(UserSingletonModel instance) {
        UserSingletonModel.instance = instance;
    }

    private static UserSingletonModel instance=null;
    protected UserSingletonModel(){
        // Exists only to defeat instantiation.
    }
    public static UserSingletonModel getInstance(){
        if(instance == null) {
            instance = new UserSingletonModel();
        }
        return instance;

    }

    //---------------------Getter method starts-----------------

    public String getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getCompID() {
        return CompID;
    }

    public String getCorpID() {
        return CorpID;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public String getSupervisorId() {
        return SupervisorId;
    }

    public String getUserRole() {
        return UserRole;
    }

    public String getAdminYN() {
        return AdminYN;
    }

    public String getPayableClerkYN() {
        return PayableClerkYN;
    }

    public String getSupervisorYN() {
        return SupervisorYN;
    }

    public String getPurchaseYN() {
        return PurchaseYN;
    }

    public String getPayrollClerkYN() {
        return PayrollClerkYN;
    }

    public String getEmpName() {
        return EmpName;
    }

    public String getUserType() {
        return UserType;
    }

    public String getEmailId() {
        return EmailId;
    }

    public String getPwdSetterId() {
        return PwdSetterId;
    }

    public String getFinYearID() {
        return FinYearID;
    }


    //--original

    public String getAbm_id() {
        return abm_id;
    }

    public String getAbm_name() {
        return abm_name;
    }

    public String getDesignation_id() {
        return designation_id;
    }

    public String getDesignation_name() {
        return designation_name;
    }

    public String getDesignation_type() {
        return designation_type;
    }

    public String getHq_id() {
        return hq_id;
    }

    public String getHq_name() {
        return hq_name;
    }

    public String getRbm_id() {
        return rbm_id;
    }

    public String getRbm_name() {
        return rbm_name;
    }

    public String getSm_id() {
        return sm_id;
    }

    public String getSm_name() {
        return sm_name;
    }

    public String getState() {
        return state;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public String getUser_group_id() {
        return user_group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getZbm_id() {
        return zbm_id;
    }

    public String getZbm_name() {
        return zbm_name;
    }

    public String getMenu_list() {
        return menu_list;
    }

    public String getSelected_date_calendar() {
        return selected_date_calendar;
    }

    public String getSelected_date_calendar_forapi_format() {
        return selected_date_calendar_forapi_format;
    }

    public String getBase_work_place_id() {
        return base_work_place_id;
    }

    public String getBase_work_place_name() {
        return base_work_place_name;
    }

    public String getSelected_date_calendar_date_status() {
        return selected_date_calendar_date_status;
    }

    public String getDcr_details_dcr_type_name() {
        return dcr_details_dcr_type_name;
    }

    public String getDcr_details_dcr_type_id() {
        return dcr_details_dcr_type_id;
    }

    public String getDcr_id_for_dcr_summary() {
        return dcr_id_for_dcr_summary;
    }

    public String getDcr_no_for_dcr_summary() {
        return dcr_no_for_dcr_summary;
    }

    public String getDcr_remarks_for_dcr_summary() {
        return dcr_remarks_for_dcr_summary;
    }

    public String getDcr_id_cal_year_for_dcr_summary() {
        return dcr_id_cal_year_for_dcr_summary;
    }

    public String getDcr_entry_user_for_dcr_summary() {
        return dcr_entry_user_for_dcr_summary;
    }

    public String getCalendar_id() {
        return calendar_id;
    }

    public String getCalendar_year() {
        return calendar_year;
    }

    public String getCalendar_start_date() {
        return calendar_start_date;
    }

    public String getCalendar_end_date() {
        return calendar_end_date;
    }

    public String getApproval_status() {
        return approval_status;
    }

    public String getApproval_status_name() {
        return approval_status_name;
    }

    public String getCheck_draft_saved_last_yn() {
        return check_draft_saved_last_yn;
    }

    public String getMwr_details_mwr_type_name() {
        return mwr_details_mwr_type_name;
    }

    public String getMwr_details_mwr_type_id() {
        return mwr_details_mwr_type_id;
    }

    public String getUser_id_rss_pull_service() {
        return user_id_rss_pull_service;
    }

    public String getRemarks() {
        return remarks;
    }

    //---------------------Getter method ends-----------------

    //---------------------Setter method starts-----------

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setCompID(String compID) {
        CompID = compID;
    }

    public void setCorpID(String corpID) {
        CorpID = corpID;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public void setSupervisorId(String supervisorId) {
        SupervisorId = supervisorId;
    }

    public void setUserRole(String userRole) {
        UserRole = userRole;
    }

    public void setAdminYN(String adminYN) {
        AdminYN = adminYN;
    }

    public void setPayableClerkYN(String payableClerkYN) {
        PayableClerkYN = payableClerkYN;
    }

    public void setSupervisorYN(String supervisorYN) {
        SupervisorYN = supervisorYN;
    }

    public void setPurchaseYN(String purchaseYN) {
        PurchaseYN = purchaseYN;
    }

    public void setPayrollClerkYN(String payrollClerkYN) {
        PayrollClerkYN = payrollClerkYN;
    }

    public void setEmpName(String empName) {
        EmpName = empName;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public void setPwdSetterId(String pwdSetterId) {
        PwdSetterId = pwdSetterId;
    }

    public void setFinYearID(String finYearID) {
        FinYearID = finYearID;
    }

    //--original

    public void setAbm_id(String abm_id) {
        this.abm_id = abm_id;
    }

    public void setAbm_name(String abm_name) {
        this.abm_name = abm_name;
    }

    public void setDesignation_id(String designation_id) {
        this.designation_id = designation_id;
    }

    public void setDesignation_name(String designation_name) {
        this.designation_name = designation_name;
    }

    public void setDesignation_type(String designation_type) {
        this.designation_type = designation_type;
    }

    public void setHq_id(String hq_id) {
        this.hq_id = hq_id;
    }

    public void setHq_name(String hq_name) {
        this.hq_name = hq_name;
    }

    public void setRbm_id(String rbm_id) {
        this.rbm_id = rbm_id;
    }

    public void setRbm_name(String rbm_name) {
        this.rbm_name = rbm_name;
    }

    public void setSm_id(String sm_id) {
        this.sm_id = sm_id;
    }

    public void setSm_name(String sm_name) {
        this.sm_name = sm_name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public void setUser_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setZbm_id(String zbm_id) {
        this.zbm_id = zbm_id;
    }

    public void setZbm_name(String zbm_name) {
        this.zbm_name = zbm_name;
    }

    public void setMenu_list(String menu_list) {
        this.menu_list = menu_list;
    }

    public void setSelected_date_calendar(String selected_date_calendar) {
        this.selected_date_calendar = selected_date_calendar;
    }

    public void setSelected_date_calendar_forapi_format(String selected_date_calendar_forapi_format) {
        this.selected_date_calendar_forapi_format = selected_date_calendar_forapi_format;
    }

    public void setBase_work_place_id(String base_work_place_id) {
        this.base_work_place_id = base_work_place_id;
    }

    public void setBase_work_place_name(String base_work_place_name) {
        this.base_work_place_name = base_work_place_name;
    }

    public void setSelected_date_calendar_date_status(String selected_date_calendar_date_status) {
        this.selected_date_calendar_date_status = selected_date_calendar_date_status;
    }

    public void setDcr_details_dcr_type_name(String dcr_details_dcr_type_name) {
        this.dcr_details_dcr_type_name = dcr_details_dcr_type_name;
    }

    public void setDcr_details_dcr_type_id(String dcr_details_dcr_type_id) {
        this.dcr_details_dcr_type_id = dcr_details_dcr_type_id;
    }

    public void setDcr_id_for_dcr_summary(String dcr_id_for_dcr_summary) {
        this.dcr_id_for_dcr_summary = dcr_id_for_dcr_summary;
    }

    public void setDcr_no_for_dcr_summary(String dcr_no_for_dcr_summary) {
        this.dcr_no_for_dcr_summary = dcr_no_for_dcr_summary;
    }

    public void setDcr_remarks_for_dcr_summary(String dcr_remarks_for_dcr_summary) {
        this.dcr_remarks_for_dcr_summary = dcr_remarks_for_dcr_summary;
    }

    public void setDcr_id_cal_year_for_dcr_summary(String dcr_id_cal_year_for_dcr_summary) {
        this.dcr_id_cal_year_for_dcr_summary = dcr_id_cal_year_for_dcr_summary;
    }

    public void setDcr_entry_user_for_dcr_summary(String dcr_entry_user_for_dcr_summary) {
        this.dcr_entry_user_for_dcr_summary = dcr_entry_user_for_dcr_summary;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }

    public void setCalendar_year(String calendar_year) {
        this.calendar_year = calendar_year;
    }

    public void setCalendar_start_date(String calendar_start_date) {
        this.calendar_start_date = calendar_start_date;
    }

    public void setCalendar_end_date(String calendar_end_date) {
        this.calendar_end_date = calendar_end_date;
    }

    public void setApproval_status(String approval_status) {
        this.approval_status = approval_status;
    }

    public void setApproval_status_name(String approval_status_name) {
        this.approval_status_name = approval_status_name;
    }

    public void setCheck_draft_saved_last_yn(String check_draft_saved_last_yn) {
        this.check_draft_saved_last_yn = check_draft_saved_last_yn;
    }

    public void setMwr_details_mwr_type_name(String mwr_details_mwr_type_name) {
        this.mwr_details_mwr_type_name = mwr_details_mwr_type_name;
    }

    public void setMwr_details_mwr_type_id(String mwr_details_mwr_type_id) {
        this.mwr_details_mwr_type_id = mwr_details_mwr_type_id;
    }

    public void setUser_id_rss_pull_service(String user_id_rss_pull_service) {
        this.user_id_rss_pull_service = user_id_rss_pull_service;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    //--------------------Setter method ends----------
}
