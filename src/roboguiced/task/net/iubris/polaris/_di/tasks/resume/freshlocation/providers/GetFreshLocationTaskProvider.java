package net.iubris.polaris._di.tasks.resume.freshlocation.providers;

import javax.inject.Inject;
import javax.inject.Provider;

import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.LocationNullAllWrongString;
import net.iubris.polaris.locator.core.provider.LocationProvider;
import net.iubris.polaris.locator.utils.LocationProviderEnabler;
import net.iubris.polaris.tasks.resume.freshlocation.GetFreshLocationTask;
import roboguice.inject.ContextSingleton;
import android.app.Activity;

@ContextSingleton
public class GetFreshLocationTaskProvider implements Provider<GetFreshLocationTask> {

	private final GetFreshLocationTask getFreshLocationTask;
	
	@Inject
	public GetFreshLocationTaskProvider(Activity activity, 
//			LocationManager locationManager, ConnectivityManager connectivityManager, 
			LocationProvider locationProvider, 
			@LocationNullAllWrongString String locationNullAllWrong, 
//			@LocationNullEnableGPSString String locationNullEnableGPS
			LocationProviderEnabler locationProviderEnabler
			) {
		this.getFreshLocationTask = new GetFreshLocationTask(activity, /*locationManager, connectivityManager,*/ 
				locationProvider, locationNullAllWrong,
				locationProviderEnabler
//				locationNullEnableGPS
				);
	};
	
	@Override
	public GetFreshLocationTask get() {
		return getFreshLocationTask;
	}
}