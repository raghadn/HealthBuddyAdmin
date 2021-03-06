package com.example.myhealthbuddyadmin;

public class DoctorRequests {
    public String date;
    public String doctor_id;
    public String patient_id;
    public String type;
    public String patient_uid;
    public String doctor_uid;
    public String request_date;

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public DoctorRequests(){

    }

    public DoctorRequests(String date, String doctor_id, String patient_id, String type) {
        this.date = date;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.type = type;
    }

    public String getDoctor_uid() {
        return doctor_uid;
    }

    public void setDoctor_uid(String doctor_uid) {
        this.doctor_uid = doctor_uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPatient_uid() {
        return patient_uid;
    }

    public void setPatient_uid(String patient_uid) {
        this.patient_uid = patient_uid;
    }
}
