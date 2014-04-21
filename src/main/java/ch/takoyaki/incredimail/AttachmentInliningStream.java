package ch.takoyaki.incredimail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64InputStream;

import com.google.common.base.Charsets;

@Slf4j
public class AttachmentInliningStream extends InputStream {

	protected static final String ATTACHMENT_MARKER = "----------[%ImFilePath%]----------";

	private final BufferedReader reader;
	private final StringBuilder buffer = new StringBuilder();

	private final List<InputStream> streams;

	private final File attachmentDir;

	private final String emlFileName;

	private AttachmentInliningStream(String emlFileName, File attachmentDir,
			InputStream emlStream, List<InputStream> streams) {
		this.emlFileName = emlFileName;
		this.attachmentDir = attachmentDir;
		this.reader = new BufferedReader(new InputStreamReader(emlStream,
				Charsets.ISO_8859_1));
		this.streams = streams;
	}

	public static void register(String emlFileName, File attachmentDir,
			SubInputStream emlStream, List<InputStream> streams) {
		streams.add(new AttachmentInliningStream(emlFileName, attachmentDir,
				emlStream, streams));
	}

	@Override
	public int read() throws IOException {
		return InputStreamUtils.read(this);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {

		if (buffer.length() == 0) {
			String l = reader.readLine();
			if (l == null) {
				return -1;
			}
			if (l.startsWith(ATTACHMENT_MARKER)) {
				addAttachmentStream(l);
				return 0;
			} else {
				buffer.append(l + "\r\n");
			}
		}

		int toRead = Math.min(buffer.length(), len);
		String s = buffer.substring(0, toRead);
		buffer.delete(0, toRead);

		System.arraycopy(s.getBytes(Charsets.ISO_8859_1), 0, b, off, toRead);
		return toRead;
	}

	private void addAttachmentStream(String attachmentLine) throws IOException {
		String file = attachmentLine.substring(ATTACHMENT_MARKER.length());
		file = file.replaceAll("\\\\", File.separator);
		file = file.replaceAll("[.]$", "");
		File attachment = new File(attachmentDir, file);
		if (attachment.exists() && attachment.isFile()) {
			streams.add(0,
					new Base64InputStream(new FileInputStream(attachment),
							true, 72, "\r\n".getBytes()));
		} else {
			log.info(String.format("Couldn't find attachment %s %s",
					attachment, emlFileName));
		}

	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

}
