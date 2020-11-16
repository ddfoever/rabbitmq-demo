package com.itan.dlx;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 延迟队列的消费者
 */
@Component
public class ComsumerDelayQueue {

    @RabbitListener(queues = "order_delay_queue")
    public  void dlx(Message message, Channel channel) throws IOException {
        try {
            System.out.println("开始订单业务方法....");
            System.out.println("订单超过30分钟未支付....");
            System.out.println("查询订单是否支付....");
            System.out.println("订单未支付....开始回滚库存");
//            int i = 1/0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }

    }
}
