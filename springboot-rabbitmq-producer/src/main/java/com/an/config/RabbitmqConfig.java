package com.an.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {
    /**
     * 以topic 为例
     *
     */
    public static final String EXCHANGE_NAME = "boot_topic_exchange";

    public static final String ACK_EXCHANGE_NAME = "ack_topic_exchange";

    public static final String QUEUE_NAME = "boot_topic_queue";

    public static final String ACK_QUEUE = "ack_topic_queue";

    /**
     * create queue
     */
    @Bean("bootQueue")
    public Queue bootQueue(){
        return QueueBuilder.durable(QUEUE_NAME).build();//durable 带参数默认了 durable=true
    }

    /**
     * create exchange
     */
    @Bean("bootExchange")
    public Exchange bootExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean("ackExchange")
    public Exchange ackExchange(){
        return ExchangeBuilder.topicExchange(ACK_EXCHANGE_NAME).durable(true).build();
    }
    @Bean("ackQueue")
    public Queue ackQueue(){
        return QueueBuilder.durable(ACK_QUEUE).build();//durable 带参数默认了 durable=true
    }

    @Bean
    public Binding bindAck(@Qualifier("ackQueue") Queue queue,@Qualifier("ackExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("ack.#").noargs();
    }


    /**
     * binding exchange and queue
     */
    @Bean
    public Binding bindExchangeQueue(@Qualifier("bootQueue") Queue queue,@Qualifier("bootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();
    }

//
//    @Bean
//    public RabbitTemplate rabbitTemplate(){
//        return new RabbitTemplate();
//    }

    //死信队列---1.创建一个正常队列和交换机
    //2. 创建死信队列和交换机


    //开始创建死信队列
    @Bean("dlx_queue")
    public Queue dlxQueue(){
        return QueueBuilder.durable("dlx_queue").build();
    }

    @Bean("dlx_exchange")
    public Exchange dlxExchange(){
        return ExchangeBuilder.topicExchange("dlx_exchange").durable(true).build();
    }

    @Bean
    public Binding dlxBindingExchange(@Qualifier("dlx_queue") Queue queue,@Qualifier("dlx_exchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dlx.#").noargs();
    }

    // --正常队列
    @Bean("test_dlx_queue")
    public Queue testDlxQueue(){
        //正常队列绑定死信队列
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-max-length",10);
        arguments.put("x-message-ttl",10000);
        arguments.put("x-dead-letter-exchange","dlx_exchange");//正常队列绑定死信交换机，当消息变成dead message 时候，就可以发送此消息到死信队列中
        arguments.put("x-dead-letter-routing-key","dlx.hh");//路由到死信队列
        return QueueBuilder.durable("test_dlx_queue").withArguments(arguments).build();
    }
    //-正常交换机
    @Bean("test_dlx_exchange")
    public Exchange topicDlxExchange(){
        return ExchangeBuilder.topicExchange("test_dlx_exchange").durable(true).build();
    }
    //-绑定
    @Bean
    public Binding testBindingDlxExchange(@Qualifier("test_dlx_queue") Queue queue,@Qualifier("test_dlx_exchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("test.dlx.#").noargs();
    }


    /**
     * 实现延迟队列---》30分钟订单未确认 就进行取消
     * 步骤：1.定义订单的正常队列（order_queue）和交换机（order_exchange）
     * 2. 创建死信队列（order_delay_queue）和交换机(order_delay_exchange)用于存放30分钟后未确认的订单消息
     * 3. 正常队列绑定死信队列并且设置过期时间30min
     */
    //order 正常队列
    @Bean("order_queue")
    public Queue orderQueue(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order_delay_exchange");
        arguments.put("x-dead-letter-routing-key","order.delay.pay");
        arguments.put("x-message-ttl",10000);
        return QueueBuilder.durable("order_queue").withArguments(arguments).build();
    }
    @Bean("order_exchange")
    public Exchange orderExchange(){
        return ExchangeBuilder.topicExchange("order_exchange").durable(true).build();
    }

    @Bean
    public Binding bingdOrderExchange(@Qualifier("order_exchange") Exchange exchange,@Qualifier("order_queue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("order.#").noargs();
    }

    /**
     * order死信队列
     */
    @Bean("order_delay_queue")
    public Queue orderDelayQueue(){
        return QueueBuilder.durable("order_delay_queue").build();
    }

    @Bean("order_delay_exchange")
    public Exchange orderDelayExchange(){
        return ExchangeBuilder.topicExchange("order_delay_exchange").durable(true).build();
    }

    @Bean
    public Binding bingdOrderDelayQueue(@Qualifier("order_delay_queue") Queue queue,@Qualifier("order_delay_exchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("order.delay.#").noargs();
    }





}
