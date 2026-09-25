package com.kunal.simplecrudapp.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModdleMapperConfiguration {

    @Bean

    public ModelMapper modelMapper(){
        return  new ModelMapper();
    }
}
