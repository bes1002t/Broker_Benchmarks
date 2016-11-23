package com.spn.benchmark.messaging.activemq.openwire;

import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.spn.benchmark.messaging.processor.MessageReader;

/**
 * Receives all messages from the message brokers queue.
 *
 * ActiveMQ is supporting many messaging protocols. The default protocol is OpenWire.
 * This Benchmark could be used with the OpenWire and the AMQP protocol. If you need
 * the OpenWire protocol, just comment in the line with the tcp connection and the related queue name.
 * If you wanna use AMQP as messaging protocol, comment in the line with the amqp connection and the related queue name.
 *
 */
public class OpenwireReceiver {

	private final static String QUEUE_NAME = "activeMQ_OpenWire";
	private final static int TIMEOUT = 1000000000;

	public static void main(String args[]) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

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

					// create a byte array with the messages size
					byte body[]= new byte[(int) bytesMessage.getBodyLength()];
					// write information to the byte array
					bytesMessage.readBytes(body);

					try {
						// create a string from byte array
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