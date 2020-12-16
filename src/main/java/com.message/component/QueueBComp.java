package com.message.component;//package com.message.component;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//
//
//@Component
//public class QueueBComp {
//    public static Logger log = LoggerFactory.getLogger(QueueBComp.class);
//
//    @Resource
//    RabbitConfirmProvider rcp;
//
//    public void sendB(String content) {
//        rcp.publicMessage(content);
//    }
//
//
//
//}
