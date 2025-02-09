package com.example.demo.config;

import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.core.usecase.impl.GerenciaStatusVideoUsecaseImpl;
import com.example.demo.core.usecase.interfaces.GerenciaStatusVideoUsecase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public GerenciaStatusVideoUsecase gerenciaStatusVideoUsecase(GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter){
        return new GerenciaStatusVideoUsecaseImpl(gerenciaStatusVideoAdapter);
    }

}
