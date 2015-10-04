package net.iubris.polaris.locator.core.exceptions;

public class LocationNullException extends LocationException {

	private static final long serialVersionUID = -457696751544075661L;

	public LocationNullException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public LocationNullException(String detailMessage) {
		super(detailMessage);
	}

	public LocationNullException(Throwable throwable) {
		super(throwable);
	}
	
	public LocationNullException() {
		super();
	}
}
