package ch.takoyaki.incredimail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.google.common.io.ByteStreams;

@Value
@Slf4j
public class IncredimailEml {
	private static final String ATTACHMENT_DIR = "Attachments";
	private final File immFile;
	private final File emlFile;
	private final MailPosition pos;

	public void convert() {
		try (val inStream = filteredStream();
				val outStream = new FileOutputStream(emlFile)) {

			ByteStreams.copy(inStream, outStream);

		} catch (IOException e) {
			log.error(String.format(
					"error while copying file %s to %s pos=%s ", immFile,
					emlFile, pos), e);
		}

	}

	private InputStream filteredStream() throws IOException {
		SubInputStream emlStream = new SubInputStream(new FileInputStream(
				immFile), pos.getOffset(), pos.getSize());

		ComposedInputStream composed = new ComposedInputStream();
		AttachmentInliningStream.register(emlFile.getAbsolutePath(),
				getAttachmentDir(), emlStream, composed.getStreams());
		return composed;
	}

	private File getAttachmentDir() {
		return new File(immFile.getParentFile(), ATTACHMENT_DIR);
	}
}
