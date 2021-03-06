package net.iubris.polaris.locator.core.updater;

import java.util.Locale;

import net.iubris.polaris.locator.utils.LocationStrategiesUtils;
import net.iubris.polaris.locator.utils.exceptions.LocationNotSoFarException;
import android.location.Location;
import android.util.Log;

public class DefaultOnLocationUpdatedCallback implements OnLocationUpdatedCallback {
	
	private static final String TAG = DefaultOnLocationUpdatedCallback.class.getPackage().getName().toUpperCase(Locale.getDefault())+"/"+DefaultOnLocationUpdatedCallback.class.getSimpleName();

	public static final int TIME_MAXIMUM_THRESHOLD_IN_SECONDS = 5;
	public static final int ACCURACY_DISTANCE_MAXIMUM_THRESHOLD_IN_METERS = 10;
	public static final float DISTANCE_MAXIMUM_FARENESS_IN_METERS = 20;
	
	private Location location;
	private boolean wasNull = false;

	@Override
	public final void onLocationUpdated(Location newLocation) {
		final String methodName = "onLocationUpdated";
		// first time
		wasNull = false;
		if (location==null) {
			location = newLocation;
			wasNull  = true;
		}			
		
		if (LocationStrategiesUtils.isLocationBetter(newLocation, location, TIME_MAXIMUM_THRESHOLD_IN_SECONDS, ACCURACY_DISTANCE_MAXIMUM_THRESHOLD_IN_METERS)) {
			if (wasNull) {
				Log.d(TAG+":"+methodName,"location was null\n");
				doSomethingWithNewLocation(location);
				return;
			}
				
			try {
				if (LocationStrategiesUtils.isLocationFar(newLocation, location, DISTANCE_MAXIMUM_FARENESS_IN_METERS)) {
					location = newLocation;
					Log.d(TAG+":"+methodName," location was far\n");
					doSomethingWithNewLocation(location);
				}
			} catch (LocationNotSoFarException e) {}
		} else {
			Log.d(TAG+":"+methodName,"waiting for a better location");
		}
	}

	protected void doSomethingWithNewLocation(Location location) {
		Log.d(TAG+":"+"onLocationUpdated:doSomethingWithNewLocation", "just printing location: "+location+"\n");
	}
}
