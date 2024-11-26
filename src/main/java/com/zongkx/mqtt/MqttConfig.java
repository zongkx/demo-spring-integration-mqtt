package com.zongkx.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MqttConfig {
    private static final String MQTT_BROKER_URL = "tcp://127.0.0.1:1883";


    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setServerURIs(new String[]{MQTT_BROKER_URL});
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "inbound")
    public MessageHandler mqttInbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("in" + UUID.randomUUID(), mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(0);
        return messageHandler;
    }

    @Bean
    @ServiceActivator(inputChannel = "outbound")
    public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("out" + UUID.randomUUID(), mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(0);
        return messageHandler;
    }

    @Bean
    public MessageChannel inbound() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel outbound() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer messageProducer(MqttPahoClientFactory mqttClientFactory, @Qualifier("inbound") MessageChannel channel) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(UUID.randomUUID().toString(), mqttClientFactory, "A/#", "B/#");
        adapter.setCompletionTimeout(5000);
        adapter.setQos(0);
        adapter.setOutputChannel(channel);
        return adapter;
    }

    //    @Bean
//    @Router(inputChannel = "inbound")
//    public AbstractMessageRouter abstractMessageRouter(@Qualifier("aChannel") MessageChannel aChannel, @Qualifier("bChannel") MessageChannel bChannel) {
//        return new AbstractMessageRouter() {
//            @Override
//            protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
//                MessageHeaders headers = message.getHeaders();
//                String topic = headers.get(MqttHeaders.RECEIVED_TOPIC).toString();
//                log.info("received topic :{} \t payload :{}", topic, message.getPayload());
//                if (topic.startsWith("A")) {
//                    return Collections.singleton(aChannel);
//                } else {
//                    return Collections.singleton(bChannel);
//                }
//            }
//        };
//    }
    @Bean  //dsl 写法等价于 上面的  AbstractMessageRouter
    public IntegrationFlow routerFlow2() {
        return IntegrationFlow.from("inbound")
                .route(Message.class, m -> m.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class).split("/")[0],
                        m -> m
                                .channelMapping("A", "aChannel")
                                .channelMapping("B", "bChannel"))
                .get();
    }


}
