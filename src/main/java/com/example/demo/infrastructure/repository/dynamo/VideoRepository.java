package com.example.demo.infrastructure.repository.dynamo;

import com.example.demo.infrastructure.repository.entity.VideoEntity;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;

import java.util.List;

public interface VideoRepository extends DynamoDBCrudRepository<VideoEntity, String> {
    List<VideoEntity> findByEmail(String email);
}