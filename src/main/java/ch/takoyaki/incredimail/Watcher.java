package ch.takoyaki.incredimail;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.xml.ws.WebServiceException;

class Watcher<T> extends Thread implements Closeable {

	private final AtomicBoolean keepWatching = new AtomicBoolean(true);
	private final Function<T, T> within;
	private T previous;

	public Watcher(Function<T, T> within, T previous) {
		this.within = within;
		this.previous = previous;
		this.start();
	}

	@Override
	public void run() {
		do {
			previous = within.apply(previous);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		} while (keepWatching.get());
	}

	@Override
	public void close() throws WebServiceException {
		keepWatching.set(false);
		try {
			this.join();
		} catch (InterruptedException e) {
		}
	}
}