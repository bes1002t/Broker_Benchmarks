package com.spn.benchmark.messaging.activemq.openwire;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.spn.benchmark.messaging.processor.MessageConfigurator;
import com.spn.benchmark.messaging.processor.MessageGenerator;

/**
 * Sends all generated messages to the queue of the message broker.
 *
 * ActiveMQ is supporting many messaging protocols. The default protocol is OpenWire.
 * This Benchmark could be used with the OpenWire and the AMQP protocol. If you need
 * the OpenWire protocol, just comment in the line with the tcp connection and the related queue name.
 * If you wanna use AMQP as messaging protocol, comment in the line with the amqp connection and the related queue name.
 * To use ActiveMQ with AMQP the QPID AMQP-JMS library is used.
 *
 */
public class OpenwireSender {

	private final static String QUEUE_NAME = "activeMQ_OpenWire";
	private final static double TIME_FACTOR = MessageConfigurator.TIME_FACTOR;

	public static void main(String[] argv) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		factory.setUseAsyncSend(true);

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

			System.out.println("Needed " + (sendTimeTotal/TIME_FACTOR) + "ms to send " + processor.getMaxCount() + " messages");

			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}