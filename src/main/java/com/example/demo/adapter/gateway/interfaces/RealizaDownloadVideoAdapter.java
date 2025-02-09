package com.example.demo.adapter.gateway.interfaces;

import java.io.File;

public interface RealizaDownloadVideoAdapter {
    File execute(String bucket, String key);
}
