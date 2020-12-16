package com.message.queues.listeners;

import com.message.JsonUtils;
import com.message.constants.InfrastructureConstants;
import com.message.dto.MessageInfo;
import com.message.dto.PromotionMessage;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class SimpleQueueListener {
    public static Logger log = LoggerFactory.getLogger(SimpleQueueListener.class);

    @RabbitListener(queues = InfrastructureConstants.queue_01)
    public void listen(Message message, Channel channel) {

        try {
            String content = new String(message.getBody(), Charset.defaultCharset());
            PromotionMessage promotionMessage = JsonUtils.decode(content, PromotionMessage.class);
            String messageId = message.getMessageProperties().getMessageId();
            MessageInfo messageInfo = MessageInfo.builder()
                    .messageId(messageId)
                    .payload(promotionMessage.toString())
                    .build();
            log.info("############### listen " + messageInfo.toString());
//        throw new RuntimeException("lis error test.");
        }
        catch (Exception ex) {
            log.info("queue process error. " + ex.getMessage(), ex);
        } finally {
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                log.info("ack error. " + e.toString(), e);
            }
        }
    }

//    @RabbitListener(queues = InfrastructureConstants.queue_01)
//    public void listenPromotionMessage(PromotionMessage message, Channel channel) {
//        log.info("############### listen " + message.toString());
//    }
}
