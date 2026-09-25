package com.kunal.simplecrudapp.exceptions;

public class EmployeeAlreadyExistException extends Exception {

    public EmployeeAlreadyExistException(String msg) {
        super(msg);
    }
}
