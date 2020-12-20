### 消息持久化
* exchange
```
boolean durable = true;
boolean autoDelete = false;
channel.exchageDeclare("dlx", TOPIC, durable, autoDelete, null)
```

* queue
```
boolean durable = true;
boolean autoDelete = false;
channel.queueDeclare("order-queue", durable, false, autoDelete, queueArguments);
```

* queue message
```
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .deliveryMode(2)
                    .priority(0)
                    .build();
channel.basicPublish("order", "order.created", false, properties, "sample-data".getBytes())
```

### 发送返回确认
消息正常投递时，回调`handleAck()`,通常情况下仅保证消息投递到Broker
```
channel.addConfirmListener(new ConfirmListener() {
    @Override
    public void handleAck(long deliveryTag, boolean multiple) throws IOException {
        log.info("############ ack " + deliveryTag);
    }
    @Override
    public void handleNack(long deliveryTag, boolean multiple) throws IOException {
        log.info("############ nack " + deliveryTag);
    }
});
channel.confirmSelect();
channel.basicPublish("order", "order.created", false, properties, "sample-data".getBytes());
```

未投递到对应队列，回调`ReturnListener`
```
boolean mandatory = true;
channel.addReturnListener((replyCode, replyText, exchange, routingKey1, properties, body) -> {
    log.info("ReturnListener exec. relyCode: " + replyCode
            + "return replyText: " + replyText
            + "return exchange: " + exchange
            + "return routingKey: " + routingKey1
            + "return properties: " + properties
            + "return body: " + new String(body, StandardCharsets.UTF_8));
});
```

### 消费端手动确认
* 处理成功 ack， 不成功 nack
* 对于nack，可设置重新投递或死信队列
* 考虑消费幂等
```
boolean autoAck = false;
channel.basicConsume("order-queue", autoAck,
        new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                if (success()) {
                    channel.basicAck(deliveryTag, false);
                } else {
                    if (!envelope.isRedeliver()) {
                        boolean requeue = true;
                        channel.basicNack(deliveryTag, false, requeue);
                    } else {
                        boolean requeue = false;
                        channel.basicNack(deliveryTag, false, requeue);
                    }
                }
            }
        });
```
*** 重复消费的问题 ***
rabbitmq、rocketmq、kafka，都有可能会出现消费重复消费的问题, 应该通过消费端实现


### 设置消息的TTL和消息队列的max-length
* 消息的时效性，可设置TTL（`x-message-ttl`） 
* 队列内容过大，可设置（`x-max-length`）

### 设置死信交换（DLX）和死信队列（DLQ）
默认情况下，被抛弃的消息将直接丢弃，可设置DLX `x-dead-letter-exchange`，设置DLQ为lazy，并且没有TTL，并且没有max-length
下列三种情况，消息将进入DLX中：
* nack时指定了`requeue = false`
* 消息的TTL已到
* 消息队列的max-length已到

在发送消息时，当已经达到queue的上限，而当queue定义为x-overflow=reject-publish时，rabbitmq将nack。

### 设置 PREFETCH COUNT
消费方一次性从rabbitmq读取的消息数量，
设置过大，消费方可能始终处于高负荷运转状态，
太小又会增加网络开销，通常设置为20-50。
另外，有时为了保证多个消费方均衡地分摊消息处理任务，通常设置prefetch count为1。

### 路由策略
- 所有类型的消息都发送到同一个exchange中
- 每种类型的消息都单独配置一个exchange
- 对消息类型进行归类，同一类型的消息对应一个exchange

** DDD ** 
每一个聚合根下发布的所有类型的事件对应一个exchange，exchange设置为topic，
queue可以配置接收某一种类型的事件，也可以配置接收所有某种聚合相关的事件，还可以配置接收所有事件。

### 异常处理
broker不可达：直接抛出异常；
- 发送方自己始终发送不出去：消息状态始终处于“未发送”，不会破坏一致性，但是对于事件表中累计太多的事件需要关注；
- exchange不存在：消息被丢掉，rabbitmq不会ack，消息状态始终处于“未发送”，下次将重新发送，不会破坏一致性，但是当exchange持续不存在下去，那么事件表中事件也会累计太多；
- exchange存在但是没有接受queue：消息将被ack并标记为“已发送”，但由于设置了alternative exchange为dlx，那么消息将发送到dlx对应的dlq中保存以便后续处理；
- consumer不在线，而累积消息太多：消息一致性没有问题，但是当累计到了max-length上限，消息队列头部的消息将被放置dlq中以便后续处理；
- consumer临时性失败：通过redelivered判断是否为重复投递，如果是，则nack并且requeue=false，表示如果重复投递的一次的消息如果再失败，那么直接扔到dlx中，也即消息最多重复投递一次；
- consumer始终失败：所有消息均被投入dlq以便后续处理，此时可能需要关注dlq的长度是否太长。