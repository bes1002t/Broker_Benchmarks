package com.spn.benchmark.messaging.processor;

import java.util.Arrays;

/**
 * Generates the configured amount of messages with the configured size.
 */
public class MessageGenerator {

	private static final int SKIP_COUNT = MessageConfigurator.OPTIMIZATION_FACTOR;
	private static final int MAX_MEASURE_COUNT = MessageConfigurator.MAX_COUNT;

	/**
	 * @return The count of all messages to send
	 */
	public int getOptimizedMaxCount() {
		return MAX_MEASURE_COUNT + SKIP_COUNT;
	}

	/**
	 * @return The count of all messages to send which should be not ignored by the reading instance
	 */
	public int getMaxCount() {
		return MAX_MEASURE_COUNT;
	}

	/**
	 * @return The count of all messages to send which should be ignored by the reading instance
	 */
	public int getSkipCount() {
		return SKIP_COUNT;
	}

	/**
	 * Generates a message string using the properties configured in the MessageConfigurator
	 * @return String to send
	 */
	public String generateMessage() {
		long sendTime = System.nanoTime();

		return MessageConfigurator.SEPERATOR + String.valueOf(sendTime) + MessageConfigurator.SEPERATOR + getChars(MessageConfigurator.MESSAGE_SIZE, 't');
	}

	/**
	 * Generates a string by creating an char array which length depends on the configured message size.
	 *
	 * @param length Size of the array
	 * @param charToFill Character that should be used to fill that array
	 * @return String with the configured size to append to the message
	 */
	private String getChars(int length, char charToFill) {
		if (length > 0) {
			char[] array = new char[length];
			Arrays.fill(array, charToFill);
			return new String(array);
		}
		return "";
	}
}
