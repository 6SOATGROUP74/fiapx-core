package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.core.domain.StatusProcessamento;
import com.example.demo.core.domain.Video;

import java.io.File;
import java.time.Instant;
import java.util.UUID;

public class VideoDomainFactory {

    public static Video criarVideo(File arquivoBaixado, String email) {
        Video video = new Video();
        video.setId(UUID.randomUUID().toString());
        video.setNome(arquivoBaixado.getName().replaceAll("^\\$[^\\$]*\\$", ""));
        video.setEmail(email);
        video.setStatus(StatusProcessamento.INCLUIDO.toString());
        video.setDataCriacao(Instant.now().toString());
        video.setDataAtualizacao(Instant.now().toString());
        return video;
    }

    public static Video alteraStatusVideo(Video video, StatusProcessamento statusProcessamento) {
        video.setStatus(statusProcessamento.name());
        video.setDataAtualizacao(Instant.now().toString());
        return video;
    }

}
