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
    public Video alteraStatus(String id, StatusProcessamento statusProcessamento) {
        Video videoAtual = gerenciaStatusVideoAdapter.execute(id);
        videoAtual.setStatus(statusProcessamento.toString());
        videoAtual.setDataAtualizacao(Instant.now().toString());
        return videoAtual;
    }
}
