package com.itan.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * topic 通配符
 */
public class TopicProducer {
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
        //5.创建交换机
        /**
         * (String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
         * 1. exchange 交换机名称
         * 2. type 交换机类型 BuiltinExchangeType
         *    BuiltinExchangeType
         *     DIRECT("direct"), 定向路由
         *     FANOUT("fanout"), 广播 只要与交换机绑定的queue 都会收到消息
         *     TOPIC("topic"),  通配路由
         *     HEADERS("headers");
         * 3. durable 是否持久化
         * 4. autoDelete 是否自动删除
         * 5.internal 内部使用一般为false
         * 6. arguments 参数
         */
        String exchangeName = "PUB-SUB-EXCHANGE-TOPIC";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC,true,false,false,null);
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
        channel.queueDeclare("topic-queue1",true,false,false,null);
        channel.queueDeclare("topic-queue2",true,false,false,null);
        channel.queueDeclare("topic-queue3",true,false,false,null);
        //7. 绑定交换机和队列
        /**
         * (String queue, String exchange, String routingKey)
         * 1. queue 队列名称
         * 2. exchange 交换机名称
         * 3. rotingkey 路由键，路由规则
         *     如果exchange是fanout，routingkey 则设置为：""
         *
         */
        channel.queueBind("topic-queue1",exchangeName,"topic.*");
        channel.queueBind("topic-queue2",exchangeName,"topic.queue.*");
        channel.queueBind("topic-queue3",exchangeName,"topic.#");

        //8. 发送消息
        channel.basicPublish(exchangeName,"topic.info",null,"topic info message".getBytes());
        channel.basicPublish(exchangeName,"topic.error.detail",null,"topic error message".getBytes());
        channel.basicPublish(exchangeName,"topic.queue.settings",null,"topic queue settings message".getBytes());
        //9. 释放资源
        channel.close();
        connection.close();
    }
}
