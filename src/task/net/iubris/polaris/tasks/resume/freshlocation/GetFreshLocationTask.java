package net.iubris.polaris.tasks.resume.freshlocation;



import net.iubris.etask.EnhancedSafeAsyncTaskContexted;
import net.iubris.polaris.locator.core.LocatorUtils;
import net.iubris.polaris.locator.core.exceptions.LocationException;
import net.iubris.polaris.locator.core.provider.LocationProvider;
import net.iubris.polaris.tasks.resume.freshlocation.exceptions.NoNetworkException;
import net.iubris.polaris.tasks.resume.freshlocation.utils.NetworkUtils;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Put this on MainActivity (or Container Activity if use Fragment, and execute on onResume.<br/><br/>
 * 
 * Technically: it uses Diane's {@link CheckerStateNetworkAware} and Polaris's {@link LocationProvider} to retrieve awareness fresh location<br/>
 *  
 */
public class GetFreshLocationTask extends EnhancedSafeAsyncTaskContexted<Boolean> {
	
	private final LocationManager locationManager;
	private final LocationProvider locationProvider;
	private final String locationNullEnableGPS;
	private final String locationNullAllWrong;
	private final ConnectivityManager connectivityManager;
	
	private boolean isInForeground;
	private OnNewLocationCallback onNewLocationCallback;
	
	public GetFreshLocationTask(Activity context, 
			LocationManager locationManager, ConnectivityManager connectivityManager, LocationProvider locationProvider, 
			String locationNullAllWrong, String locationNullEnableGPS) {
		super(context);
		this.locationManager = locationManager;
		this.connectivityManager = connectivityManager;
		this.locationProvider = locationProvider;
		this.locationNullAllWrong = locationNullAllWrong;
		this.locationNullEnableGPS = locationNullEnableGPS;
	}
	
	public GetFreshLocationTask(Activity context, LocationManager locationManager, ConnectivityManager connectivityManager, LocationProvider locationProvider, String locationNullAllWrong, String locationNullEnableGPS, OnNewLocationCallback onNewLocationCallback) {
		this(context, locationManager, connectivityManager, locationProvider, locationNullAllWrong, locationNullEnableGPS);
		this.onNewLocationCallback = onNewLocationCallback;
	}
	
	
	public void execute(OnNewLocationCallback onNewLocationCallback) {
		this.onNewLocationCallback = onNewLocationCallback;
		super.execute();
	}
	
	@Override
	public Boolean call() throws NoNetworkException, LocationException {
		
		int sleepCount = 0;
		while (isNullLocation() && sleepCount<3) {
			try { Thread.sleep(500); } catch (InterruptedException e) {}
			sleepCount++;
		}
		
		if (isNullLocation()) { // something wrong...
			// if not, throw NoNetworkException
			if (NetworkUtils.isInternetOn(connectivityManager))
				throw new NoNetworkException();
	
			// ok, there is connectivity, go on			
			if (isGPSEnabled()) {
				// network = 1, gps =1, but all is wrong
				throw new LocationException(locationNullAllWrong);
			}
			// network = 1, gps = 0
			return true;
		}	
		// location is not null
		return false;
	}
	protected void onSuccess(Boolean locationIsNull) throws Exception {
		if (locationIsNull) {
			// try again
			Location location = getLocation();
			if (location==null) {
				// simply invite to enable gps
				Toast.makeText(context, locationNullEnableGPS, Toast.LENGTH_LONG).show();
			} else {
				if (onNewLocationCallback!=null) {
					onNewLocationCallback.onNewLocation(location);
				}
				// not null, so re-send: receiver in main activity will grab
				context.sendBroadcast( new Intent().setAction(
						LocatorUtils.getActionLocationUpdatedString(context)
						) );
			}
		}
		
	}
	protected void onException(NoNetworkException e) throws RuntimeException {
		// nor network, nor gps
		if (!isGPSEnabled()) {
			// enable gps
			if (isInForeground) {
				Toast.makeText(context, locationNullEnableGPS, Toast.LENGTH_LONG).show();
			}
		}
	}
	protected void onException(LocationException arg0) throws RuntimeException {
		// all is wrong.
		// enable gps and force via google maps
		if (isInForeground) {
			Toast.makeText(context, locationNullAllWrong, Toast.LENGTH_LONG).show();
		}
	};
	
	private boolean isNullLocation() {
		return (getLocation()==null);
	}
	private Location getLocation() {
		return locationProvider.getLocation();
	}

	private boolean isGPSEnabled() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			return true;
		return false;
	}
	
	public void setApplicationInForeground(boolean isInForeground) {
		this.isInForeground = isInForeground;
	}
	
	public static interface OnNewLocationCallback {
		void onNewLocation(Location location);
	} 
}
