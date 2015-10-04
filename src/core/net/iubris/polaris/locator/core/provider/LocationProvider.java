/*******************************************************************************
 * Copyleft 2013 Massimiliano Leone - massimiliano.leone@iubris.net .
 * 
 * LocationProvider.java is part of 'Polaris'.
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
package net.iubris.polaris.locator.core.provider;

import net.iubris.polaris.locator.core.exceptions.LocationNullException;
import android.location.Location;

public interface LocationProvider {
	/**
	 * @return provided location
	 */
	Location getLocation() throws LocationNullException;
	/**
	 * @return time threshold used to check new locations - in milliseconds
	 */
	Integer getMinimumTimeThreshold();
	/**
	 * @return distance threshold used to check new locations - in meters
	 */
	Integer getMinimumDistanceThreshold();
}
