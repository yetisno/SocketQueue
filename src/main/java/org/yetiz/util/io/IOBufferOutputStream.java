package org.yetiz.util.io;

import java.io.IOException;
import java.io.OutputStream;

public class IOBufferOutputStream extends OutputStream {

	private IOBuffer _IOBuffer;

	public IOBufferOutputStream(IOBuffer _IOBuffer) {
		this._IOBuffer = _IOBuffer;
	}

	@Override
	public void write(int b) throws IOException {
		_IOBuffer.put((byte) b);
	}

	@Override
	public void close() throws IOException {
		_IOBuffer = new IOBuffer();
	}

	public IOBuffer getIOBuffer() {
		return _IOBuffer;
	}
}
