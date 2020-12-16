package com.message.component.template;

import com.message.JsonUtils;
import com.message.constants.InfrastructureConstants;
import com.message.dto.MessageInfo;
import com.message.util.QueueCreator;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ReturnListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@ConditionalOnProperty(value = "message.type", havingValue = "rabbit", matchIfMissing = true)
@Primary
@Component
public class RabbitMessageTemplate implements MessageTemplate {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AmqpAdmin amqpAdmin;

    /**
     * 生产端 Confirm 消息确认机制：Broker收到消息返回发送端应答
     * ${@code
     * channel.confirmSelect();
     * channel.addConfirmListener()
     * }
     * 我们采用的是异步 confirm 模式：提供一个回调方法，服务端 confirm 了一条或者多条消息后 Client 端会回调这个方法。
     * 除此之外还有单条同步 confirm 模式、批量同步 confirm 模式，由于现实场景中很少使用我们在此不做介绍，如有兴趣直接参考官方文档。
     * <p>
     * Return 消息机制: 处理一些不可路由的消息，Mandatory：如果为 true，则监听会接收到路由不可达的消息，然后进行后续处理，如果为 false，那么 broker 端自动删除该消息!
     */
    @Override
    public void sendMessage(String routingKey, MessageInfo info) {
        rabbitTemplate.execute(channel -> {
            channel.confirmSelect();
            channel.addConfirmListener((deliveryTag, multiple) -> {
                log.info("ack " + routingKey);
            }, (deliveryTag, multiple) -> {
                log.info("nack " + routingKey);
            });

            boolean mandatory = true;
            channel.addReturnListener((replyCode, replyText, exchange, routingKey1, properties, body) -> {
                System.out.println("ReturnListener exec. relyCode: " + replyCode
                        + "return replyText: " + replyText
                        + "return exchange: " + exchange
                        + "return routingKey: " + routingKey1
                        + "return properties: " + properties
                        + "return body: " + new String(body, StandardCharsets.UTF_8));
            });

            channel.basicPublish(routingKey, routingKey, mandatory,null, info.getPayload().getBytes(StandardCharsets.UTF_8));
//            channel.basicPublish(InfrastructureConstants.queue_01, routingKey, mandatory,null, info.getPayload().getBytes(StandardCharsets.UTF_8));
            return null;
        });
    }

    @Override
    public void syncSendMessage(String routingKey, MessageInfo info) {
        rabbitTemplate.execute(channel -> {

            channel.basicPublish(routingKey, routingKey,null, info.getPayload().getBytes(StandardCharsets.UTF_8));
//            if (!channel.waitForConfirms()) {
//                throw new RuntimeException("send message error.");
//            }
            channel.waitForConfirmsOrDie();
            return null;
        });
    }

    @Override
    public <T> void sendMessage(String routingKey, T info) {
        try {
            log.info("sendPromotionMessage start. thread name " + Thread.currentThread().getName());
            MessageBuilder mb = MessageBuilder.withBody(JsonUtils.encode(info).getBytes(Charset.defaultCharset()))
                    .andProperties(MessagePropertiesBuilder.newInstance()
                            .setContentType(MessageProperties.CONTENT_TYPE_JSON) // 默认 application/octet-stream
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                            .setAppId("message-test-id")
                            .build());
            QueueInformation queueInfo = amqpAdmin.getQueueInfo(routingKey);
            if (queueInfo == null) {
                amqpAdmin.declareQueue(QueueCreator.INSTANCE.createQueue(routingKey));
            }
            rabbitTemplate.convertAndSend(routingKey, mb.build());
        } catch (Exception e) {
            log.info("send error. exMsg " + e.getMessage(), e);
        }
    }

    @Override
    public <T, R> void queueConsumer(String routingKey, Function<T, R> apply) {
        rabbitTemplate.execute(channel -> {

//            channel.basicConsume(routingKey, false, () -> apply.apply());

            return null;
        });
    }
}
