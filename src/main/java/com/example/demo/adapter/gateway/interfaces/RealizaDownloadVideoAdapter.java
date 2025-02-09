package com.example.demo.adapter.gateway.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface RealizaDownloadVideoAdapter {
    File execute(String bucket, String key);
}
