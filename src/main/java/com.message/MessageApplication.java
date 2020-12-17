package com.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.stream.TestStream;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableBinding(TestStream.class)
@EnableWebMvc
@EnableRabbit
@SpringBootApplication
public class MessageApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MessageApplication.class)
                .run(args);
    }

    /**
     * 可通过 MessageConverter 自动转换类型，也可直接接收消息，自行转换
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
