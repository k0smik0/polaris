package net.iubris.polaris._roboguice.module;

import com.google.inject.AbstractModule;

public abstract class PolarisModule extends AbstractModule {
	
	@Override
	protected final void configure() {
		bindLocationProvider();
		bindLocationUpdater();
	}
	
	protected abstract void bindLocationProvider();
	protected abstract void bindLocationUpdater();

}
