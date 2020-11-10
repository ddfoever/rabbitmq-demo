package com.itan;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作队列模式 workqueue
 */
public class WorkQueueProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.建立连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        //2.设置参数
        connectionFactory.setHost("192.168.179.129");
        connectionFactory.setPassword("root");
        connectionFactory.setUsername("root");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/mq-vs");
        //3.创建连接Connection
        Connection connection = connectionFactory.newConnection();
        //4.创建Channel
        Channel channel = connection.createChannel();
        //5.创建队列Queue
        /**
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * 1. queue 队列名称
         * 2. durable 是否持久化，当mq重启之后消息还在
         * 3. exclusive 一，是否独占（只能有一个消费者监听队列）二. 当connection关闭时 是否删除队列
         * 4.autoDelete 没有consumer时，自动删除掉
         * 5. arguments 参数
         */
        //如果没有一个叫hello_queue 的队列，则会自动创建
        channel.queueDeclare("work_queue",true,false,false,null);
        //6.发送消息
        /**
         * basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
         * 1. exchange 交换机名称，简单模式下 交换机是默认的""
         * 2. routingkey 路由名称  交换机为默认的"" ,则routingkey 必须是和queue 一样
         * 3. props 配置信息
         * 4. body 消息
         */
        for(int i=1;i<=10;i++){
            String body = i+"rabbit mq message .....";
            channel.basicPublish("","work_queue",null,body.getBytes());
        }

        //释放资源
        channel.close();
        connection.close();
    }
}
