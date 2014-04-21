package ch.takoyaki.incredimail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ComposedInputStream extends InputStream {
	@Getter
	private final List<InputStream> streams = new ArrayList<InputStream>();

	ComposedInputStream(InputStream... streams) {
		Arrays.stream(streams).forEach((e) -> this.streams.add(e));
	}

	@Override
	public int read() throws IOException {
		return InputStreamUtils.read(this);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		while (!streams.isEmpty()) {
			InputStream first = streams.get(0);
			int r = first.read(b, off, len);
			if (r != -1) {
				return r;
			}
			streams.remove(0);
		}
		return -1;
	}

	@Override
	public void close() throws IOException {
		streams.forEach((i) -> {
			try {
				i.close();
			} catch (Exception e) {
			}
		});
		;
	}
}
