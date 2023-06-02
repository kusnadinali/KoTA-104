package com.jtk.ps.api.helper;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @KafkaListener(topics = "account_topic", groupId = "tryGroup")
    public void consumeAccountService(String message){
        LOGGER.info(String.format("Message received -> %s", message));
        try {
            // Mengubah string JSON menjadi objek
            ObjectMapper objectMapper = new ObjectMapper();
            AccountKafka receivedObject = objectMapper.readValue(message, AccountKafka.class);

            // Lakukan operasi apa pun pada objek yang diterima
            System.out.println("==============-----------------------------=========================");
            System.out.println("ID: " + receivedObject.getId());
            System.out.println("Username: " + receivedObject.getUsername());
            System.out.println("Role Id: " + receivedObject.getRole_id());
            System.out.println("Operation: " + receivedObject.getOperation());


            // proses melakukan save pada tabel account
            if(receivedObject.getOperation().equalsIgnoreCase("ADDED")){
                Account account = new Account();
                account.setRole_id(receivedObject.getRole_id());
                account.setUsername(receivedObject.getUsername());
                account.setId(receivedObject.getId());
                account.setIs_delete(0);

                accountRepository.save(account);
                eventStoreHandler("account", "ACCOUNT_ADDED", account, account.getId());
            }
            else if(receivedObject.getOperation().equalsIgnoreCase("UPDATE")){
                Optional<Account> account = accountRepository.findById(receivedObject.getId());
                account.ifPresent(c -> {
                    c.setRole_id(receivedObject.getRole_id());
                    accountRepository.save(c);
                    eventStoreHandler( "account", "ACCOUNT_UPDATE", c, c.getId());
                });
            }else if(receivedObject.getOperation().equalsIgnoreCase("DELETE")){
                Optional<Account> account = accountRepository.findById(receivedObject.getId());

                account.ifPresent(c -> {
                    c.setIs_delete(1);
                    accountRepository.save(c);
                    eventStoreHandler("account", "ACCOUNT_DELETE", c, c.getId());
                });
                
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
