/*******************************************************************************
 * 
 * Copyleft 2012 Massimiliano Leone - massimiliano.leone@iubris.net .
 * LocationUtils.java is part of 'Diane'.
 * 
 * 'Diane' is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * 'Diane' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with 'Diane' ; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 ******************************************************************************/
package net.iubris.polaris.locator.utils;

import net.iubris.polaris.locator.utils.exceptions.LocationNotSoCarefulException;
import net.iubris.polaris.locator.utils.exceptions.LocationNotSoFarException;
import net.iubris.polaris.locator.utils.exceptions.LocationNotSoNewerException;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Build;

/**
 * using tips from: http://developer.android.com/guide/topics/location/strategies.html
 */
@SuppressLint("NewApi")
public class LocationUtils {
	
	/**
	 * Compare locations by distance, within weighted threshold: <br/><br/>
	 * if distance >  distanceMaximumThreshold => really farer, return true<br/>
	 * if threshold/3 < distance < threshold =>  not so farer, throw exception<br/>
	 * if 0 < distance < threshold/3 => almost near, return false <br/><br/>
	 * 
	 * we use "threshold/3" as value for weighted threshold: <br/>
	 * <pre>
	 * | -- false -- | 	-- throw exception --   | true <br/>
	 * 0	     TS/3	    ts*2/3 		TS
	 * </pre>
	 * @param newLocation
	 * @param currentLocation
	 * @param distanceMaximumThreshold
	 * @return
	 * @throws LocationNotSoFarException 
	 */
	public static boolean isLocationFar(Location newLocation, Location currentLocation, float distanceMaximumThreshold) throws LocationNotSoFarException {
		float deltaDistance = newLocation.distanceTo(currentLocation);
		float sensibleThreshold = distanceMaximumThreshold/3;
		if (deltaDistance > distanceMaximumThreshold) return true; // distance >  threshold
		if (deltaDistance < sensibleThreshold) return false; // 0 < distance < threshold/3
		throw new LocationNotSoFarException(""); // distance > threshold/3 
	}
	
	/**
	 * @param currentLocation
	 * Compare currentLocation with newLocation by time
	 * @param newLocation
	 * @param timeMinimumThreshold - in milliseconds
	 * @return true if currentLocation is older than newLocation, according to timeMinimumThreshold - false otherwise 
	 * @throws LocationNotSoNewerException  
	 */	
//	@SuppressLint("NewApi")
	public static boolean isLocationNewer(Location newLocation, Location currentLocation, float timeMinimumThreshold ) throws LocationNotSoNewerException {		 
/*Log.d("LocationUtils.isLocationOlder","current time: "+System.currentTimeMillis());
Log.d("LocationUtils.isLocationOlder","location time: "+location.getTime());
Log.d("LocationUtils.isLocationOlder","timeMinimumThreshold: "+timeMinimumThreshold);
Log.d("LocationUtils.isLocationOlder","difference: "+ (System.currentTimeMillis() - location.getTime()) );*/
//		old way
//		if(System.currentTimeMillis() - location.getTime() > timeMinimumThreshold) return true;
		
		if (currentLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }
		
		// es. currentLocation = 180s; newLocation = 10s; timeDelta = 170s
		final long timeDelta = getTimeDelta(newLocation, currentLocation);		
		// with timeMinimumThreshold = 60s => currentLocation is older than newLocation
		if (timeDelta > timeMinimumThreshold) { // isSignificantlyNewer
			return true;
		} else if (timeDelta < -timeMinimumThreshold) // isSignificantlyOlder
			return false;
//		-timeMinimumThreshold < timeDelta < timeMinimumThreshold <=> timeDelta > 0 // isNewer
		throw new LocationNotSoNewerException("newLocation is newer than currentLocation, but both are retrieved in last "+timeMinimumThreshold/1000+" seconds");
	}
	
	
	/**
	 * Compare currentLocation with newLocation by accuracy (distance error)
	 * @param actualLocation
	 * @param currentLocation
	 * @param distanceMinimumThreshold - in meters
	 * @return true is newLocation is nearer than currentLocation, false otherwise - "near" means "higher accuracy"
	 * @throws LocationNotSoCarefulException 
	 */
	public static boolean isLocationCareful(Location newLocation, Location currentLocation, float accuracyDistanceMaximumThreshold) throws LocationNotSoCarefulException{
		
//		old way
//		if (actualLocation.distanceTo(oldLocation) > distanceMinimumThreshold) return true;
		
		// lower value from getAccuraty => better accuracy  
		final float accuracyDelta = newLocation.getAccuracy() - currentLocation.getAccuracy();
		
		if (accuracyDelta < 0.0) 	// ok, delta < 0, so is careful: isMoreAccurate
			return true;
		if (accuracyDelta > accuracyDistanceMaximumThreshold) { // significantly farer: isSignificantlyLessAccurate !
			return false;
		}
		// accuracyDelta > 0.0 // newLocation is farer: isLessAccurate
		// delta is low, and under threshold <=> 0 < accuracyDelta < accuracyDistanceMaximumThreshold
		throw new LocationNotSoCarefulException("newLocation is nearer (careful) than currentLocation, but both are retrieved in admitted area, according to declared accuracyDistanceMaximumThreshold="+accuracyDistanceMaximumThreshold +"m");
	}
	
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param newLocation  The new Location that you want to evaluate
	  * @param currentLocation  The current Location fix, to which you want to compare the new one
	  * @see <a href="http://developer.android.com/guide/topics/location/strategies.html">tips used for implementation strategy</a>
	  */
	public static boolean isLocationBetter(Location newLocation, Location currentLocation, long timeMinimumThreshold, int accuracyDistanceMaximumThreshold) {
		/* tips way
	    if (currentLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = getTimeDelta(newLocation, currentLocation);
	    boolean isSignificantlyNewer = timeDelta > timeMinimumThreshold;
	    boolean isSignificantlyOlder = timeDelta < -timeMinimumThreshold;
	    boolean isNewer = timeDelta > 0;
	    */
	    
	    boolean isSignificantlyNewer = false; 
//	    boolean isNewer = false;
	    
    	try {
    		isSignificantlyNewer = isLocationNewer(newLocation, currentLocation, timeMinimumThreshold);
    	    // If it's been more than "timeMinimumThreshold" (2?) minutes since the current location, use the new location
    	    // because the user has likely moved
    		if (isSignificantlyNewer) {
    	        return true;
    		} 
    		// 	isSignificantlyOlder = !isSignificantlyNewer;
    		// If the new location is more than "timeMinimumThreshold" minutes older, it must be worse
    	    return false;    	    
		} catch (LocationNotSoNewerException e) {
//			isNewer = true;
		}
	    
	    

	    

	    /* old way, by tips
	    // improvement ?
//	    int accuracyThreshold = (accuracyDistanceMaximumThreshold < 200) ? accuracyDistanceMaximumThreshold : 200;
	    int accuracyThreshold = accuracyDistanceMaximumThreshold;
	    
	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (newLocation.getAccuracy() - currentLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > accuracyThreshold;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    */
    	
    	// if we are here, isNewer is true
	    boolean isLessAccurate = false;
//	    boolean isSignificantlyLessAccurate = false;
	    boolean isMoreAccurate = false;
	    
	    // Determine location quality using a combination of timeliness and accuracy
	    
	    try {
	    	isMoreAccurate = isLocationCareful(newLocation, currentLocation, accuracyDistanceMaximumThreshold);
	    	if (isMoreAccurate) {
		        return true;
	    	}
	    	// if we are here, isLessAccurate is false;
//	    	isSignificantlyLessAccurate = true; // = !isMoreAccurate
		} catch (LocationNotSoCarefulException e) {
			// if we are here, isSignificantlyLessAccurate is false, that is 0 < accuracyDelta < accuracyThreshold
			
			boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentLocation.getProvider());
			// isSignificantlyLessAccurate is false
		    if (isFromSameProvider) { // really is: (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
		        return true;
		    }
			
			isLessAccurate = true;
		}
	    
	    // isMoreAccurate is false (not returned) AND isLessAccurate is false (not throwed exception)
	    if (!isLessAccurate) { // really is (isNewer && !isLessAccurate) 
	    	return true;
	    }
	    
	    return false;
	}
	
	public static boolean is() {
		return false;	
	}
	
//	public static boolean is
	/*
	public static boolean isNewerAndAccurate(boolean isNewer, boolean isLessAccurate) {
		if (isNewer && !isLessAccurate)
	        return true;
		return false;
	}
	public static boolean isNewerAndAccurateAndSameProvider(boolean isNewer, boolean isSignificantlyLessAccurate, boolean isFromSameProvider) {
		if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
	        return true;
		return false;
	}*/

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	@SuppressLint("NewApi")
	private static long getTimeDelta(Location a, Location b) {
		if ( Build.VERSION.SDK_INT > 16 )
			return Math.abs( a.getElapsedRealtimeNanos() - b.getElapsedRealtimeNanos() );
		return Math.abs( a.getTime() - b.getTime() );
	}
	
	/*public static boolean isBetter(Location newLocation, Location currentLocation, long timeMinimumThreshold, int accuracyDistanceMaximumThreshold) {
		return false;
	}*/
}
