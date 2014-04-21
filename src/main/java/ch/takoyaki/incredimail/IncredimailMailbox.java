package ch.takoyaki.incredimail;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.ToString;
import lombok.val;
import lombok.experimental.Builder;
import ch.takoyaki.incredimail.MailPosition.MailPositionBuilder;

@Builder
@ToString
public class IncredimailMailbox {
	private final File containerFile;
	private final String containerID;
	private final String parentContainerID;
	private final Integer indexInParent;
	@Getter
	private final String label;
	private final String fileName;
	@Getter
	private final int messageCount;
	private final boolean includeDeleted;

	private final AtomicInteger numExtracted = new AtomicInteger(0);

	public int getNumExtracted() {
		return numExtracted.get();
	}

	public void convert(final File outputDir) {
		if (messageCount <= 0) {
			return;
		}
		val positions = getMailPositions();
		val immFile = getImmFile();

		positions.parallelStream().forEach(
				(pos) -> {
					new IncredimailEml(immFile, createEmlFile(outputDir, pos),
							pos).convert();
					numExtracted.incrementAndGet();
				});
	}

	private File getImmFile() {
		return new File(containerFile.getParentFile(), fileName + ".imm");
	}

	private File createEmlFile(File outputDir, MailPosition pos) {
		File outdir = new File(outputDir, fileName);
		outdir.mkdirs();
		return new File(outdir, pos.getOffset() + "-" + pos.getSize() + ".eml");
	}

	private List<MailPosition> getMailPositions() {
		String query = String
				.format("SELECT MsgPos,LightMsgSize,Deleted FROM Headers WHERE containerID='%s' ORDER BY MsgPos ASC",
						containerID);
		return SqliteWrapper.query(containerFile, query, (rs) -> {
			MailPositionBuilder builder = new MailPositionBuilder();
			builder.offset(rs.getLong("MsgPos"));
			builder.size(rs.getInt("LightMsgSize"));
			builder.deleted(rs.getBoolean("Deleted"));
			MailPosition v = builder.build();
			if (!includeDeleted && v.isDeleted()) {
				return null;
			}
			return v;
		});
	}
}
