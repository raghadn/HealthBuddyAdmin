package com.example.myhealthbuddyadmin;

import org.junit.Test;

import static org.junit.Assert.*;

public class CreateDoctorTest {

    @Test
    public void checkPhone() {
        String phone="0504411915";
        boolean expected=true;
        CreateDoctor doctor=new CreateDoctor();
        boolean result=doctor.checkPhone(phone);
        assertEquals(expected,result);
    }


    @Test
    public void checkLicense() {
        String license="123456uy";
        CreateDoctor doctor=new CreateDoctor();
        boolean result=doctor.checkLicense(license);
        assertEquals(false,result);
    }
}