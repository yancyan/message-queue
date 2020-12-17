package com.message.controller;

import com.message.stream.TestStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestStreamPutController {

    @Resource
    TestStream testStream;

    @GetMapping("/stream-message/{content}")
    public void sendMessage(@PathVariable("content") String content) {
        testStream.out().send(MessageBuilder.withPayload(content).build());
    }


}
