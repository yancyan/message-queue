package com.message.queues;

import com.message.constants.InfrastructureConstants;
import com.message.util.QueueCreator;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutQueueConfig {


    public static final String queue_02 = InfrastructureConstants.queue_02;
    public static final String queue_03 = InfrastructureConstants.queue_03;
    //    public static final String queue_01_routingKey = InfrastructureConstants.queue_01 + ".routingKey";
    public static final String queue_02_directExchange = InfrastructureConstants.queue_02 + ".directExchange";
    public static final String queue_02_directExchangeBinding = InfrastructureConstants.queue_02 + ".directExchange.binding";
    public static final String queue_03_directExchangeBinding = InfrastructureConstants.queue_03 + ".directExchange.binding";

    @Bean(name = queue_02)
    public Queue queue_02() {
        return QueueCreator.INSTANCE.createQueue(InfrastructureConstants.queue_02);
    }
    @Bean(name = queue_03)
    public Queue queue_03() {
        return QueueCreator.INSTANCE.createQueue(InfrastructureConstants.queue_03);
    }

    @Bean(name = queue_02_directExchange)
    public FanoutExchange directExchange() {
        return ExchangeBuilder.fanoutExchange(queue_02).build();
    }
    @Bean(name = queue_02_directExchangeBinding)
    public Binding queue_02_bind_directExchange(@Qualifier(queue_02) Queue queue,
                                                @Qualifier(queue_02_directExchange) FanoutExchange de) {
        return BindingBuilder.bind(queue).to(de);
    }

    @Bean(name = queue_03_directExchangeBinding)
    public Binding queue_03_bind_directExchange(@Qualifier(queue_03) Queue queue,
                                                @Qualifier(queue_02_directExchange) FanoutExchange de) {
        return BindingBuilder.bind(queue).to(de);
    }
}
