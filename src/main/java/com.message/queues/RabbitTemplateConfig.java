package com.message.queues;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RabbitTemplateConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback{

    final RabbitTemplate rabbitTemplate;

    public RabbitTemplateConfig(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("########## setConfirmCallback exec.");
//        rabbitTemplate.setConfirmCallback(this);
//
//        rabbitTemplate.setMandatory(true);
//        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 消息只要被 broker 接收到就会执行 confirmCallback，
     * 如果是 cluster 模式，需要所有 broker 接收到才会调用 confirmCallback。
     *
     * CachingConnectionFactory factory = new CachingConnectionFactory();
     * factory.setPublisherConfirms(true);//开启confirm模式
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("########## ConfirmCallback confirm exec." + correlationData + ", ack = " + ack + ", cause = " + cause);
    }

    /**
     * CachingConnectionFactory factory = new CachingConnectionFactory();
     * factory.setPublisherReturns(true);//开启return模式
     * rabbitTemplate.setMandatory(true);//开启强制委托模式
     *
     * @param returned
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.info("########## returnedMessage exec. returnedMessage is " + returned.toString());
        throw new RuntimeException("send fail.");
    }
}
