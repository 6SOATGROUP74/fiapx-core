package com.example.demo.infrastructure.repository.dynamo;

import com.example.demo.core.domain.Video;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;

public interface VideoRepository extends DynamoDBCrudRepository<Video, String> {}