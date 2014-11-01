package org.yetiz.util.io;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yeti on 14/11/1.
 */
public class IOBuffer {
	private int arrangeThreshold = 1024;
	private int dataArraySize = 1024;
	private List<byte[]> data;
	private ReentrantLock reentrantLock = new ReentrantLock();
	private int getListCursor;
	private int getArrayCursor;
	private int markListCursor;
	private int markArrayCursor;
	private int putListCursor;
	private int putArrayCursor;
	private boolean arranged = false;

	public IOBuffer() {
		clear();
	}

	/**
	 * clear this buffer and reinitialized.
	 */
	public void clear() {
		reentrantLock.lock();
		data = new ArrayList<byte[]>();
		data.add(new byte[dataArraySize]);
		getListCursor = 0;
		getArrayCursor = 0;
		markArrayCursor = -1;
		markListCursor = -1;
		putListCursor = 0;
		putArrayCursor = 0;
		reentrantLock.unlock();
	}

	private void _put(int b) {
		data.get(putListCursor)[putArrayCursor++] = (byte) b;
		if (putArrayCursor == 1024) {
			data.add(new byte[dataArraySize]);
			putListCursor += 1;
			putArrayCursor = 0;
		}
	}

	/**
	 * put byte into buffer
	 *
	 * @param b byte into buffer.
	 */
	public void put(int b) {
		reentrantLock.lock();
		_put(b);
		reentrantLock.unlock();
	}

	/**
	 * put bytes into buffer
	 *
	 * @param b bytes into buffer.
	 */
	public void put(byte[] b) {
		reentrantLock.lock();
		for (int i = 0; i < b.length; i++) {
			_put(b[i]);
		}
		reentrantLock.unlock();
	}

	private int _get() {
		int rtn = data.get(getListCursor)[getArrayCursor++];
		if (getArrayCursor == 1024) {
			getListCursor++;
			getArrayCursor = 0;
		}
		return rtn;
	}

	/**
	 * get byte from buffer.
	 *
	 * @return the next byte or -1 on there is no next byte.
	 */
	public int get() {
		if (remaining() == 0) {
			return -1;
		}
		reentrantLock.lock();
		int rtn = _get();
		reentrantLock.unlock();
		if (getListCursor > arrangeThreshold && !arranged) {
			if (markListCursor > arrangeThreshold || markListCursor == -1)
				arrange();
		}
		return rtn;
	}

	private void arrange() {
		arranged = true;
		Executors.newSingleThreadExecutor().execute(new ArrangeTask(this));
	}

	/**
	 * get the length readable bytes
	 *
	 * @return the length of unread bytes.
	 */
	public long remaining() {
		return (putListCursor - getListCursor) * dataArraySize + (putArrayCursor - getArrayCursor);
	}

	/**
	 * mark current read position for reset.
	 */
	public void mark() {
		reentrantLock.lock();
		markArrayCursor = getArrayCursor;
		markListCursor = getListCursor;
		reentrantLock.unlock();
	}

	/**
	 * reset read position to marked position.
	 */
	public void reset() {
		if (markListCursor == -1 || markArrayCursor == -1) {
			return;
		}
		reentrantLock.lock();
		getArrayCursor = markArrayCursor;
		getListCursor = markListCursor;
		markArrayCursor = -1;
		markListCursor = -1;
		reentrantLock.unlock();
	}

	private class ArrangeTask implements Runnable {

		private IOBuffer ioBuffer;

		private ArrangeTask(IOBuffer ioBuffer) {
			this.ioBuffer = ioBuffer;
		}

		@Override
		public void run() {
			ioBuffer.reentrantLock.lock();
			if (ioBuffer.getListCursor > ioBuffer.arrangeThreshold) {
				for (int i = 0; i < ioBuffer.arrangeThreshold; i++) {
					ioBuffer.data.remove(0);
				}
				ioBuffer.getListCursor -= ioBuffer.arrangeThreshold;
				ioBuffer.putListCursor -= ioBuffer.arrangeThreshold;
			}
			ioBuffer.reentrantLock.unlock();
			ioBuffer.arranged = false;
		}
	}

}
