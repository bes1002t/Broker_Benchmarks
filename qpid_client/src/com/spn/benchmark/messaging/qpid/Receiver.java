package com.spn.benchmark.messaging.qpid;

import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnectionFactory;

import com.spn.benchmark.messaging.processor.MessageReader;

/**
 * Receives all messages from the message brokers queue.
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
 * This receiver could be used for both, the java QPID broker and the c++ QPID broker.
 */
public class Receiver {

	private final static String QUEUE_NAME = "QPID_AMQP";
	private final static int TIMEOUT = 1000000000;

	public static void main(String args[]) {
		JmsConnectionFactory factory = new JmsConnectionFactory("guest", "guest", "amqp://localhost?amqp.traceFrames=true");
		factory.setCloseTimeout(300000);

		try {
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(QUEUE_NAME);
			MessageConsumer consumer = session.createConsumer(destination);

			MessageReader processor = new MessageReader(QUEUE_NAME);
			while(true) {
				Message message = consumer.receive(TIMEOUT);

				if (message instanceof BytesMessage) {
					BytesMessage bytesMessage = (BytesMessage) message;

					byte body[]= new byte[(int) bytesMessage.getBodyLength()];
					bytesMessage.readBytes(body);

					try {
						String text = new String(body, "UTF-8");
						processor.printResult(body.length, text);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		} catch(JMSException e) {
			e.printStackTrace();
		}
	}
}