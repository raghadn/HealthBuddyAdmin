
package com.example.myhealthbuddyadmin;


public class PatientResult {


    public String name;
    public String national_id;
    //public String phone;
    //public String img;


    public PatientResult() {

    }

    public PatientResult(String name, String national_id) {
        this.name = name;
        this.national_id = national_id;
        //this.phone= phone;

        // add img paramter also
        //this.img=img;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getNational_id() {
        return national_id;
    }


   /* public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }*/
}
