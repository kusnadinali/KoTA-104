package com.jtk.ps.api.config;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
// import org.springframework.kafka.support.serializer.JsonDeserializer;

// import com.jtk.ps.api.payload.AccountKafka;

// @Configuration
public class KafkaConsumerConfig {
    // @Value("${spring.kafka.bootstrap-servers}")
    // private String bootstrapServers;

    // @Bean
    // public ConcurrentKafkaListenerContainerFactory<String, AccountKafka> kafkaListenerContainerFactory() {
    //     ConcurrentKafkaListenerContainerFactory<String, AccountKafka> factory = new ConcurrentKafkaListenerContainerFactory<>();
    //     factory.setConsumerFactory(consumerFactory());
    //     return factory;
    // }

    // @Bean
    // public DefaultKafkaConsumerFactory<String, AccountKafka> consumerFactory() {
    //     Map<String, Object> props = new HashMap<>();
    //     props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    //     props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    //     props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    //     props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    //     props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    //     return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(AccountKafka.class));
    // }
}
