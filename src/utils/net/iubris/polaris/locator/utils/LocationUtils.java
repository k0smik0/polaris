package net.iubris.polaris.locator.utils;

import android.location.LocationManager;

public class LocationUtils {

	public static boolean isHighAccuracy(LocationManager locationManager, String provider, float accuracy, int accuracyMaxError) {
		final String gpsProvider = LocationManager.GPS_PROVIDER;
		final boolean isLowAccuracy = accuracy > accuracyMaxError;
		final boolean isProviderGPS = provider.equals(gpsProvider);
		final boolean isGPSEnabled = locationManager.isProviderEnabled(gpsProvider);
		if ( isLowAccuracy && !isProviderGPS && !isGPSEnabled)
			return true;
		else
			return false;
	}
}
