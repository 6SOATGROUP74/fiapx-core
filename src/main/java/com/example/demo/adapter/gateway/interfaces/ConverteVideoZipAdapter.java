package com.example.demo.adapter.gateway.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ConverteVideoZipAdapter {
    String execute(String nomeArquivo) throws IOException;
}
