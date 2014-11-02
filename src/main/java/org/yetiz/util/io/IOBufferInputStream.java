package org.yetiz.util.io;

import java.io.IOException;
import java.io.InputStream;

public class IOBufferInputStream extends InputStream {

	private IOBuffer iOBuffer;

	public IOBufferInputStream(IOBuffer iOBuffer) {
		this.iOBuffer = iOBuffer;
	}

	@Override
	public int read() throws IOException {
		return iOBuffer.get();
	}

	@Override
	public int available() throws IOException {
		return (int) iOBuffer.remaining();
	}

	/**
	 * close stream and reset buffer.
	 */
	@Override
	public void close() throws IOException {
		iOBuffer = new IOBuffer();
	}

	/**
	 * mark current read position for <code>reset</code>.
	 *
	 * @param readlimit this param is no used.
	 */
	@Override
	public synchronized void mark(int readlimit) {
		iOBuffer.mark();
	}

	/**
	 * check whether support <code>mark</code> function.
	 *
	 * @return always return true.
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 * reset read position to mark position and clear mark position.
	 */
	@Override
	public synchronized void reset() {
		iOBuffer.reset();
	}

	/**
	 * get the buffer of current stream.
	 *
	 * @return the shared buffer of this stream.
	 */
	public IOBuffer getIOBuffer() {
		return iOBuffer;
	}
}
