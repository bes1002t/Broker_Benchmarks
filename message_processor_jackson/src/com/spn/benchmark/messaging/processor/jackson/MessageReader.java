package com.spn.benchmark.messaging.processor.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Analyses the received messages and prints a result to the standard output.
 */
public class MessageReader {

	private ObjectMapper mapper = new ObjectMapper();

	private long decodeTimeTotal = 0;

	/**
	 * Name of the queue the benchmark is using
	 */
	private String queue = "";

	private long msgMinLength = 0;
	private double timeTotal = 0;
	private double sendStartTime = 0;
	private long receiveStartTime = 0;

	private static final double TIME_FACTOR = MessageConfigurator.TIME_FACTOR;
	private static final int SKIP_COUNT = MessageConfigurator.OPTIMIZATION_FACTOR;
	private int count;

	public MessageReader(String queue) {
		this.queue = queue;
	}

	/**
	 * Prints the result of the benchmark
	 *
	 * @param bodyLength Size of the message content
	 * @param messageStr Message json string
	 */
	public void printResult(long bodyLength, String messageStr) {
		count++;

		/**
		 * while the sender measures all messages, usually the receiver is measuring n-1.
		 * This happens because the measurement process starts with the first skipped message.
		 * That's why it is necessary to start the measurement process at SKIP_COUNT - 1.
		 */
		if (count == SKIP_COUNT - 1) {
			receiveStartTime = System.nanoTime();
		}

		// Skip all messages <= SKIP_COUNT
		if (count >= SKIP_COUNT) {
			double msgReceivedTime = System.nanoTime();

			if (msgMinLength == 0 || bodyLength < msgMinLength) {
				msgMinLength = bodyLength;
			}

			try {
				// This calculates the time that gson needs to deserialize the message string to an object.
				long decodeStartTime = System.nanoTime();
				Message message = mapper.readValue(messageStr, Message.class);
				long decodeEndTime = System.nanoTime();

				// This calculates the time that gson needs to deserialize all message strings to objects.
				long decodeObjectTime = (decodeEndTime - decodeStartTime);
				decodeTimeTotal += decodeObjectTime;

				// Access every object attribute to prevent parse optimization
				double msgSendStartTime = message.getStartTime();
				String msgDynamicLength  = message.getDynamicLength();
				double msgSendTime = (msgReceivedTime - msgSendStartTime);

				if (count == SKIP_COUNT) {
					sendStartTime = msgSendStartTime;
				}

				timeTotal += msgSendTime;

				// Count of messages which are used for measurement
				int optimizedMsgCount = (count - SKIP_COUNT);
				System.out.println(optimizedMsgCount + "th Message needed " + (msgSendTime/TIME_FACTOR) + "ms, average is " + ((timeTotal/optimizedMsgCount)/TIME_FACTOR));

				if (optimizedMsgCount >= MessageConfigurator.MAX_COUNT) {
					double neededTimeTotal = (msgReceivedTime - sendStartTime)/TIME_FACTOR;

					System.out.println(queue + " with Jackson");
					System.out.println("Needed " + ((msgReceivedTime - receiveStartTime)/TIME_FACTOR) + "ms to receive " + optimizedMsgCount + " messages");
					System.out.println("Message body min length was " + msgMinLength + " byte and max length was " + bodyLength + " byte");
					System.out.println("The send and receive process needed " + neededTimeTotal + "ms for " + optimizedMsgCount + " messages");
					System.out.println("Scored " + (optimizedMsgCount / neededTimeTotal) + " messages per millisecond");
					System.out.println("Jackson needed " + (decodeTimeTotal/TIME_FACTOR) + "milliseconds to decode all JSON strings");
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
