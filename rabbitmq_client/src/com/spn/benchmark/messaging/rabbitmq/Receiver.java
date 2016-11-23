package com.spn.benchmark.messaging.rabbitmq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.spn.benchmark.messaging.processor.MessageReader;

/**
 * Receives all messages from the message brokers queue.
 *
 * RabbitMQ is using the user "guest" per default. This is only working if you connect
 * to a RabbitMQ broker that is running on your local machine. Otherwise you have to create
 * a new user. That's why the "user" User is used per default in this benchmark. So to
 * create a session you have to install the RabbitMQ management plugin, open it's GUI
 * and create a new user with the name "user" and the password "user". If you are
 * working on a local machine you can change the credentials below.
 */
public class Receiver {
	private final static String QUEUE_NAME = "rabbitmq_AMQP";

	public static void main(String[] argv) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("user");
		factory.setPassword("user");

		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);

			final MessageReader processor = new MessageReader(QUEUE_NAME);
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
					try {
						String message = new String(body, "UTF-8");

						processor.printResult(body.length, message);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			};

			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}