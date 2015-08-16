package net.iubris.polaris.tasks.resume.freshlocation;



import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

import net.iubris.etask.EnhancedSafeAsyncTaskContexted;
import net.iubris.polaris.locator.core.LocatorUtils;
import net.iubris.polaris.locator.core.exceptions.LocationException;
import net.iubris.polaris.locator.core.provider.LocationProvider;
import net.iubris.polaris.locator.utils.LocationProviderEnabler;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

/**
 * Put this on MainActivity (or Container Activity if use Fragment, and execute on onResume.<br/><br/>
 * 
 * Technically: it uses Diane's {@link CheckerStateNetworkAware} and Polaris's {@link LocationProvider} to retrieve awareness fresh location<br/>
 *  
 */
public class GetFreshLocationTask extends EnhancedSafeAsyncTaskContexted<Boolean> {
	
	private static final long TIMEOUT = 4;
//	private final LocationManager locationManager;
	private final LocationProvider locationProvider;
//	private final String locationNullEnableGPS;
	private final String locationNullAllWrong;
	private final LocationProviderEnabler locationProviderEnabler;
//	private final ConnectivityManager connectivityManager;
	
	private boolean isInForeground;
	private OnNewLocationCallback onNewLocationCallback;
	private CountDownLatch latch;
	
	
	public GetFreshLocationTask(Activity activity,
//			LocationManager locationManager,
//			ConnectivityManager connectivityManager, 
			LocationProvider locationProvider,
			String locationNullAllWrong, 
//			String locationNullEnableGPS
			LocationProviderEnabler locationProviderEnabler
			) {
		super(activity);
//		this.locationManager = locationManager;
//		this.connectivityManager = connectivityManager;
		this.locationProvider = locationProvider;
		this.locationNullAllWrong = locationNullAllWrong;
//		this.locationNullEnableGPS = locationNullEnableGPS;
		this.locationProviderEnabler = locationProviderEnabler;
	}
	
	public GetFreshLocationTask(Activity activity, 
//			LocationManager locationManager, /*ConnectivityManager connectivityManager,*/ 
			LocationProvider locationProvider,
			String locationNullAllWrong, 
//			String locationNullEnableGPS, 
			OnNewLocationCallback onNewLocationCallback, LocationProviderEnabler locationProviderEnabler) {
		this(activity, /*locationManager, connectivityManager,*/ locationProvider
				, locationNullAllWrong 
//				locationNullEnableGPS*/
				, locationProviderEnabler);
		this.onNewLocationCallback = onNewLocationCallback;
	}
	
	
	public void execute(OnNewLocationCallback onNewLocationCallback) {
		this.onNewLocationCallback = onNewLocationCallback;
		super.execute();
	}
	
	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();
		int sleepCount = 0;
		while (isNullLocation() && sleepCount<3) {
			try { Thread.sleep(700); } catch (InterruptedException e) {}
			sleepCount++;
		}
		if (isNullLocation()) {
			latch = new CountDownLatch(1);
			locationProviderEnabler.checkLocationAndEventuallyAskForProviderEnabling(getLocation(), latch, (Activity)context);
		}
	}
	
	@Override
	public Boolean call() /*throws NoNetworkException, LocationException*/ {
		/*int sleepCount = 0;
		while (isNullLocation() && sleepCount<3) {
			try { Thread.sleep(500); } catch (InterruptedException e) {}
			sleepCount++;
		}*/
		
		try {
			if (latch!=null){
//				Log.d("GFLT","waiting");
				latch.await(TIMEOUT, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (isNullLocation()) { // something wrong...
			/*// if not, throw NoNetworkException
			if (NetworkUtils.isInternetOn(connectivityManager))
				throw new NoNetworkException();
	
			// ok, there is connectivity, go on
			if (isGPSEnabled()) {
				// network = 1, gps =1, but all is wrong
				throw new LocationException(locationNullAllWrong);
			}
			// network = 1, gps = 0*/			
			return true;
		}
		// location is not null
		return false;
	}
	protected void onSuccess(Boolean locationIsNull) throws Exception {
//		Log.d("GTLT:119", ""+getLocation());
		Location location = null;
//		if (locationIsNull) {
			// try again
		location = getLocation();
//		Log.d("GTLT:124", ""+location);
//		}
		if (location==null) {
			// simply report apocalypse now
			Toast.makeText(context, locationNullAllWrong, Toast.LENGTH_LONG).show();
		} else {
//			Log.d("GetFreshLocationTask:90",""+location);
			if (onNewLocationCallback!=null) {
				onNewLocationCallback.onNewLocation(location);
			}
			// not null, so re-send: receiver in main activity will grab
			context.sendBroadcast( new Intent().setAction( LocatorUtils.getActionLocationUpdatedString(context) ));
		}
	}
	/*protected void onException(NoNetworkException e) throws RuntimeException {
		// nor network, nor gps
		if (!isGPSEnabled()) {
			// enable gps
			if (isInForeground) {
				Toast.makeText(context, locationNullEnableGPS, Toast.LENGTH_LONG).show();
			}
		}
	}*/
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

	/*private boolean isGPSEnabled() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			return true;
		return false;
	}*/
	
	/*public void setApplicationInForeground(boolean isInForeground) {
		this.isInForeground = isInForeground;
	}*/
	
	public static interface OnNewLocationCallback {
		void onNewLocation(Location location);
	} 
}
