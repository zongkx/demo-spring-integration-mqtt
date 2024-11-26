package com.zongkx.mqtt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class DemoSpringIntegrationMqttApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringIntegrationMqttApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(IMqttMessageGateway gateway) {
        return args -> {
            gateway.publish("A/1", "aa".getBytes(StandardCharsets.UTF_8));
            gateway.publish("B/1", "aa".getBytes(StandardCharsets.UTF_8));
        };
    }

}
