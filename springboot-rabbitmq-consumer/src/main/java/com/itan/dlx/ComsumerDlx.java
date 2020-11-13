package com.itan.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ComsumerDlx {

    @RabbitListener(queues = "test_dlx_queue")
    public  void dlx(Message message, Channel channel) throws IOException {
        try {
            System.out.println("start to consumer...");
            int i = 1/0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }

    }
}
