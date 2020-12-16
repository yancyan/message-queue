package message;

import com.message.MessageApplication;
import com.message.component.QueueAComp;
import com.message.dto.PromotionMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;


@SpringBootTest(classes = MessageApplication.class)
public class MessageTest {

    @Resource
    QueueAComp queueAComp;

    @Test
    public void test() {
        queueAComp.sendA("test queue content.");
        System.out.println("######### ");
    }

    @Test
    public void test_trans() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            PromotionMessage promotionMessage = new PromotionMessage();
            promotionMessage.setStart(Instant.now());
            promotionMessage.setEnd(Instant.now());
            promotionMessage.setDays(12L + i);
            promotionMessage.setDesc("test send." + i);
            promotionMessage.setSendDate(LocalDateTime.now());

            queueAComp.sendPromotionMessage(promotionMessage);
        }


    }

    @Test
    public void test_send_2() throws InterruptedException {

        PromotionMessage promotionMessage = new PromotionMessage();
        promotionMessage.setStart(Instant.now());
        promotionMessage.setEnd(Instant.now());
        promotionMessage.setDays(12L);
        promotionMessage.setDesc("test send.");
        promotionMessage.setSendDate(LocalDateTime.now());

        queueAComp.send02(promotionMessage);



    }

    @Test
    public void test_send_() {
        try {
            PromotionMessage promotionMessage = new PromotionMessage();
            promotionMessage.setStart(Instant.now());
            promotionMessage.setEnd(Instant.now());
            promotionMessage.setDays(12L);
            promotionMessage.setDesc("test confirm");
            promotionMessage.setSendDate(LocalDateTime.now());

//            queueAComp.send02NoExist(InfrastructureConstants.queue_01, promotionMessage);
            queueAComp.send02NoExist("abc-abc-no-exits", promotionMessage);
        } catch (Exception e) {
            System.out.println("######## test_send_ end." + e.getMessage());
        }
    }

}
