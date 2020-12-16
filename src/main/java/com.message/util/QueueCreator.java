package com.message.util;

import com.message.constants.InfrastructureConstants;
import org.springframework.amqp.core.*;

public class QueueCreator {
    public static QueueCreator INSTANCE = new QueueCreator();

    private QueueCreator() {
    }

    public Queue createQueue(String queueName) {
        return QueueBuilder.durable(queueName)
                .overflow(QueueBuilder.Overflow.rejectPublish)
                .build();
    }

    public DirectExchange createDirectExchange(String name) {
        DirectExchange directExchange = new DirectExchange(name);
        directExchange.setInternal(false);
        return directExchange;
    }

    public Binding bindToDirectExchange(Queue queue, DirectExchange de, String routeKey) {
        return BindingBuilder.bind(queue).to(de).with(routeKey);
    }

}
