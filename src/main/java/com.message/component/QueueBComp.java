package com.message.component;//package com.message.component;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class QueueBComp {
    public static Logger log = LoggerFactory.getLogger(QueueBComp.class);

    @Resource
    RabbitTemplate rabbitTemplate;
    public static final AMQP.BasicProperties basic = MessageProperties.BASIC;

    public void send_01(String content) {
        rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.execute(ch -> {
            ch.txSelect();
            try {
//                ch.basicPublish("error_ex", "", basic, content.getBytes(StandardCharsets.UTF_8));
                ch.basicPublish("", "error_route", basic, content.getBytes(StandardCharsets.UTF_8));
                ch.txCommit();
            } catch (Exception e) {
                log.info("send error. ", e);
                ch.txRollback();
            }
            return null;
        });

    }
    public void send_02(String content) {
        rabbitTemplate.execute(channel -> {
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    log.info("############ ack " + deliveryTag);
                }
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.info("############ nack " + deliveryTag);
                }
            });
            channel.confirmSelect();

            boolean mandatory = true;
            channel.addReturnListener((replyCode, replyText, exchange, routingKey1, properties, body) -> {
                log.info("ReturnListener exec. relyCode: " + replyCode
                        + "return replyText: " + replyText
                        + "return exchange: " + exchange
                        + "return routingKey: " + routingKey1
                        + "return properties: " + properties
                        + "return body: " + new String(body, StandardCharsets.UTF_8));
            });

//            channel.basicPublish("", "error_ex",null, content.getBytes(StandardCharsets.UTF_8));
//            channel.basicPublish("", "error_route",null, content.getBytes(StandardCharsets.UTF_8));
            channel.basicPublish("", "error_route", mandatory, null, content.getBytes(StandardCharsets.UTF_8));

//            TimeUnit.MINUTES.sleep(1);
            return null;
        });

    }




}
