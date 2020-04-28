package com.example.myhealthbuddyadmin;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {

    @Test
    public void checkFields() {
        String id="12345";
        String password="";
        Login login=new Login();
        boolean result=login.checkFields(id,password);
        assertEquals(false,result);
    }
}