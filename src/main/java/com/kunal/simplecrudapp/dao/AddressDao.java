package com.kunal.simplecrudapp.dao;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class AddressDao {
    @NotEmpty(message = "field Should not be empty")
    private String street;
    @NotEmpty(message = "field Should not be empty")
    private String city;
    @NotEmpty(message = "field Should not be empty")
    private String state;
    @NotEmpty(message = "field Should not be empty")
    @Pattern(regexp = "^[0-9]{5}$",message = "field contains only 10 digits")
    private String pincode;
}
