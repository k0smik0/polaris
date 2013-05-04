package net.iubris.polaris.locator.utils.exceptions;

public class LocationNotSoCarefulException extends LocationException {

	public LocationNotSoCarefulException(String string) {
		super(string);
	}
	public LocationNotSoCarefulException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}
	public LocationNotSoCarefulException(Throwable throwable) {
		super(throwable);
	}
	private static final long serialVersionUID = 4657190508508306617L;
}
