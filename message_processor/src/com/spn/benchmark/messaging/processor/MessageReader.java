package com.spn.benchmark.messaging.processor;

/**
 * Analyses the received messages and prints a result to the standard output.
 */
public class MessageReader {

	/**
	 * Name of the queue the benchmark is using
	 */
	private String queue = "";

	private double timeTotal = 0;
	private double sendStartTime = 0;
	private long receivingStartTime = 0;

	private static final double TIME_FACTOR = MessageConfigurator.TIME_FACTOR;
	private static final int SKIP_COUNT = MessageConfigurator.OPTIMIZATION_FACTOR;
	private static final int MAX_COUNT = MessageConfigurator.MAX_COUNT;
	private int count;

	public MessageReader(String queue) {
		this.queue = queue;
	}

	/**
	 * Prints the result of the benchmark
	 *
	 * @param bodyLength Just needed for GSON and Jackson Message Processors
	 * @param message The message that is received from the Receiver
	 */
	public void printResult(long bodyLength, String message) {
		count++;

		/**
		 * while the sender measures all messages, usually the receiver is measuring n-1.
		 * This happens because the measurement process starts with the first skipped message.
		 * That's why it is necessary to start the measurement process at SKIP_COUNT - 1.
		 */
		if (count == (SKIP_COUNT - 1)) {
			receivingStartTime = System.nanoTime();
		}

		// Skip all messages <= SKIP_COUNT
		if (count >= SKIP_COUNT) {
			double msgReceivedTime = System.nanoTime();
			String[] messageParts =  message.split(MessageConfigurator.SEPERATOR);
			double msgSendStartTime = Double.valueOf(messageParts[1]);
			double msgSendTime = (msgReceivedTime - msgSendStartTime);
			timeTotal += msgSendTime;

			if (count == SKIP_COUNT) {
				sendStartTime = msgSendStartTime;
			}

			// Count of messages which are used for measurement
			int optimizedMsgCount = (count - SKIP_COUNT);
			System.out.println(optimizedMsgCount + "th Message needed " + (msgSendTime/TIME_FACTOR) + "ms, average is " + ((timeTotal/optimizedMsgCount)/TIME_FACTOR));

			if (optimizedMsgCount >= MAX_COUNT) {
				double neededTimeTotal = (msgReceivedTime - sendStartTime)/TIME_FACTOR;

				System.out.println(queue);
				System.out.println("Needed " + ((msgReceivedTime - receivingStartTime)/TIME_FACTOR) + "ms to receive " + optimizedMsgCount + " messages");
				System.out.println("The send and receive process needed " + neededTimeTotal + "ms for " + optimizedMsgCount + " messages");
				System.out.println("Scored " + (optimizedMsgCount / neededTimeTotal) + " messages per millisecond");
			}
		}
	}
}
