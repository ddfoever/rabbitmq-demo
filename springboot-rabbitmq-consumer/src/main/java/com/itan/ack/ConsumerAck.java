package com.itan.ack;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class ConsumerAck {

    @RabbitListener(queues="ack_topic_queue")
    public void getMessage(Message message, Channel channel) throws Exception {
        Thread.sleep(1000);
        try {
            System.out.println("message ack is received: "+new String(message.getBody(),"UTF-8"));
            int i = Integer.valueOf(new String(message.getBody(),"UTF-8"));
            if(i%2==0){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }else{
                int a = 5/0;
            }
        } catch (Exception e) {
            System.out.println("throw a exception, retry.......");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);//第三个参数是requeue 是否返回队列,true 返回队列重新发送，false 则消息丢弃
        }
        System.out.println("dfd.................");
//        channel.basicReject();
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
