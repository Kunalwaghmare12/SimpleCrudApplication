package com.kunal.simplecrudapp.service.employee;

import com.kunal.simplecrudapp.dao.EmployeeDao;
import com.kunal.simplecrudapp.exceptions.EmployeeAlreadyExistException;
import com.kunal.simplecrudapp.exceptions.EmployeeNotFoundException;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EmployeeService {
    EmployeeDao saveEmployee(EmployeeDao employeeDao) throws EmployeeAlreadyExistException, MessagingException;

    EmployeeDao updateEmployee(long id, EmployeeDao employeeDao) throws EmployeeNotFoundException, MessagingException;

    EmployeeDao findEmployeeById(long id) throws EmployeeNotFoundException;

    List<EmployeeDao> findAllEmployees();

    void deleteEmployeeById(long id) throws EmployeeNotFoundException, MessagingException;

    String callThirdPartyApi() throws Exception;
}
