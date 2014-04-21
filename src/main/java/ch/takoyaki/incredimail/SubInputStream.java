package ch.takoyaki.incredimail;

import java.io.IOException;
import java.io.InputStream;

public class SubInputStream extends InputStream {

	private final InputStream stream;

	private int remaining;

	public SubInputStream(InputStream stream, long offset, int size)
			throws IOException {
		this.stream = stream;
		forward(stream, offset);
		this.remaining = size;

	}

	private void forward(InputStream stream, long offset) throws IOException {
		long skipped = 0;
		while (skipped < offset) {
			skipped += stream.skip(offset - skipped);
		}
	}

	@Override
	public int read() throws IOException {
		if (remaining > 0) {
			remaining--;
			return stream.read();
		}
		return -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (remaining <= 0) {
			return -1;
		}
		int r = stream.read(b, off, Math.min(len, remaining));
		remaining -= r;
		return r;
	}

}
