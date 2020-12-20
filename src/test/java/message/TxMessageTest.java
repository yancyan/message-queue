package message;

import com.message.MessageApplication;
import com.message.component.QueueBComp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MessageApplication.class)
public class TxMessageTest {

    @Autowired
    QueueBComp queueBComp;

    @Test
    public void test_send_01() {

        queueBComp.send_01("send_01_content.");

    }
    @Test
    public void test_send_02() {

        queueBComp.send_02("send_02_content.");

    }

}
