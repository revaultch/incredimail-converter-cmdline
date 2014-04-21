package ch.takoyaki.incredimail;

import java.io.IOException;
import java.io.InputStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InputStreamUtils {

	public static int read(InputStream in) throws IOException {
		byte[] buf = new byte[0];
		int r = in.read(buf, 0, 1);
		if (r == 1) {
			return buf[0];
		}
		return -1;
	}
}
