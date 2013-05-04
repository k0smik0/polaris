package net.iubris.polaris.locator.utils.exceptions;

public class LocationNotSoNewerException extends LocationException {

	public LocationNotSoNewerException(String string) {
		super(string);
	}
	public LocationNotSoNewerException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
	public LocationNotSoNewerException(Throwable throwable) {
		super(throwable);
	}
	private static final long serialVersionUID = -6216901852101200245L;
}
