package org.yetiz.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yeti on 14/11/1.
 */
public class IOBufferOutputStream extends OutputStream {

	private IOBuffer _IOBuffer;

	private IOBufferOutputStream() {
		throw new UnsupportedClassVersionError();
	}

	public IOBufferOutputStream(IOBuffer _IOBuffer) {
		this._IOBuffer = _IOBuffer;
	}

	@Override
	public void write(int b) throws IOException {
		_IOBuffer.put((byte) b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		_IOBuffer.put(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		for (int i = 0; i < len; i++) {
			write(b[off + i]);
		}
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		_IOBuffer = new IOBuffer();
	}

	public IOBuffer getIOBuffer() {
		return _IOBuffer;
	}
}
