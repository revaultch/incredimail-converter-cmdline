package ch.takoyaki.incredimail;

public class ArgumentException extends IllegalArgumentException {
	private static final long serialVersionUID = -6403577677019217910L;

	public ArgumentException(String message, Object... args) {
		super(String.format(message, args));
	}
}
