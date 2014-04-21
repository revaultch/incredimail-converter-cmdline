package ch.takoyaki.incredimail;

import java.io.File;

import lombok.val;

public class IncredimailConverterMain {

	private IncredimailConverter converter;

	public IncredimailConverterMain(String in, String out) {
		this.converter = new IncredimailConverter(new File(in), new File(out),
				false);
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length != 2) {
			usage();
		}
		try {
			new IncredimailConverterMain(args[0], args[1]).run();
		} catch (ArgumentException e) {
			usage(e.getMessage());
		}
	}

	private void run() throws InterruptedException {
		printMailboxes();
		long start;
		long stop;
		try (val watcher = new Watcher<Integer>(this::logTotal, 0)) {
			start = System.currentTimeMillis();
			converter.convert();
			stop = System.currentTimeMillis();
		}
		logTotal();
		System.out.println("\nruntime " + (stop - start) + "ms");

	}

	private void printMailboxes() {
		System.out.println("Mailbox name          # messages");
		System.out.println("------------          ----------");
		converter
				.getMailBoxes()
				.stream()
				.forEach(
						(m) -> {
							System.out.println(String.format("%-20s    %8s",
									m.getLabel(), m.getMessageCount()));
						});
		System.out.println("\n");
	}

	private int logTotal() {
		return logTotal(null);
	}

	private int logTotal(Integer previous) {
		int count = converter.getMailBoxes().stream()
				.mapToInt(IncredimailMailbox::getNumExtracted).sum();

		String diff = "";
		if (previous != null) {
			diff = String.format("(%5s)", (count - previous));
		}
		System.out.println(String.format("Total %5s %s", count, diff));
		return count;
	}

	private static void usage() {
		usage("");
	}

	private static void usage(String message, Object... args) {
		System.err.println(String.format(message, args));
		System.out
				.println("incredimail-converter <incredimail dir> <empty output dir>");
		System.exit(-1);
	}
}
