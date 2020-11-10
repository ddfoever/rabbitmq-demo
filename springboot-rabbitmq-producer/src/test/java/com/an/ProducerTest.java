package com.an;


import com.an.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * test producer message send
     */
    @Test
    public void test(){
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_NAME,"boot.send.test","springboot message sender.......");
    }
}
