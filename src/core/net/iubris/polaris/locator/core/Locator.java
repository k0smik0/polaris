/*******************************************************************************
 * Copyleft 2013 Massimiliano Leone - massimiliano.leone@iubris.net .
 * 
 * Locator.java is part of 'Polaris'.
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
package net.iubris.polaris.locator.core;

import net.iubris.polaris.locator.core.provider.LocationProvider;
import net.iubris.polaris.locator.core.updater.LocationUpdater;


public interface Locator extends LocationProvider, LocationUpdater {
	final static String ACTION_LOCATION_UPDATED_SUFFIX = ".action.ACTION_LOCATION_UPDATED";
	final static String EXTRA_KEY_LOCATION_UPDATED = "net.iubris.polaris.extra.LOCATION_UPDATED";
}
