package com.message.component.template;

import com.message.dto.MessageInfo;

import java.util.function.Function;

public interface MessageTemplate {

    public void sendMessage(String routingKey, MessageInfo messageInfo);

    public void syncSendMessage(String routingKey, MessageInfo messageInfo);

    public <T> void sendMessage(String routingKey, T info);

    public <T, R> void queueConsumer(String routingKey, Function<T, R> apply);
}
