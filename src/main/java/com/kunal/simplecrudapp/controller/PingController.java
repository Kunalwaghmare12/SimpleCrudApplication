package com.kunal.simplecrudapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class PingController {

    @GetMapping("/ping")
    public String helloWorld(){
        return "Hello world how  you >>>"+"kunal";
    }

    @GetMapping("/pingV2")
    public String helloWorldV2(){
        return "Connecting... >>>";
    }
}
