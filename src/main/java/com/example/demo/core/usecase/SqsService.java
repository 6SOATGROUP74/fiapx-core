package com.example.demo.core.usecase;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqsService {

    private static final String SQS_URL = "http://localhost:4566/000000000000/minha-fila";
    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);

    @Autowired
    private SqsTemplate sqsTemplate;


    public void enviaMensagem(String mensagem) {

        sqsTemplate.send(to -> to.queue("filaTeste").payload(mensagem));
    }

    @SqsListener("filaTeste")
    public void consomeEvento(String mensagem) {
        logger.info("Mensagem recebida - {}", mensagem);
    }

}
