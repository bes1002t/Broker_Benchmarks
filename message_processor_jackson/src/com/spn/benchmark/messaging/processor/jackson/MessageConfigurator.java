package com.spn.benchmark.messaging.processor.jackson;

public class MessageConfigurator {

	/**
	 * Configure how many messages will be sent
	 */
//	public static final int MAX_COUNT = 200;
//	public static final int MAX_COUNT = 2000;
//	public static final int MAX_COUNT = 20000;
	public static final int MAX_COUNT = 2000000;

	/**
	 * Configure the size of a message in byte
	 */
//	public static final int MESSAGE_SIZE = 15000000;
	public static final int MESSAGE_SIZE = 0;

	/**
	 * Constant to calculate seconds from milliseconds
	 */
	public static final double TIME_FACTOR = 1000000;

	/**
	 * Constant that will be added to MAX_COUNT to trigger JVM optimization
	 */
	public static final int OPTIMIZATION_FACTOR = (MAX_COUNT / 2);

}
