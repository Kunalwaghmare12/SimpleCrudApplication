package com.kunal.simplecrudapp.exceptions;

public class EmployeeNotFoundException extends Exception {

    public EmployeeNotFoundException(String msg) {
        super(msg);
    }
}
