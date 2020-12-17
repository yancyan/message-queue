package com.message.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestStreamListener {

    @SendTo(TestStream.fail_msg)
    @StreamListener(TestStream.msg)
    public String listen(@Payload String msg) {
        log.info("########### TestStreamListener " + msg);

        return msg;
    }
}
