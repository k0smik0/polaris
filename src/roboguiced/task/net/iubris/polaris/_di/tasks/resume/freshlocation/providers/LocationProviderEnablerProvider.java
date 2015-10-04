package net.iubris.polaris._di.tasks.resume.freshlocation.providers;

import javax.inject.Inject;
import javax.inject.Provider;

import net.iubris.polaris._di.tasks.freshlocation.annotations.ProviderSettingsMessage;
import net.iubris.polaris._di.tasks.freshlocation.annotations.ProviderSettingsNo;
import net.iubris.polaris._di.tasks.freshlocation.annotations.ProviderSettingsYes;
import net.iubris.polaris.locator.utils.LocationProviderEnabler;
import android.app.Activity;

//@Singleton
public class LocationProviderEnablerProvider implements Provider<LocationProviderEnabler> {
	private final LocationProviderEnabler locationProviderEnabler;
	
	@Inject
	public LocationProviderEnablerProvider(Activity activity, 
			@ProviderSettingsMessage String providerSettingsMessage, 
			@ProviderSettingsYes String providerSettingsYes, 
			@ProviderSettingsNo String providerSettingsNo) {
		this.locationProviderEnabler = new LocationProviderEnabler(activity, providerSettingsMessage, providerSettingsYes, providerSettingsNo);
	}

	@Override
	public LocationProviderEnabler get() {
		return locationProviderEnabler;
	}
}
