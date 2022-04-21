package com.bsds.ddf.server;

import com.bsds.ddf.server.rabbitmq.Receiver;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner2 implements CommandLineRunner {

  private final RabbitTemplate rabbitTemplate;
  private final Receiver receiver;

  public Runner2(Receiver receiver, RabbitTemplate rabbitTemplate) {
    this.receiver = receiver;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Sending message...");
    rabbitTemplate.convertAndSend(ServerApplication.topicExchangeName, "foo.bar.baz", "Hello from Praguna!");
    receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
  }

}