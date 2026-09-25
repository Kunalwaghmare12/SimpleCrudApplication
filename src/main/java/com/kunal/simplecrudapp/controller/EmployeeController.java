package com.kunal.simplecrudapp.controller;

import com.kunal.simplecrudapp.dao.EmployeeDao;
import com.kunal.simplecrudapp.exceptions.EmployeeAlreadyExistException;
import com.kunal.simplecrudapp.exceptions.EmployeeNotFoundException;
import com.kunal.simplecrudapp.service.employee.EmployeeService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    private ModelMapper modelMapper;

    @PostMapping("/")
    public ResponseEntity<EmployeeDao> saveEmployee(@Valid @RequestBody EmployeeDao employeeDao) throws EmployeeAlreadyExistException, MessagingException {
        EmployeeDao savedEmployee=employeeService.saveEmployee(employeeDao);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDao> updateEmployee(@PathVariable("id") long id,@Valid @RequestBody EmployeeDao employeeDao) throws EmployeeNotFoundException, MessagingException {
    EmployeeDao updatedEmployee=employeeService.updateEmployee(id,employeeDao);
    return new ResponseEntity<>(updatedEmployee,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDao> getEmployeeById(@PathVariable("id") long id) throws EmployeeNotFoundException {
        EmployeeDao foundEmployee=employeeService.findEmployeeById(id);
        return new ResponseEntity<>(foundEmployee,HttpStatus.FOUND);
    }

    @GetMapping("/")
    public ResponseEntity<List<EmployeeDao>> getAllEmployee(){
        List<EmployeeDao> employeeDaoList=employeeService.findAllEmployees();
        return new ResponseEntity<>(employeeDaoList,HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") long id) throws EmployeeNotFoundException, MessagingException {
        employeeService.deleteEmployeeById(id);
        return new ResponseEntity<>("Employee deleted Successfully",HttpStatus.OK);
    }

    @GetMapping("/get-otp")
    public ResponseEntity<String> callApi() throws Exception {
        return ResponseEntity.ok(employeeService.callThirdPartyApi());
    }





}
