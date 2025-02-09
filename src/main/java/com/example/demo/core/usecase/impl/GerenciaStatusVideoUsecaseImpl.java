package com.example.demo.core.usecase.impl;

import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.core.domain.StatusProcessamento;
import com.example.demo.core.domain.Video;
import com.example.demo.core.usecase.interfaces.GerenciaStatusVideoUsecase;

import java.time.Instant;

public class GerenciaStatusVideoUsecaseImpl implements GerenciaStatusVideoUsecase {

    GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter;

    public GerenciaStatusVideoUsecaseImpl(GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter) {
        this.gerenciaStatusVideoAdapter = gerenciaStatusVideoAdapter;
    }

    @Override
    public Video salvaVideo(Video video) {
        return gerenciaStatusVideoAdapter.salvaVideo(video);
    }

    @Override
    public Video buscaVideo(String id) {
        return gerenciaStatusVideoAdapter.buscaVideo(id);
    }

    @Override
    public Video alteraStatus(String id, StatusProcessamento statusProcessamento) {
        Video videoAtual = gerenciaStatusVideoAdapter.alteraStatus(id, statusProcessamento);
        videoAtual.setStatus(statusProcessamento.toString());
        videoAtual.setDataCriacao(Instant.now().toString());
        videoAtual.setDataAtualizacao(Instant.now().toString());
        return gerenciaStatusVideoAdapter.salvaVideo(videoAtual);
    }

    @Override
    public void deletaVideo(String id) {
        gerenciaStatusVideoAdapter.deletaVideo(id);
    }
}
