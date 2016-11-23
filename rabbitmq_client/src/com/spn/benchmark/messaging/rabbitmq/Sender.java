package com.spn.benchmark.messaging.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.spn.benchmark.messaging.processor.MessageConfigurator;
import com.spn.benchmark.messaging.processor.MessageGenerator;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Sends all generated messages to the queue of the message broker.
 *
 * RabbitMQ is using the user "guest" per default. This is only working if you connect
 * to a RabbitMQ broker that is running on your local machine. Otherwise you have to create
 * a new user. That's why the "user" User is used per default in this benchmark. So to
 * create a session you have to install the RabbitMQ management plugin, open it's GUI
 * and create a new user with the name "user" and the password "user". If you are
 * working on a local machine you can change the credentials below.
 */
public class Sender {
	private final static String QUEUE_NAME = "rabbitmq_AMQP";
	private final static double TIME_FACTOR = MessageConfigurator.TIME_FACTOR;

	public static void main(String[] argv) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("user");
		factory.setPassword("user");

		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			// To persist messages the queue has to be durable. That's why you have to set the second param to true
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);

			BasicProperties props = new BasicProperties();
			Builder builder = props.builder();
			// Delivery mode 1 is non-persistent. Set this value to 2 to make it persistent.
			builder.deliveryMode(1);
			BasicProperties newProps = builder.build();

			MessageGenerator processor = new MessageGenerator();

			// to avoid measurement issues caused by the garbage collector, the send time is added every loop iteration
			long sendTimeTotal = 0;
			for(int i=1; i <= processor.getOptimizedMaxCount(); i++) {
				String text = processor.generateMessage();

				long startSendTime = 0;
				if (i >= processor.getSkipCount()) {
					startSendTime = System.nanoTime();
				}

				channel.basicPublish("", QUEUE_NAME, newProps, text.getBytes());

				if (i >= processor.getSkipCount()) {
					sendTimeTotal += (System.nanoTime() - startSendTime);
				}
			}

			System.out.println("Needed " + (sendTimeTotal/TIME_FACTOR) + "ms to send " + processor.getMaxCount() + " messages");

			channel.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}