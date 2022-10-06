package main.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfiguration {

  @Bean
  public Queue createQueue() {
    return new Queue("exampleQueue");
  }

  @Bean
  public Binding createBindingBetweenQueueAndMqttTopic() {
    return new Binding("exampleQueue", Binding.DestinationType.QUEUE, "amq.topic", "exampleQueue", null);
  }
}
