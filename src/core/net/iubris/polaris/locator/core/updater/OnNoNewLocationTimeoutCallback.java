package net.iubris.polaris.locator.core.updater;

import android.location.Location;

public interface OnNoNewLocationTimeoutCallback {
	void onNoNewLocation(Location oldLocation);
}
