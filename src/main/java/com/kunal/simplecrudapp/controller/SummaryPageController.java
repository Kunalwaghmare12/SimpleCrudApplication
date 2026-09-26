package com.kunal.simplecrudapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
public class SummaryPageController {

    public String getSummary(){
        return "This is summary page";
    }

}
