package com.example.demo.adapter.gateway.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ConverteVideoZipAdapter {
    MultipartFile execute(MultipartFile arquivo) throws IOException;
}
