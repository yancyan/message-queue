package com.message.constants;

public final class InfrastructureConstants {
    public static final String queue_01 = "amqp.simple.directExTestQueue01";
    public static final String queue_02 = "amqp.simple.directExTestQueue02";
    public static final String queue_03 = "amqp.simple.directExTestQueue03";

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
}
