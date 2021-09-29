package com.behl.brahma.configuration;

import java.util.List;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeclarablesConfiguration {

    // Create Dead Letter Exchange (DLX)
    public FanoutExchange dlxExchange() {
        return new FanoutExchange("error-exchange");
    }

    // Create Queue for listening to objects that are passed to DLX
    public Queue dlxNonAdultUserErrorHandlingQueue() {
        return new Queue("dlx-non-adult-handing-queue");
    }

    /*
     * Bind the Dead letter exchange with the queue that expects objects that caused
     * error in the main queue
     */
    public Binding dlxExchangeAndErrorHandlinQueueBinding() {
        return BindingBuilder.bind(dlxNonAdultUserErrorHandlingQueue()).to(dlxExchange());
    }

    // Create a Direct Exchange to get user objects
    public DirectExchange userRegisterationExchange() {
        return new DirectExchange("user-registeration-exchange");
    }

    /*
     * Create queue to which user object will be broadcasted to. Set the DLX as the
     * exchange created above. Any object that is rejected by this queue with retry
     * = false, will be sent to DLX
     */
    public Queue adultUserLogQueue() {
        return QueueBuilder.durable("adult-user-logging-queue")
                .withArgument("x-dead-letter-exchange", dlxExchange().getName()).build();
    }

    // Bind the main queue with the main exchange
    public Binding userRegisterationExchangeAndAdultLogQueueBinding() {
        return BindingBuilder.bind(adultUserLogQueue()).to(userRegisterationExchange()).with("user.adult");
    }

    /*
     * Pass all the above created exchanges, queues and Bindings (Declarable) to the
     * Declarables constructor and register it as a bean to let spring automatically
     * create the above if they don't exist in the configured RabbitMQ instance
     */
    @Bean
    public Declarables declarables() {
        return new Declarables(List.of(userRegisterationExchange(), adultUserLogQueue(),
                userRegisterationExchangeAndAdultLogQueueBinding(), dlxExchange(), dlxNonAdultUserErrorHandlingQueue(),
                dlxExchangeAndErrorHandlinQueueBinding()));
    }

}
