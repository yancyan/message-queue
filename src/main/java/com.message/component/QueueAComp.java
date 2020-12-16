package com.message.component;

import com.message.JsonUtils;
import com.message.component.template.RabbitMessageTemplate;
import com.message.constants.InfrastructureConstants;
import com.message.dto.MessageInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class QueueAComp {

    @Resource
    RabbitMessageTemplate rmt;

    public void sendA(String content) {

    }

    public <T> void sendPromotionMessage(T pm) {
        rmt.sendMessage(InfrastructureConstants.queue_01, pm);
    }


    public <T> void send02(T pm) {
        rmt.sendMessage(InfrastructureConstants.queue_02, pm);
    }

    public <T> void send02NoExist(String queueName, T pm) {
        MessageInfo info = MessageInfo.builder().payload(JsonUtils.encode(pm)).build();
        rmt.sendMessage(queueName, info);
    }

}
