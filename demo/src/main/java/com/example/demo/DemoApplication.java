package com.example.demo;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	final static String queueName = "*";//"spring-boot";
	final static String exchangeName = "amq.topic";//"spring-boot-exchange";

	@Autowired
	AnnotationConfigApplicationContext context;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(exchangeName);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {

		// ConnectionFactory factory = new ConnectionFactory();
		// factory.setHost("localhost");
		// Connection connection = factory.newConnection();
		// Channel channel = connection.createChannel();

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MyReceiver receiver() {
		return new MyReceiver();
	}

	@Bean
	MessageListenerAdapter listenerAdapter(MyReceiver receiver) {
		return new MessageListenerAdapter(receiver);// , "receiveMessage");
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Waiting five seconds...");
		// Thread.sleep(5000);
		// System.out.println("Sending message...");
		// rabbitTemplate.convertAndSend(queueName, "Hello from RabbitMQ!");
		// receiver().getLatch().await(10000, TimeUnit.MILLISECONDS);
		// context.close();
		callScanner();
	}

	private void callScanner() {
		while (true) {
			Scanner s = new Scanner(System.in);
			String line = s.nextLine();
			if (!line.equalsIgnoreCase("exit")) {
				rabbitTemplate.convertAndSend(queueName, line);
			} else {
				context.close();
				break;
			}
			// System.out.println(s.next());
		}

	}

}

class MyReceiver implements MessageListener {
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		System.out.println(message.getMessageProperties());
		System.out.println("Received <" + new String(message.getBody()) + ">");
	}
	// private CountDownLatch latch = new CountDownLatch(1);
	//
	// public void receiveMessage(String message) {
	// System.out.println("Received <" + message + ">");
	// latch.countDown();
	// }
	//
	// public void receiveMessage(byte[] message) {
	// System.out.println(message.toString());
	// latch.countDown();
	// }
	//
	// public CountDownLatch getLatch() {
	// return latch;
	// }

}
