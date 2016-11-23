package com.spn.benchmark.messaging.openmq;

import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import com.spn.benchmark.messaging.processor.MessageReader;

/**
 * Receives all messages from the message brokers queue.
 */
public class Receiver {

	private final static String QUEUE_NAME = "openMQ_UMS";
	private final static int TIMEOUT = 1000;

	public static void main(String args[]) {
		ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

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