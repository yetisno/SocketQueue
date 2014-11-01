package org.yetiz.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yeti on 14/11/1.
 */
public class IOBufferInputStream extends InputStream {

	private IOBuffer iOBuffer;

	private IOBufferInputStream() {
		throw new UnsupportedClassVersionError();
	}

	public IOBufferInputStream(IOBuffer iOBuffer) {
		this.iOBuffer = iOBuffer;
	}

	@Override
	public int read() throws IOException {
		return iOBuffer.get();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int c = read();
		if (c == -1) {
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				if (c == -1) {
					break;
				}
				b[off + i] = (byte) c;
			}
		} catch (IOException ee) {
		}
		return i;
	}

	@Override
	public int available() throws IOException {
		return (int) iOBuffer.remaining();
	}

	@Override
	public long skip(long n) throws IOException {
		long length = 0;
		if (iOBuffer.remaining() < n) {
			length = iOBuffer.remaining();
			for (long i = 0; i < length; i++) {
				iOBuffer.get();
			}
		} else {
			length = n;
			for (long i = 0; i < length; i++) {
				iOBuffer.get();
			}
		}
		return length;
	}

	/**
	 * close stream and reset buffer.
	 */
	@Override
	public void close() throws IOException {
		iOBuffer = new IOBuffer();
	}

	/**
	 * mark current read position for reset.
	 *
	 * @param readlimit this param is no used.
	 */
	@Override
	public synchronized void mark(int readlimit) {
		iOBuffer.mark();
	}

	/**
	 * check whether support mark function.
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
