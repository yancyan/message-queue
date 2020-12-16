package com.message.queues.listeners;

import com.message.constants.InfrastructureConstants;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RabbitListener(queues = InfrastructureConstants.queue_02)
public class Queue2Listener {

    public static Logger log = LoggerFactory.getLogger(SimpleQueueListener.class);
    public static final Set<Long> cache = new HashSet<>();

    @RabbitHandler
    public void receive(Message message, @Headers Map<String,Object> headers, Channel channel) throws Exception{
        String content = new String(message.getBody(), Charset.defaultCharset());
        log.info("queue_02 receive " + content);
//        // 唯一的消息ID
//        Long deliverTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
//
//        if(cache.contains(deliverTag)){
//            channel.basicAck(deliverTag,false);
//        }else{
//            channel.basicNack(deliverTag,false,true);
//        }
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

    }

}
