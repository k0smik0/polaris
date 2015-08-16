package net.iubris.polaris.locator.utils;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.location.Location;
import android.os.Handler;

//@Singleton
/**
 * you should use this as a singleton
 */
public class LocationProviderEnabler {

	private final String message;
	private final String no;
	private final String yes;
	private final Activity activity;
	
	private boolean showed;

//	@Inject
	public LocationProviderEnabler(Activity activity, String providerSettingsMessage, String providerSettingsYes, String providerSettingsNo) {
		this.activity = activity;
		this.message = providerSettingsMessage;
		this.yes = providerSettingsYes;
		this.no = providerSettingsNo;
	}
	
//	@UiThread
	public void checkLocationAndEventuallyAskForProviderEnabling(Location location, final CountDownLatch latch, final Activity activity) {
		if (location==null ) {
			if (LocationUtils.isGpsProviderEnabled(activity) || LocationUtils.isNetworkProviderEnabled(activity))
				return;
			
			new Handler().post( new Runnable() {
				@Override
				public void run() {
					if (!showed) {
						LocationUtils.askEnableProviders(LocationProviderEnabler.this.activity, message, yes, no, latch);
						showed = true;
					}
				}
			});
		}
	}

	public void reset() {
		showed = false;
	}
}	
