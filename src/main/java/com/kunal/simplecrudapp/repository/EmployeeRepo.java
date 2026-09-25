package com.kunal.simplecrudapp.repository;

import com.kunal.simplecrudapp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);
}
