package com.spn.benchmark.messaging.openmq;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.spn.benchmark.messaging.processor.MessageConfigurator;
import com.spn.benchmark.messaging.processor.MessageGenerator;

/**
 * Sends all generated messages to the queue of the message broker.
 *
 * For some scenarios it is recoomended to configure your glassfish server. The maximal count of
 * unconsumed messages should be increased depending on your scenario. The default value is 1000,
 * so if you queue 1 million messages your value should be 1 million.
 */
public class Sender {

	private final static String QUEUE_NAME = "openMQ_UMS";
	private final static double TIME_FACTOR = MessageConfigurator.TIME_FACTOR;

	public static void main(String[] argv) {
		ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

		try {
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(QUEUE_NAME);
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			MessageGenerator processor = new MessageGenerator();

			// to avoid measurement issues caused by the garbage collector, the send time is added every loop iteration
			long sendTimeTotal = 0;
			for(int i=1; i <= processor.getOptimizedMaxCount(); i++) {
				String text = processor.generateMessage();

				long startSendTime = 0;
				if (i == processor.getSkipCount()) {
					startSendTime = System.nanoTime();
				}

				BytesMessage message = session.createBytesMessage();
				message.writeBytes(text.getBytes());

				producer.send(message);

				if (i >= processor.getSkipCount()) {
					sendTimeTotal += (System.nanoTime() - startSendTime);
				}
			}

			System.out.println("Needed " + (sendTimeTotal/TIME_FACTOR) + "ms to send " + (processor.getMaxCount()) + " messages");

			producer.close();
			session.close();
			connection.close();

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}