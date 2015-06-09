package net.iubris.polaris.tasks.resume.freshlocation;



import net.iubris.etask.EnhancedSafeAsyncTaskContexted;
import net.iubris.polaris.locator.LocatorUtils;
import net.iubris.polaris.locator.exceptions.LocationException;
import net.iubris.polaris.locator.provider.LocationProvider;
import net.iubris.polaris.tasks.resume.freshlocation.exceptions.NoNetworkException;
import net.iubris.polaris.tasks.resume.freshlocation.utils.NetworkUtils;
import android.app.Activity;
import android.content.Intent;
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
	
	private boolean isInForeground;
	private final ConnectivityManager connectivityManager;
	
	public GetFreshLocationTask(Activity context, LocationManager locationManager, ConnectivityManager connectivityManager, LocationProvider locationProvider, String locationNullAllWrong, String locationNullEnableGPS) {
		super(context);
		this.locationManager = locationManager;
		this.connectivityManager = connectivityManager;
		this.locationProvider = locationProvider;
		this.locationNullAllWrong = locationNullAllWrong;
		this.locationNullEnableGPS = locationNullEnableGPS;
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
				throw new LocationException("");
			}
			// network = 1, gps = 0
			return true;
		}	
		// location is not null
		return false;
	}
	protected void onSuccess(Boolean locationIsNull) throws Exception {
		if (locationIsNull) {
			// TODO simply invite to enable gps
			Toast.makeText(context, "You could enable GPS", Toast.LENGTH_LONG).show();
		} 
		else {
			// not null, so re-send: receiver in main activity will grab
			context.sendBroadcast( new Intent().setAction(
					LocatorUtils.getActionLocationUpdatedString(context)
					) );
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
		return (locationProvider.getLocation()==null);
	}

	private boolean isGPSEnabled() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			return true;
		return false;
	}
	
	public void setApplicationInForeground(boolean isInForeground) {
		this.isInForeground = isInForeground;
	}
}
