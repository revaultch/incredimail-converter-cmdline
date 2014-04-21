package ch.takoyaki.incredimail;

import lombok.Value;
import lombok.experimental.Builder;

@Builder
@Value
public class MailPosition {
	private final long offset;
	private final int size;
	private final boolean deleted;
}