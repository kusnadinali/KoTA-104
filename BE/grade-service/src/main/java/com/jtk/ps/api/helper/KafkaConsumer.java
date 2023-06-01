package com.jtk.ps.api.helper;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtk.ps.api.dto.kafka.AccountKafka;
import com.jtk.ps.api.model.Account;
import com.jtk.ps.api.model.EventStore;
import com.jtk.ps.api.repository.AccountRepository;
import com.jtk.ps.api.repository.EventStoreRepository;


@Component
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    @Lazy
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private EventStoreRepository eventStoreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private void eventStoreHandler(String entityId, String eventType, Object object,Integer eventDataId){
        try {
            EventStore eventStore = new EventStore();

            eventStore.setEntityId(entityId);
            eventStore.setEventType(eventType);
            eventStore.setEventTime(LocalDateTime.now());
            eventStore.setEventData(objectMapper.writeValueAsString(object));
            eventStore.setEventDataId(eventDataId);

            eventStoreRepository.save(eventStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "account_topic", groupId = "newGroup")
    public void consume(String message){
        LOGGER.info(String.format("Message received -> %s", message));
        try {
            // Mengubah string JSON menjadi objek
            ObjectMapper objectMapper = new ObjectMapper();
            AccountKafka receivedObject = objectMapper.readValue(message, AccountKafka.class);
            Account account = new Account();

            // Lakukan operasi apa pun pada objek yang diterima
            System.out.println("ID: " + receivedObject.getId());
            System.out.println("Username: " + receivedObject.getUsername());
            System.out.println("Role Id: " + receivedObject.getRole_id());

            // proses melakukan save pada tabel account
            account.setId(receivedObject.getId());
            account.setRole_id(receivedObject.getRole_id());
            account.setUsername(receivedObject.getUsername());

            accountRepository.save(account);

            // proses mencatat perubahan pada event store
            eventStoreHandler("Account", "ACCOUNT_ADDED", account, account.getId());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
