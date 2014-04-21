package ch.takoyaki.incredimail;

import java.io.File;
import java.util.List;

import lombok.NonNull;

public class IncredimailConverter {

	private final IncredimailContainer container;
	private final File outputDir;

	public IncredimailConverter(@NonNull File incredimailDir,
			@NonNull File outputDir, boolean includeDeleted) {
		this.outputDir = checkOutputDir(outputDir);
		this.container = new IncredimailContainer(incredimailDir,
				includeDeleted);
	}

	public List<IncredimailMailbox> getMailBoxes() {
		return this.container.getMailBoxes();
	}

	public void convert() {
		getMailBoxes().parallelStream().forEach((a) -> {
			a.convert(outputDir);
		});
	}

	private static File checkOutputDir(File f) {

		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new ArgumentException("Couldn't create directory %s", f);
			}
		}
		if (!f.isDirectory()) {
			throw new ArgumentException("%s is not a directory", f);
		}
		if (f.list().length != 0) {
			throw new ArgumentException("%s is not an empty directory", f);
		}
		return f;
	}
}
