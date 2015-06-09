package net.iubris.polaris._di.tasks.resume.freshlocation.providers;

import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.LocationNullAllWrongString;
import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.LocationNullEnableGPSString;
import net.iubris.polaris.locator.provider.LocationProvider;
import net.iubris.polaris.tasks.resume.freshlocation.GetFreshLocationTask;
import roboguice.inject.ContextSingleton;
import android.app.Activity;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

@ContextSingleton
public class GetFreshLocationTaskProvider implements Provider<GetFreshLocationTask> {

	private final GetFreshLocationTask getFreshLocationTask;
	
	@Inject
	public GetFreshLocationTaskProvider(Activity context, LocationManager locationManager, ConnectivityManager connectivityManager, LocationProvider locationProvider, 
			@LocationNullAllWrongString String locationNullAllWrong, @LocationNullEnableGPSString String locationNullEnableGPS) {
		this.getFreshLocationTask = new GetFreshLocationTask(context, locationManager, connectivityManager, locationProvider, locationNullAllWrong, locationNullEnableGPS);
	};
	
	@Override
	public GetFreshLocationTask get() {
		return getFreshLocationTask;
	}
}