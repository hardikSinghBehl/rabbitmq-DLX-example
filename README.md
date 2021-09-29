## Spring-RabbitMQ Dead letter Exchange(DLX) example
Messages can live forever in a queue, if no consumer consumes it or is rejected by the consumer/subscriber with requeue=true when an exception occurs in consumer/subsriber method.

RabbitMQ has a mechanism called DLX, or Dead Letter Exchange. Messages from a queue can be 'dead-lettered', i.e republished to another exchange when any of the following events occur:

- The message is rejected with *requeue=false (will result in possible infinite loop if requeue=true)*
- The TTL (time to live) for the message expires
- The queue length limit is exceeded

This mechanism is useful to handle invalid messages, without discarding it.

```
Incase of an exception in the method annotated with @RabbitListener, Spring will by default automatically 
requeue the message and most probably get stuck in an infinte loop, since the input isn't changing... 
so if the exception occured once, it'll most likely occur on every method execution.
```
Dead letter exchange(DLX) is just an ordinary exchange (can be any of the four types) to which data can be sent when it is rejected by the consumer (set requeue = false as well) and the DLX can broadcast it to another queue that has appropriate listener for error handling.

## Configuring DLX with a queue

1. Create a DLX queue (any type)
2. When creating the main queue configure the below argument
    - `x-dead-letter-exchange` (with the name of the DLX as value)
    - `x-dead-letter-routing-key` (not required if using fanout or header exchange as DLX)

```java
    public FanoutExchange dlxExchange() {
        return new FanoutExchange("dlx-exchange");
    }
		
		public Queue adultUserLogQueue() {
        return QueueBuilder.durable("adult-user-log-queue")
                .withArgument("x-dead-letter-exchange", dlxExchange().getName()).build();
    }
```

Any subscriber of `adult-user-log-queue` when rejects the message with requeue = false,the message will be discarded from the queue and sent to the `dlx-exchange` like a normal message which can be then handled by a subscriber of queue binded with `dlx-exchange`.

---
## Application Description
* For the sake of simplicity, this application is treated both as a publisher and listener to the above queues (divided under their appropriate packages)
* We create all the required Decalarables (Echanges, Queues and Bindings) inside a configuration class [DeclarablesConfiguration.class](https://github.com/hardikSinghBehl/rabbitmq-DLX-example/blob/main/src/main/java/com/behl/brahma/configuration/DeclarablesConfiguration.java) and let spring handle creation if they do not exist in the confiured RabbitMQ instance
* We push a User DTO object to the main exchange with the age of the user being randomly generated
* The listener of the main queue throws `AmqpRejectAndDontRequeueException.class` if the recieved users age is less than 18
* The above object is broadcasted to the configured dead letter exchange which is further sent to the queue binded to it (Fanout Exchange)
* The queue binded with DLX logs the user's id and error 

---
## Local Setup
* Install Java 17 (Below command using [SdkMan](https://sdkman.io))

`sdk install java 17-open`
* Install Maven (Below command using [SdkMan](https://sdkman.io))

`sdk install maven`

* Install Erlang 

`brew install erlang`

* Install RabbitMQ

`brew install rabbitmq`

* Start RabbitMQ

`brew services start rabbitmq`

* Start Application

```
mvn spring-boot:run &
```
