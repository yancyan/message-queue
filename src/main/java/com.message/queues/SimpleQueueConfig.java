package com.message.queues;

import com.message.constants.InfrastructureConstants;
import com.message.util.QueueCreator;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 队列声明
 */
@Configuration
public class SimpleQueueConfig {

    public static final String queue_01 = InfrastructureConstants.queue_01;
//    public static final String queue_01_routingKey = InfrastructureConstants.queue_01 + ".routingKey";
    public static final String queue_01_directExchange = InfrastructureConstants.queue_01 + ".directExchange";
    public static final String queue_01_directExchangeBinding = InfrastructureConstants.queue_01 + ".directExchange.binding";

    @Bean(name = queue_01)
    public Queue queue() {
        return QueueCreator.INSTANCE.createQueue(InfrastructureConstants.queue_01);
    }
    @Bean(name = queue_01_directExchange)
    public DirectExchange directExchange() {
        return QueueCreator.INSTANCE.createDirectExchange(queue_01);
    }
    @Bean(name = queue_01_directExchangeBinding)
    public Binding queue_01_bind_directExchange(@Qualifier(queue_01) Queue queue,
                                                @Qualifier(queue_01_directExchange) DirectExchange de) {
        return QueueCreator.INSTANCE.bindToDirectExchange(queue, de, InfrastructureConstants.queue_01);
    }

}
