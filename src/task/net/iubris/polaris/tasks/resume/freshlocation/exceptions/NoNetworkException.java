package net.iubris.polaris.tasks.resume.freshlocation.exceptions;

public class NoNetworkException extends Exception {

	private static final long serialVersionUID = -1131538818626748667L;

	public NoNetworkException() {
	}

	public NoNetworkException(String detailMessage) {
		super(detailMessage);
	}

	public NoNetworkException(Throwable throwable) {
		super(throwable);
	}

	public NoNetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
