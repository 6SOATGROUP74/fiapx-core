package com.example.demo.infrastructure.repository.presenter;

import com.example.demo.core.domain.Video;
import com.example.demo.infrastructure.repository.entity.VideoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VideoEntityMapper {

    VideoEntityMapper INSTANCE = Mappers.getMapper(VideoEntityMapper.class);

    Video mapFrom(VideoEntity videoEntity);


    VideoEntity mapFrom(Video video);
}
