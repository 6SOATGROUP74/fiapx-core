package com.example.demo.adapter.gateway.interfaces;

import com.example.demo.core.domain.StatusProcessamento;
import com.example.demo.core.domain.Video;

public interface GerenciaStatusVideoAdapter {
    Video salvaVideo(Video video);
    Video buscaVideo(String id);
    Video alteraStatus(String id, StatusProcessamento statusProcessamento);
    void deletaVideo(String id);
}
