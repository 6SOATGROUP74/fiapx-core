package com.example.demo.adapter.gateway.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RealizaUploadVideoAdapter {
    void execute(String diretorio, MultipartFile nomeDoArquivo) throws IOException;
}
