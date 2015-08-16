/*******************************************************************************
 * Copyleft 2013 Massimiliano Leone - massimiliano.leone@iubris.net .
 * 
 * PolarisModule.java is part of 'Polaris'.
 * 
 * 'Polaris' is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * 'Polaris' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with 'Polaris'; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 ******************************************************************************/
package net.iubris.polaris._di._roboguice.module.task;

import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.LocationNullAllWrongString;
import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.ProviderSettingsMessage;
import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.ProviderSettingsNo;
import net.iubris.polaris._di.tasks.resume.freshlocation.annotations.ProviderSettingsYes;
import net.iubris.polaris._di.tasks.resume.freshlocation.providers.GetFreshLocationTaskProvider;
import net.iubris.polaris._di.tasks.resume.freshlocation.providers.LocationProviderEnablerProvider;
import net.iubris.polaris.locator.utils.LocationProviderEnabler;
import net.iubris.polaris.tasks.resume.freshlocation.GetFreshLocationTask;

import com.google.inject.AbstractModule;

public abstract class PolarisTaskModule extends AbstractModule {
	
	@Override
	protected final void configure() {
		bindLocationNullAllWrongString(); // provides an english message
//		bindLocationNullEnableGPSString(); // provides an english message
		bind(GetFreshLocationTask.class).toProvider(GetFreshLocationTaskProvider.class);
		bind(LocationProviderEnabler.class).toProvider(LocationProviderEnablerProvider.class);
		
		bindProviderSettingsMessage();
		bindProviderSettingsYes();
		bindProviderSettingsNo();
	}
	
	/**
	 * binding needed for {@link GetFreshLocationTask}<br/>
	 * default: in english
	 */
	protected void bindLocationNullAllWrongString() {
		bind(String.class).annotatedWith(LocationNullAllWrongString.class).toInstance("location is null,\nsomething was wrong,\ncheck your settings.");
	}
	protected void bindProviderSettingsMessage() {
		bind(String.class).annotatedWith(ProviderSettingsMessage.class).toInstance("Your GPS seems to be disabled, do you want to enable it?");
	}
	protected void bindProviderSettingsYes() {
		bind(String.class).annotatedWith(ProviderSettingsYes.class).toInstance("Yes");
	}
	protected void bindProviderSettingsNo() {
		bind(String.class).annotatedWith(ProviderSettingsNo.class).toInstance("No");
	}
	
	
	/**
	 * binding needed for {@link GetFreshLocationTask}<br/>
	 * default: do nothing 
	 */
	/*protected void bindLocationNullEnableGPSString() {
		bind(String.class).annotatedWith(LocationNullEnableGPSString.class).toInstance("location is null, you could enable your GPS.");
	}*/
}
