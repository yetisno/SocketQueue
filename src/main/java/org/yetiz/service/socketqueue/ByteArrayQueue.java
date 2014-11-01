package org.yetiz.service.socketqueue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yeti on 14/10/31.
 */
public class ByteArrayQueue extends LinkedBlockingQueue<byte[]> {

	private String queueName;
	private static ConcurrentHashMap<String, ByteArrayQueue> channelMap = new ConcurrentHashMap<String, ByteArrayQueue>();

	private ByteArrayQueue(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * get name of this queue.
	 *
	 * @return queue name.
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Get ByteArrayQueue from target queueName.
	 *
	 * @param queueName target queue name to get.
	 * @return the ByteArrayQueue instance of target queueName.
	 */
	public static ByteArrayQueue getChannel(String queueName) {
		if (queueName == null)
			throw new NullPointerException("queueName can't be null.");
		if (!channelMap.containsKey(queueName)) {
			return createChannel(queueName);
		}
		return channelMap.get(queueName);
	}

	private synchronized static ByteArrayQueue createChannel(String queueName) {
		if (!channelMap.containsKey(queueName)) {
			ByteArrayQueue byteArrayQueue = new ByteArrayQueue(queueName);
			channelMap.put(queueName, byteArrayQueue);
			return byteArrayQueue;
		}
		return channelMap.get(queueName);
	}

	/**
	 * Destroy all instance of ByteArrayQueue.
	 */
	public static void destroyAll() {
		channelMap.clear();
	}
}