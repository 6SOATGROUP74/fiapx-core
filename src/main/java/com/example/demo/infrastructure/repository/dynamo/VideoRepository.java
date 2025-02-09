package com.example.demo.infrastructure.repository.dynamo;

import com.example.demo.infrastructure.repository.entity.VideoEntity;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;

public interface VideoRepository extends DynamoDBCrudRepository<VideoEntity, String> {}