package org.yetiz.util.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yeti on 14/11/1.
 */
public class IOBufferIOStream {
	private IOBuffer _IOBuffer;
	private IOBufferInputStream IOBufferInputStream;
	private IOBufferOutputStream IOBufferOutputStream;

	/**
	 * get a pair of input and output stream.
	 */
	public IOBufferIOStream() {
		this._IOBuffer = new IOBuffer();
		IOBufferInputStream = new IOBufferInputStream(_IOBuffer);
		IOBufferOutputStream = new IOBufferOutputStream(_IOBuffer);
	}

	/**
	 * get a pair of input and output stream with shared IOBuffer.
	 *
	 * @param _IOBuffer shared buffer between input and output stream.
	 */
	public IOBufferIOStream(IOBuffer _IOBuffer) {
		this._IOBuffer = _IOBuffer;
		IOBufferInputStream = new IOBufferInputStream(_IOBuffer);
		IOBufferOutputStream = new IOBufferOutputStream(_IOBuffer);
	}

	public InputStream getInputStream() {
		return IOBufferInputStream;
	}

	public OutputStream getOutputStream() {
		return IOBufferOutputStream;
	}
}
