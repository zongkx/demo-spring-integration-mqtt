package com.zongkx.mqtt;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class Topic {

    @Bean(name = "aChannel")
    public MessageChannel aChannel() {
        return new DirectChannel();
    }

    @Bean(name = "bChannel")
    public MessageChannel bChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "aChannel")
    public MessageHandler aMessageHandler() {
        return message -> {
            System.out.println("aaaaaa");
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "bChannel")
    public MessageHandler bMessageHandler() {
        return message -> {
            System.out.println("bbbb");
        };
    }


}
