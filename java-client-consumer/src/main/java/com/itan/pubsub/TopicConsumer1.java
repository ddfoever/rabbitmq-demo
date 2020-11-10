package com.itan.pubsub;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布订阅模式-消费者
 */
public class TopicConsumer1 {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //2.设置参数
        connectionFactory.setVirtualHost("/mq-vs");
        connectionFactory.setHost("192.168.179.129");
        connectionFactory.setUsername("root");
        connectionFactory.setPort(5672);
        connectionFactory.setPassword("root");
        //3. 创建连接
        Connection connection = connectionFactory.newConnection();
        //4. 创建channel
        Channel channel = connection.createChannel();
        //5. 消费消息
        Consumer consumer = new DefaultConsumer(channel){
            /**
             * 回调方法（当收到消息，会自动执行该方法）
             * @param consumerTag  消息唯一标识
             * @param envelope 获取一些交换机的信息，routingkey的信息等等
             * @param properties 配置信息
             * @param body  消息体
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("body consumer1: "+new String(body));
            }
        };
        /**
         * (String queue, boolean autoAck, Consumer callback)
         * 1. queue 队列名称
         * 2. autoAck 是否自动确认 如果false 则消息为unacked，容易造成消息堆积。
         * 3. callback 回调函数
         */
        channel.basicConsume("topic-queue1",true,consumer);

//        channel.close();
//        connection.close();
    }
}
