package com.message.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface TestStream {


    String msg = "test-stream";
    String fail_msg = "fail-test-stream";

    @Input(TestStream.msg)
    SubscribableChannel input();


    @Output(TestStream.msg)
    MessageChannel out();

    @Output(TestStream.fail_msg)
    MessageChannel fail_out();

    @Input(TestStream.fail_msg)
    SubscribableChannel fail_in();
}
