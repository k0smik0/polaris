package net.iubris.polaris.locator.utils.exceptions;

public class LocationException extends Exception {

	private static final long serialVersionUID = -3643620849629479936L;

	public LocationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
	public LocationException(String detailMessage) {
		super(detailMessage);
	}
	public LocationException(Throwable throwable) {
		super(throwable);
	}
}
