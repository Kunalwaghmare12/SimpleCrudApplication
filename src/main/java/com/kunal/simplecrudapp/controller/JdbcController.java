package com.kunal.simplecrudapp.controller;

import com.kunal.simplecrudapp.dao.AddressDao;
import com.kunal.simplecrudapp.dao.EmployeeDao;
import com.kunal.simplecrudapp.entity.Address;
import com.kunal.simplecrudapp.helper.DBUtils;
import com.kunal.simplecrudapp.helper.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class JdbcController {

    @GetMapping("/getEmployee/{id}")
    public ResponseEntity<List<Map<String,Object>>> getEmployee(@PathVariable long id)  {
        System.out.println("inside mthod ==> "+id);
        String query ="select * from employee where employee_id=?";
        List<Map<String,Object>> response=null;
        try{
             response=Helper.execSelectAsMap(query,id);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
