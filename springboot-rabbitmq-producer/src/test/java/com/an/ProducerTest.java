package com.an;


import com.an.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
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


    @Test
    public void testAck(){
        for(int i=1;i<=4;i++){
                rabbitTemplate.convertAndSend(RabbitmqConfig.ACK_EXCHANGE_NAME,"ack.send.test","test messaage");

        }
    }


    @Test
    public void testTtl(){

//        MessagePostProcessor processor = new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                message.getMessageProperties().setExpiration("5000");//设置单个消息的过期时间
//                return message;
//            }
//        };
        for(int i=1;i<=4;i++){
            rabbitTemplate.convertAndSend(RabbitmqConfig.ACK_EXCHANGE_NAME,"ack.send.test","test messaage");

        }
    }

    //死信队列
    //消息要成为死信消息有三种情况
    //1. 队列设置了TTL，导致消息过期，消费者没有及时消息到消息
    //2. 队列的消息长度大于消息的长度
    //3， 消费者拒绝了消息并且未将消息重新返回队列 requeue=false
    @Test
    public void testDlx(){
//
//        MessagePostProcessor processor = new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                message.getMessageProperties().setExpiration("5000");//设置单个消息的过期时间
//                return message;
//            }
//        };

        //先测试第一种情况，1.队列设置了TTL,消息过期情况
        //测试结果：当队列的消息过期后，该消息会自动路由到死信队列中去
//        rabbitTemplate.convertAndSend("test_dlx_exchange","test.dlx.hh","test dlx messaage");


        //第二种情况，消息超过队列长度 设置队列长度为10
        //测试结果如下,发送超过10的消息,从11条开始消息全部发送到死信队列中去了,并且过了10秒中消息过期全部路由到死信队列了
//        for (int i = 0; i < 20; i++) {
//            rabbitTemplate.convertAndSend("test_dlx_exchange","test.dlx.hh","test dlx messaage");
//        }
        //第三种情况： 消费者拒绝的情况 导致消息路由到死信队列
        rabbitTemplate.convertAndSend("test_dlx_exchange","test.dlx.hh","test dlx messaage");

//        for(int i=1;i<=4;i++){
//            rabbitTemplate.convertAndSend("test-dlx-exchange","test.dlx.queue","test dlx messaage");
//
//        }
    }

    /**
     * 延迟队列的实现
     * 发送消息
     */
    @Test
    public void testDelay() throws InterruptedException {
        rabbitTemplate.convertAndSend("order_exchange","order.pay","order send and waiting for payment....");
//        for (int i = 10; i > 0; i--) {
//            System.out.println("订单等待支付中.."+i+"s");
//            Thread.sleep(1000);
//        }
    }



}
