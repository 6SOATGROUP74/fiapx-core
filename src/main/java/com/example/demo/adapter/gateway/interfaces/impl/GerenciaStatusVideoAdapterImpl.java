package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.core.domain.StatusProcessamento;
import com.example.demo.core.domain.Video;
import com.example.demo.infrastructure.repository.presenter.VideoEntityMapper;
import com.example.demo.infrastructure.repository.dynamo.VideoRepository;
import com.example.demo.infrastructure.repository.entity.VideoEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class GerenciaStatusVideoAdapterImpl implements GerenciaStatusVideoAdapter {
    
    VideoRepository videoRepository;

    public GerenciaStatusVideoAdapterImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Video salvaVideo(Video video) {
        return VideoEntityMapper.INSTANCE.mapFrom(videoRepository.save(VideoEntityMapper.INSTANCE.mapFrom(video)));
    }

    @Override
    public Video buscaVideo(String id) {
        return VideoEntityMapper.INSTANCE.mapFrom(videoRepository.findById(id).get());
    }

    @Override
    public Video alteraStatus(String id, StatusProcessamento statusProcessamento) {
        VideoEntity videoAtual = videoRepository.findById(id).get();
        videoAtual.setStatus(statusProcessamento.toString());
        videoAtual.setDataAtualizacao(Instant.now().toString());
        return VideoEntityMapper.INSTANCE.mapFrom(videoRepository.save(videoAtual));
    }

    @Override
    public void deletaVideo(String id) {
        videoRepository.deleteById(id);
    }
}
