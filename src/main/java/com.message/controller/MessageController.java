package com.message.controller;

import com.message.component.QueueAComp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MessageController {

    @Resource
    QueueAComp queueAComp;


    @GetMapping("/message/{content}")
    public void test(@PathVariable("content") String content) {
        queueAComp.sendA(content);
    }
}
