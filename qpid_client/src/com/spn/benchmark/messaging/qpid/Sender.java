package com.spn.benchmark.messaging.qpid;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnectionFactory;

import com.spn.benchmark.messaging.processor.MessageConfigurator;
import com.spn.benchmark.messaging.processor.MessageGenerator;

/**
 * Sends all generated messages to the queue of the message broker.
 * In this benchmark the qpid JMS-AMQP library is used, that's why a JMSConnectionFactory is needed.
 * The QPID server is using AMQP per default, so it is necessary to use the AMQP messaging protocol.
 *
 * To create a session for this test setup you have to edit the qpidd.conf file. Just add "auth=no"
 * and you can create a session without authentication.
 *
 * Probably it is required to create the queue manually using the server configuration interface.
 * I don't know why but creating a destionation programmatically won't create the queue. All other
 * testet brokers are able to do that.
 *
 * This sender could be used for both, the java QPID broker and the c++ QPID broker.
 */
public class Sender {

	private final static String QUEUE_NAME = "QPID_AMQP";
	private final static double TIME_FACTOR = MessageConfigurator.TIME_FACTOR;

	public static void main(String[] argv) {
		JmsConnectionFactory factory = new JmsConnectionFactory("guest", "guest", "amqp://localhost?amqp.traceFrames=true");
		factory.setCloseTimeout(300000);

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