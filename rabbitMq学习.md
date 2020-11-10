



## RabbitMq 学习总结

### 1.MQ

#### 1.1 不同mq的特点

```reStructuredText
# 1. ActiveMq
     ActiveMq是Apache出品，最流行的，能力最强劲的小心总线。它是一个完全支持JMS规范的消息中间件。丰富的API,多种集群架构模式让ActiveMQ在业界成为老牌的消息中间件，在中小型企业颇为欢迎。
     
# 2. Kafka
     Kafka是linked-in开源的分布式发布订阅消息系统，目前归属Apache顶级项目。Kafka主要特点是基于pull的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志的收集和传输。0.8版本后开始支持复制，不支持事务，对消息的重复，丢失，错误没有严格要求。适合产生大数据量的互联网服务的数据收集业务。
     
# 3. RocketMQ
     RocketMq是阿里开源的小心中间件，它是纯java开发，具有高吞吐量，高可用性，适合大规模分布式系统应用的特点，RocketMQ思路起源于Kafka，但并不是kafka的一个copy，他对消息的可靠性传输及事务性做了处理和优化，目前在阿里集团被广泛应用于交易、充值、流计算、消息推送、日志流式处理、binglog分发等场景
     
# 4. RabbitMQ
     Rabbitmq 是使用Elang语言开发的，基于AMQP协议来实现，AMQP协议主要特征面向消息、队列、路由（包含点对点和发布/订阅）、可靠性、安全性。AMQP协议更多用在企业系统内对数据一致性、稳定性和可靠性要求很高的场景，对性能和吞吐量换在其次。
     

```

```java
/***
Rabbitmq 比kafka更可靠，kafka更适合吞吐量比较大的业务，一般应用于大数据日志处理或者对实时性（少量延迟），可靠性（少了丢失数据）要求低的场景使用
***/
```

### 2. RabbitMq的安装

 ```java
基于AMQP协议，elang语言开发的（专门做socket开发），是部署最广泛的开源消息中间价，是最受欢迎的消息中间之一。（几乎不丢失消息）
 ```

基于`Ubuntu 20.0.4 version`  `rabbitmq-server 3.8.9` `Erlang 23.1.1`

```bash
curl -fsSL https://github.com/rabbitmq/signing-keys/releases/download/2.0/rabbitmq-release-signing-key.asc | sudo apt-key add -
```

```bash
sudo apt-key adv --keyserver "hkps://keys.openpgp.org" --recv-keys "0x0A9AF2115F4687BD29803A206B73A36E6026DFCA"
```

为了使用apt能够安装rabbitmq server和Erlang包，开启apt https tranport

```bash
sudo apt-get install apt-transport-https
```

添加源文件列表` /etc/apt/sources.list.d/bintray.erlang.list`

```bash
## Installs the latest 22.x version available in the repository.
## Please see the distribution name table above.

deb https://dl.bintray.com/rabbitmq-erlang/debian bionic erlang-22.x
```

更新apt 源

```bash
sudo apt-get update -y
```

```bash
# This is recommended. Metapackages such as erlang and erlang-nox must only be used
# with apt version pinning. They do not pin their dependency versions.
sudo apt-get install -y erlang-base \
                        erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets \
                        erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key \
                        erlang-runtime-tools erlang-snmp erlang-ssl \
                        erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl
```



start downloading

```bash
#!/bin/sh

## If sudo is not available on the system,
## uncomment the line below to install it
# apt-get install -y sudo

sudo apt-get update -y

## Install prerequisites
sudo apt-get install curl gnupg -y

## Install RabbitMQ signing key
curl -fsSL https://github.com/rabbitmq/signing-keys/releases/download/2.0/rabbitmq-release-signing-key.asc | sudo apt-key add -

## Install apt HTTPS transport
sudo apt-get install apt-transport-https

## Add Bintray repositories that provision latest RabbitMQ and Erlang 23.x releases
sudo tee /etc/apt/sources.list.d/bintray.rabbitmq.list <<EOF
## Installs the latest Erlang 23.x release.
## Change component to "erlang-22.x" to install the latest 22.x version.
## "bionic" as distribution name should work for any later Ubuntu or Debian release.
## See the release to distribution mapping table in RabbitMQ doc guides to learn more.
deb https://dl.bintray.com/rabbitmq-erlang/debian bionic erlang
## Installs latest RabbitMQ release
deb https://dl.bintray.com/rabbitmq/debian bionic main
EOF

## Update package indices
sudo apt-get update -y

## Install rabbitmq-server and its dependencies
sudo apt-get install rabbitmq-server -y --fix-missing
```

### 3. RabbitMQ 快速操作

```xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.6.0</version>
</dependency>
```



  ```java
/**
java client 生产者
**/
package com.itan;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * java 客户端方式 （生产者）
 */
public class RabbitmqProducer {
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
        channel.queueDeclare("hello_queue",true,false,false,null);
        //6.发送消息
        /**
         * basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
         * 1. exchange 交换机名称，简单模式下 交换机是默认的""
         * 2. routingkey 路由名称  交换机为默认的"" ,则routingkey 必须是和queue 一样
         * 3. props 配置信息
         * 4. body 消息
         */
        channel.basicPublish("","hello_queue",null,"我是消息".getBytes());

        //释放资源
        channel.close();
        connection.close();
    }
}

    
    
  ```

消费端：

```java
package com.itan;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitmqConsumer {
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
                System.out.println("consumertag: "+consumerTag);
                System.out.println("envelope: "+envelope.toString());
                System.out.println("properties: "+properties.toString());
                System.out.println("body: "+new String(body));
            }
        };
        /**
         * (String queue, boolean autoAck, Consumer callback)
         * 1. queue 队列名称
         * 2. autoAck 是否自动确认 如果false 则消息为unacked，容易造成消息堆积。
         * 3. callback 回调函数
         */
        channel.basicConsume("hello_queue",true,consumer);

//        channel.close();
//        connection.close();

    }
}
```

### 4. RabbitMq 的工作模式

#### 4.1 workqueue工作队列模式

```java
//工作队列
1. 一个队列中如果绑定了多个消费者，那么消费者之间是竞争关系（轮询方式）
```

`java 代码如下`

```java
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
        //一次生产10条消息
        for(int i=1;i<=10;i++){
            String body = i+"rabbit mq message .....";
            channel.basicPublish("","work_queue",null,body.getBytes());
        }

        //释放资源
        channel.close();
        connection.close();
    }
}

```

`workqueue_consumer1 代码`

```java

```

####  4.2 Pub/Sub 发布订阅模式

![image-20201109233906456](image-20201109233906456.png)

> 模式说明：在订阅模型中多一个Exchange 角色，而过程略有变化

+ P: 生产者，也就是消息发送者，但是不再 发送到队列中，而是发给X（交换机）
+ C:消费者，消息的接收者，会一直等待消息的到来。
+ Queue：消息队列，接收消息，缓存消息
+ Exchange：交换机（X），一方面，接收生产者发送的消息，另一方面，知道如何处理消息，例如提交给某个特别队列、递交给所有队列、或是将消息丢弃。到底如何操作，取决Exchange类型。Exchange有常见一下3种类型：
  - Fanout: 广播，将消息交给所有绑定到交换机的队列
  - Direct：定向，把消息交给符合routingkey的队列
  - Topic：通配符，把消息交给符合routing pattern（路由模式）的队列

<font color='red'>Exchange</font> 只负责转发消息，不具备存储消息的能力，因此如果没有任何队列与Exchange绑定，或者没有符合路由规则的队列，那么消息会丢失。

> 注意：若指定交换机类型为fanout ，则不用指定routingkey，因为fanout是将消息广播给与他绑定的所有queue

##### 4.2.1 Fanout 模式

   没有routingkey ，默认是"",会把消息发送给所有与fanout交换机绑定的队列

##### 4.2.2 Direct 模式

  全量匹配 routingkey

##### 4.2.3 Topic 模式

通配符 *和#代替，*  * 代表一个单词，#代表一个或者多个











