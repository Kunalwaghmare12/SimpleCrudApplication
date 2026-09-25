package com.kunal.simplecrudapp.dao;

import com.kunal.simplecrudapp.entity.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDao {
    @NotEmpty(message = "field Should not be empty")
    private String firstName;
    @NotEmpty(message = "field Should not be empty")
    private String lastName;
    @Email(message = "Enter valid email")
    private String email;
    @Valid
    private Address address;
    @NotEmpty(message = "field should not be empty")
    @Pattern(regexp = "^[0-9]{10}$",message = "field contains only 10 digits")
    private String contact;

}
