#### exec
监听过程异常，重试执行的原因分析
org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer.AsyncMessageProcessingConsumer.mainLoop
```
	while (isActive(this.consumer) || this.consumer.hasDelivery() || !this.consumer.cancelled()) {
					mainLoop();
				}

private void mainLoop() throws Exception { // NOSONAR Exception
			try {
				boolean receivedOk = receiveAndExecute(this.consumer); // At least one message received
				if (SimpleMessageListenerContainer.this.maxConcurrentConsumers != null) {
					checkAdjust(receivedOk);
				}
				long idleEventInterval = getIdleEventInterval();
				if (idleEventInterval > 0) {
					if (receivedOk) {
						updateLastReceive();
					}
					else {
						long now = System.currentTimeMillis();
						long lastAlertAt = SimpleMessageListenerContainer.this.lastNoMessageAlert.get();
						long lastReceive = getLastReceive();
						if (now > lastReceive + idleEventInterval
								&& now > lastAlertAt + idleEventInterval
								&& SimpleMessageListenerContainer.this.lastNoMessageAlert
								.compareAndSet(lastAlertAt, now)) {
							publishIdleContainerEvent(now - lastReceive);
						}
					}
				}
			}
			catch (ListenerExecutionFailedException ex) {
				// Continue to process, otherwise re-throw
				if (ex.getCause() instanceof NoSuchMethodException) {
					throw new FatalListenerExecutionException("Invalid listener", ex);
				}
			}
			catch (AmqpRejectAndDontRequeueException rejectEx) {
				/*
				 *  These will normally be wrapped by an LEFE if thrown by the
				 *  listener, but we will also honor it if thrown by an
				 *  error handler.
				 */
			}
		}
```

### 消息队列可靠性
####  消息生成者的可靠性
a. 消息事务（性能较差）
AMQP协议提供了事务机制，在投递消息时开启事务支持，如果消息投递失败，则回滚事务。
b. 发送方确认
详见：https://docs.spring.io/spring-amqp/docs/current/reference/html/#cf-pub-conf-ret
```yaml
spring:
  rabbitmq:
    publisher-returns: true
    publisher-confirm-type: correlated
```
消息未接受时，调用returncallback
```
rabbitTemplate.setMandatory(true);
```
消息确认失败后，我们可以进行消息补偿，也就是消息的重试机制。
```yaml
spring:
  rabbitmq:
    publisher-returns: true
    publisher-confirm-type: correlated
    listener:
      simple:
        retry:
          enabled: true
          # 最大重试次数
          max-attempts: 5
          # 重试时间间隔
          initial-interval: 3000
```
#### 消息发送到MQ后，MQ宕机导致内存中的消息丢失(各持久化控制应在声明阶段控制，)
消息在MQ中有可能发生丢失，这时候我们就需要将队列和消息都进行持久化。
@Queue注解为我们提供了队列相关的一些属性，具体如下：
```java
  class RabbitRequestParamKey {
          // How long a message published to a queue can live before it is discarded (milliseconds).
          public static final String X_TTL = "x-message-ttl";
          // How long a queue can be unused for before it is automatically deleted (milliseconds).
          public static final String X_EXPIRES = "x-expires";
          // 队列最大长度，超过该最大值，则将从队列头部开始删除消息；
          public static final String X_MAX_LENGTH = "x-max-length";
          // 队列消息内容占用最大空间，受限于内存大小，超过该阈值则从队列头部开始删除消息；
          public static final String X_LENGTH_BYTES = "x-max-length-bytes";
          // Optional name of an exchange to which messages will be republished if they are rejected or expire.
          public static final String X_DL_EX = "x-dead-letter-exchange";
          // Optional replacement routing key to use when a message is dead-lettered. If this is not set, the message's original routing key will be used.
          public static final String X_DL_RK = "x-dead-letter-routing-key";
          // Maximum number of priority levels for the queue to support; if not set, the queue will not support message priorities.
          public static final String X_PRIORITY = "x-max-priority";
          // Set the queue into lazy mode, keeping as many messages as possible on disk to reduce RAM
          public static final String X_MODE = "x-queue-mode";
      }
```
a. 持久化队列
创建队列的时候将持久化属性durable设置为true，同时要将autoDelete设置为false
```
@Queue(value = "javatrip",durable = "false",autoDelete = "false")
```
b. 持久化消息
MessageDeliveryMode.PERSISTENT
```
// 发送消息的时候将消息的deliveryMode设置为2，
 MessageBuilder mb = MessageBuilder.withBody(content.getBytes())
                .andProperties(MessagePropertiesBuilder.newInstance()
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                        .setAppId("message-test-id")
                        .build());
```
c. 交换机持久化
```
 * Actively declare a non-autodelete exchange with no extra arguments
 * @see com.rabbitmq.client.AMQP.Exchange.Declare
 * @see com.rabbitmq.client.AMQP.Exchange.DeclareOk
 * @param exchange the name of the exchange
 * @param type the exchange type
 * @param durable true if we are declaring a durable exchange (the exchange will survive a server restart)
 * @throws java.io.IOException if an error is encountered
 * @return a declaration-confirm method to indicate the exchange was successfully declared
 */
Exchange.DeclareOk (String exchange, String type, boolean durable) throws IOException;
```
d...

持久化机制：
1. 持久化时机
- 消息本身在publish的时候就要求消息写入磁盘
- 内存紧张，需要将部分内存中的消息转移到磁盘
2. 刷到磁盘的时机
-


#### 消息消费的可靠性
消费消息，还没有处理业务，结果发生异常。这时候就需要关闭自动确认，改为手动确认消息。
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        # 手动签收模式
        acknowledge-mode: manual
        # 每次签收一条消息
        prefetch: 1
```



### 死信
#### RabbitMQ的TTL（消息有效期）和DLX（死信交换机/队列）
##### TTL（消息有效期）
TTL（Time-To-Live）表示的是消息的有效期
- 声明队列的时候（队列可空
- 发消息时，在消息属性中设置（消息可空
  两者的区别：
  声明队列时设置有效期，则消息过期了就会被删掉；
  发消息时设置的有效期，消息过期了也不会被立马删掉，因为消息是否过期是在要投递给消费者时判断的。

如果不设置TTL，则表示此消息永久有效。如果将TTL设为0，如果消息不能被立马消费则会被立即丢掉

##### DLX（死信交换机/队列）
Dead Letter Exchange
1. 死信队列范围
- 消息被拒绝（basic.reject或basic.nack）并且requeue=false.
- 消息TTL过期
- 队列达到最大长度（队列满了，无法再添加数据到mq中）

2. 死信交换机
   在定义业务队列的时候，要考虑指定一个死信交换机，
   死信交换机可以和任何一个普通的队列进行绑定，然后在业务队列出现死信的时候就会将数据发送到死信队列。

3. 死信队列
   死信队列实际上就是一个普通的队列，只是这个队列跟死信交换机进行了绑定，用来存放死信而已。

4. 如何使用
   定义普通队列时，指定参数
```
x-dead-letter-exchange: 用来设置死信后发送的交换机
x-dead-letter-routing-key：用来设置死信的routingKey
```



### Test
https://www.docs4dev.com/docs/zh/spring-amqp/2.1.2.RELEASE/reference/_reference.html
#### 消息转换器 MessageConverter
```
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @RabbitListener(queues = InfrastructureConstants.queue_01)
    public void listena(PromotionMessage message, Channel channel) {

        log.info("############### listen " + message.toString());
    }
```

#### 消息转换器 MessageConverter
```
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @RabbitListener(queues = InfrastructureConstants.queue_01)
    public void listena(PromotionMessage message, Channel channel) {

        log.info("############### listen " + message.toString());
    }
```

























