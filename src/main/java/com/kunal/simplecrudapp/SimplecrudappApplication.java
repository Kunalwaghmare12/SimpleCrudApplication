package com.kunal.simplecrudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SimplecrudappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimplecrudappApplication.class, args);
    }

}
