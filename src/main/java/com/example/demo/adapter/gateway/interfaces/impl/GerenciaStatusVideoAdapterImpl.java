package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.core.domain.Video;
import com.example.demo.infrastructure.integration.pagbank.presenter.VideoEntityMapper;
import com.example.demo.infrastructure.repository.dynamo.VideoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GerenciaStatusVideoAdapterImpl implements GerenciaStatusVideoAdapter {

    VideoRepository videoRepository;

    public GerenciaStatusVideoAdapterImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Video execute(String id) {
        return VideoEntityMapper.INSTANCE.mapFrom(videoRepository.findById(id).get());
    }
}
