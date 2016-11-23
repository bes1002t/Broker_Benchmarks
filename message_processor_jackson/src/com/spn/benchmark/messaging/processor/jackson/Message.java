package com.spn.benchmark.messaging.processor.jackson;

/**
 * Message that will be sent from sender to receiver using a message broker
 */
public class Message {

	private long startTime = 0;
	private String dynamicLength = "";

	public Message() {

	}

	public Message(long startTime, String dynamicLength) {
		this.startTime     = startTime;
		this.dynamicLength = dynamicLength;
	}

	/**
	 * @return Starttime of measurement for this message
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @return String that is responsible for the message size
	 */
	public String getDynamicLength() {
		return dynamicLength;
	}

	/**
	 * Sets the start time of measurment for this message
	 * @param startTime
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Sets the dynamic length of the message
	 * @param dynamicLength
	 */
	public void setDynamicLength(String dynamicLength) {
		this.dynamicLength = dynamicLength;
	}
}
