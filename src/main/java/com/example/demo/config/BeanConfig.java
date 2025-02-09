package com.example.demo.config;

import com.example.demo.adapter.gateway.interfaces.ConverteVideoFrameAdapter;
import com.example.demo.adapter.gateway.interfaces.ConverteVideoZipAdapter;
import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.ProcessaVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaUploadVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.impl.ProcessaVideoAdapterSqsImpl;
import com.example.demo.core.usecase.impl.GerenciaStatusVideoUsecaseImpl;
import com.example.demo.core.usecase.interfaces.GerenciaStatusVideoUsecase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public GerenciaStatusVideoUsecase gerenciaStatusVideoUsecase(GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter) {
        return new GerenciaStatusVideoUsecaseImpl(gerenciaStatusVideoAdapter);
    }

    @Bean
    public ProcessaVideoAdapter processaVideoAdapter(ConverteVideoZipAdapter converteVideoZipAdapter,
                                                     ConverteVideoFrameAdapter converteVideoFrameAdapter,
                                                     RealizaUploadVideoAdapter realizaUploadVideoAdapter,
                                                     RealizaDownloadVideoAdapter realizaDownloadVideoAdapter,
                                                     GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter) {
        return new ProcessaVideoAdapterSqsImpl(converteVideoZipAdapter, converteVideoFrameAdapter, realizaUploadVideoAdapter, realizaDownloadVideoAdapter, gerenciaStatusVideoAdapter);
    }
}
