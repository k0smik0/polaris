/**
 * Copyleft 2015 Massimiliano Leone, LGPLv3 license
 * 
 */
package net.iubris.polaris.tasks.freshlocation;



import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.iubris.etask.EnhancedSafeAsyncTaskContexted;
import net.iubris.polaris.locator.core.LocatorUtils;
import net.iubris.polaris.locator.core.exceptions.LocationException;
import net.iubris.polaris.locator.core.exceptions.LocationNullException;
import net.iubris.polaris.locator.core.provider.LocationProvider;
import net.iubris.polaris.locator.core.updater.OnLocationUpdatedCallback;
import net.iubris.polaris.locator.utils.LocationProviderEnabler;
import net.iubris.polaris.locator.utils.LocationStrategiesUtils;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

/**
 * Put this on MainActivity (or Container Activity if use Fragment, and execute on onResume.<br/><br/>
 * 
 * Technically: it uses Diane's {@link CheckerStateNetworkAware} and Polaris's {@link LocationProvider} 
 * to retrieve awareness fresh location<br/>
 */
public class GetFreshLocationTask extends EnhancedSafeAsyncTaskContexted<Boolean> {
	
	private static final long LATCH_TIMEOUT = 10;
	private final LocationProvider locationProvider;
	private final String locationNullAllWrong;
	private final LocationProviderEnabler locationProviderEnabler;
	
	private boolean isInForeground;
	private OnLocationUpdatedCallback onLocationUpdatedCallback;
	private CountDownLatch latch;
	private Location location;
	private final static int TIME_MINIMUM_THRESHOLD_IN_SECONDS = 30;
	
	
	public GetFreshLocationTask(Activity activity,
			LocationProvider locationProvider,
			String locationNullAllWrong, 
			LocationProviderEnabler locationProviderEnabler) {
		super(activity);
		this.locationProvider = locationProvider;
		this.locationNullAllWrong = locationNullAllWrong;
		this.locationProviderEnabler = locationProviderEnabler;
	}
	
	public GetFreshLocationTask(Activity activity, 
			LocationProvider locationProvider,
			String locationNullAllWrong, 
			OnLocationUpdatedCallback onNewLocationCallback, LocationProviderEnabler locationProviderEnabler) {
		this(activity, locationProvider
				, locationNullAllWrong 
				, locationProviderEnabler);
		this.onLocationUpdatedCallback = onNewLocationCallback;
	}
	
	
	public void execute(OnLocationUpdatedCallback onLocationUpdatedCallback) {
		this.onLocationUpdatedCallback = onLocationUpdatedCallback;
		super.execute();
	}
	
	@Override
	protected void onPreExecute() {
		try {
			location = getLocation();
		} catch (LocationNullException e) {
			if (isNullLocation(location)) {
				latch = new CountDownLatch(1);
				Log.d("GetFreshLocationTask","checking location or ask for provider");
				locationProviderEnabler.checkLocationAndEventuallyAskForProviderEnabling(location, latch, (Activity)context);
			}
		}
	}
	
	@Override
	public Boolean call() /*throws NoNetworkException, LocationException*/ {
		
		try {
			if (latch!=null){
//				Log.d("GFLT","waiting");
				latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (isNullLocation(location)) {
			Log.d("GetFreshLocationTask","checking isNullLocation inside call");
			// try again
			try {
				location = getLocation();
			} catch (LocationNullException e) {
				if (isNullLocation(location)) { // something wrong...
					return true;
				}
			}
		}
		
		Log.d("GetFreshLocationTask","checking (new) getLocation() inside call, since isNullLocation is false");
		// try for fresh location		
		int sleepCount = 0;
		while (LocationStrategiesUtils.isLocationOld(location, TIME_MINIMUM_THRESHOLD_IN_SECONDS) && sleepCount<5) {
			Log.d("GetFreshLocationTask", "sleeping waiting for not-old location: "+sleepCount+" "+location);
			try { Thread.sleep(500); } catch (InterruptedException e) {}
			try {
				location = getLocation();
			} catch (LocationNullException e) {}
			sleepCount++;
		}
		
		// location is not null
		return false;
	}

	protected void onSuccess(Boolean locationIsNull) throws Exception {
		Log.d("GetFreshLocationTask","new Location inside onSuccess: "+location);
//		Log.d("GTLT:124", ""+location);
//		}
		if (isNullLocation(location)) {
			// simply report apocalypse now
			Toast.makeText(context, locationNullAllWrong, Toast.LENGTH_LONG).show();
		} else {
//			Log.d("GetFreshLocationTask:90",""+location);
			if (onLocationUpdatedCallback!=null) {
				onLocationUpdatedCallback.onLocationUpdated(location);
			}
			// not null, so re-send: receiver in main activity will grab
			context.sendBroadcast( new Intent().setAction( LocatorUtils.getActionLocationUpdatedString(context) ));
		}
	}

	protected void onException(LocationException arg0) throws RuntimeException {
		// all is wrong.
		// enable gps and force via google maps
		if (isInForeground) {
			Toast.makeText(context, locationNullAllWrong, Toast.LENGTH_LONG).show();
		}
	};
	
	private boolean isNullLocation(Location location) {
		Log.d("GetFreshLocationTask","inside isNullLocation ");
//		Location location = getLocation();
		boolean isNull = location==null;
		Log.d("GetFreshLocationTask","(inside) isNullLocation? "+isNull);
		return isNull;
	}
	private Location getLocation() throws LocationNullException {
		Location location = locationProvider.getLocation();
		Log.d("GetFreshLocationTask","_____ getLocation: location = "+location);
		return location;
	}
	 
}
