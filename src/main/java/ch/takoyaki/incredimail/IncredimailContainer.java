package ch.takoyaki.incredimail;

import java.io.File;
import java.util.List;

import lombok.Getter;
import ch.takoyaki.incredimail.IncredimailMailbox.IncredimailMailboxBuilder;

public class IncredimailContainer {

	public static final String DB_NAME = "Containers.db";
	private final File containerFile;
	private final boolean includeDeleted;
	
	@Getter(lazy = true)
	private final List<IncredimailMailbox> mailBoxes = getMailBoxesFromDb();


	public IncredimailContainer(File incredimailDir, boolean includeDeleted) {
		this.containerFile = new File(checkIncredimailDir(incredimailDir),
				DB_NAME);
		this.includeDeleted = includeDeleted;
	}

	private List<IncredimailMailbox> getMailBoxesFromDb() {
		return SqliteWrapper
				.query(containerFile,
						"SELECT * FROM Containers;",
						(rs) -> {
							IncredimailMailboxBuilder builder = new IncredimailMailboxBuilder();
							builder.containerFile(containerFile);
							builder.containerID(rs.getString("ContainerID"));
							builder.parentContainerID(rs
									.getString("ParentContainerID"));
							builder.indexInParent(rs.getInt("IndexInParent"));
							builder.label(rs.getString("Label"));
							builder.fileName(rs.getString("FileName"));
							builder.messageCount(rs.getInt("MsgsCount"));
							builder.includeDeleted(includeDeleted);
							return builder.build();
						});
	}

	private File checkIncredimailDir(File incredimailDir) {
		if (!incredimailDir.isDirectory()
				|| getContainerFile(incredimailDir) == null) {
			throw new IllegalArgumentException(String.format(
					"%s is not an incredimail message store", incredimailDir));
		}
		return incredimailDir;
	}

	private File getContainerFile(File incredimailDir) {
		File[] files = incredimailDir
				.listFiles((dir, name) -> IncredimailContainer.DB_NAME
						.equals(name));
		if (files.length == 0) {
			return null;
		}
		return files[0];
	}
}
