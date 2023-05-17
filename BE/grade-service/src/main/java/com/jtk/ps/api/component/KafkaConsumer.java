package com.jtk.ps.api.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtk.ps.api.payload.AccountKafka;

// import com.jtk.ps.api.payload.AccountKafka;
// import com.jtk.ps.api.payload.User;

@Component
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "account_topic", groupId = "myGroup")
    public void consume(String message){
        LOGGER.info(String.format("Message received -> %s", message));
        try {
            // Mengubah string JSON menjadi objek
            ObjectMapper objectMapper = new ObjectMapper();
            AccountKafka testObject = objectMapper.readValue(message, AccountKafka.class);

            // Lakukan operasi apa pun pada objek yang diterima
            System.out.println("ID: " + testObject.getId());
            System.out.println("Username: " + testObject.getUsername());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
