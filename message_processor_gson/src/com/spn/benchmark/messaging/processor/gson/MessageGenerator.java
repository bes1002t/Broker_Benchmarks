package com.spn.benchmark.messaging.processor.gson;

import java.util.Arrays;

import com.google.gson.Gson;

/**
 * Generates the configured amount of messages with the configured size.
 * The serialization is realised with Google Gson.
 */
public class MessageGenerator {

	private Gson gson = new Gson();

	private long encodeTime = 0;

	private static final double CALC_FACTOR = MessageConfigurator.TIME_FACTOR;
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
	 * Generates a message using the properties configured in the MessageConfigurator
	 * The message object is serialized to a json string.
	 *
	 * @return Message Json string to send
	 */
	public String generateMessage(int counter) {
		Message message = new Message();
		message.setDynamicLength(getChars(MessageConfigurator.MESSAGE_SIZE, 't'));
		message.setStartTime(System.nanoTime());

		long encodeStartTime = System.nanoTime();
		String jsonInString = gson.toJson(message);
		long encodeEndTime = System.nanoTime();

		int realCount = counter - getSkipCount();
		if (realCount >= getSkipCount()) {
			calcEncodeAverage(encodeStartTime, encodeEndTime);

			if (realCount >= getMaxCount()) {
				System.out.println("Needed " + (encodeTime/CALC_FACTOR) + "milliseconds to encode all Objects");
			}
		}

		return jsonInString;
	}

	/**
	 * Generates a string by creating an char array whose length depends on the configured message size.
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

	/**
	 * This method calculates the time that gson needs to serialize the message object to a string.
	 *
	 * @param encodeStartTime Start time of the encoding process
	 * @param encodeEndTime End time of the encoding process
	 */
	private void calcEncodeAverage(long encodeStartTime, long encodeEndTime) {
		long encodeObjectTime = (encodeEndTime - encodeStartTime);

		encodeTime += encodeObjectTime;
	}
}
