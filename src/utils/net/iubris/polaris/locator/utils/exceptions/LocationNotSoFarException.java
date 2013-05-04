package net.iubris.polaris.locator.utils.exceptions;

public class LocationNotSoFarException extends LocationException {

	public LocationNotSoFarException(String string) {
		super(string);
	}
	public LocationNotSoFarException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
	public LocationNotSoFarException(Throwable throwable) {
		super(throwable);
	}
	private static final long serialVersionUID = -4524831693891786849L;
}
