/*******************************************************************************
 * Copyleft 2013 Massimiliano Leone - massimiliano.leone@iubris.net .
 * 
 * LocationUtils.java is part of 'Polaris'.
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
package net.iubris.polaris.locator.utils;

import java.util.concurrent.TimeUnit;

import net.iubris.polaris.locator.utils.exceptions.LocationNotSoCarefulException;
import net.iubris.polaris.locator.utils.exceptions.LocationNotSoFarException;
import net.iubris.polaris.locator.utils.exceptions.LocationNotSoNewerException;
import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

/**
 * using tips from: <a href="http://developer.android.com/guide/topics/location/strategies.html">
 * http://developer.android.com/guide/topics/location/strategies.html</a>
 */

public class LocationStrategiesUtils {
	
	@SuppressLint("NewApi")
	public static boolean isLocationOld(Location location, int seconds) {
		long now = System.currentTimeMillis()/(10^3);
		float locationTime; 
		if ( Build.VERSION.SDK_INT >= 17 )
			locationTime = location.getElapsedRealtimeNanos()/(10^9);
		else 
			locationTime = location.getTime();
		
		float delta = (now - locationTime)/1000;
		if (delta > seconds)
			return true;
		return false;
	}
	
	/**
	 * Compares locations by distance, within weighted threshold.
	 * 
	 * <p>
	 * <ul>
	 * 	<li>if distance >  distanceMaximumThreshold => really farer, return true</li>
	 * 	<li>if threshold/3 < distance < threshold =>  not so farer, throw exception</li>
	 * 	<li>if 0 < distance < threshold/3 => almost near, return false</li>
	 * </ul>
	 * 
	 * we use "threshold/3" as value for weighted threshold:
	 * <pre>
	 * <code>
	 * | -- false -- |   -- throw exception --   | true <br/>
	 * 0            TS/3    	    ts*2/3         TS
	 * </code>
	 * </pre>
	 * 
	 * @param newLocation
	 * @param currentLocation
	 * @param distanceMaximumThresholdInMeters
	 * @return true if newLocation is significantly far than currentLocation, false elsewhere
	 * @throws LocationNotSoFarException if newLocation is not so far than currentLocation, that is 
	 * within distanceMaximumThreshold
	 */
	public static boolean isLocationFar(Location newLocation, Location currentLocation, 
			float distanceMaximumThresholdInMeters) throws LocationNotSoFarException {
		float distanceDelta = newLocation.distanceTo(currentLocation);
//Log.d("LocationStrategiesUtils:81", "\n"
//		+"currentLocation: "+currentLocation
//		+" newLocation: "+newLocation
//		+" distanceDelta = "+ new DecimalFormat("#.##").format(distanceDelta)+"m");
		if (distanceDelta<1 && distanceMaximumThresholdInMeters<11)
			// < 1 <=> < 10m
			return false;

		if (distanceDelta > distanceMaximumThresholdInMeters) return true; // distance > threshold
		
		float sensibleThreshold = distanceMaximumThresholdInMeters/3;
		if (distanceDelta < sensibleThreshold) return false; // 0 < distance < threshold/3
		
		throw new LocationNotSoFarException("newLocation ["+newLocation+"] is not so far from "
				+ "["+currentLocation+"]: just "+distanceDelta+" m"); // distance > threshold/3 
	}
	
	/**
	 * 	Compare currentLocation with newLocation by time
	 * @param currentLocation
	 * @param newLocation
	 * @param timeMaximumThresholdInSeconds - in seconds
	 * @return true if newLocation is newer than currentLocation, according to timeMinimumThreshold 
	 * - false otherwise 
	 * @throws LocationNotSoNewerException when newLocation is newer than currentLocation, but 
	 * retrieved within threshold defined by timeMinimumThreshold parameter
	 */	
//	@SuppressLint("NewApi")
	public static boolean isLocationNewer(Location newLocation, Location currentLocation, 
			float timeMaximumThresholdInSeconds ) throws LocationNotSoNewerException {		 
/*Log.d("LocationUtils.isLocationOlder","current time: "+System.currentTimeMillis());
Log.d("LocationUtils.isLocationOlder","location time: "+location.getTime());
Log.d("LocationUtils.isLocationOlder","timeMinimumThreshold: "+timeMinimumThreshold);
Log.d("LocationUtils.isLocationOlder","difference: "+ (System.currentTimeMillis() - location.getTime()) );*/
//		old way
//		if(System.currentTimeMillis() - location.getTime() > timeMinimumThreshold) return true;
		
		if (currentLocation == null) {
//			A new location is always better than no location
			return true;
		}
		
		// es. currentLocation = 180s; newLocation = 10s; timeDelta = 170s
		final float timeDeltaInSeconds = TimeUnit.SECONDS.convert(
				getTimeDeltaInMilliseconds(newLocation, currentLocation),
				TimeUnit.MILLISECONDS);
//Log.d("LocationStrategiesUtils:115","timeDelta: "+timeDeltaInSeconds+"s");
		// with timeMinimumThreshold = 60s => currentLocation is older than newLocation
		if (timeDeltaInSeconds > timeMaximumThresholdInSeconds) { // isSignificantlyNewer
			return true;
		} else if (timeDeltaInSeconds < -timeMaximumThresholdInSeconds) // isSignificantlyOlder
			return false;
//		-timeMinimumThreshold < timeDelta < timeMinimumThreshold <=> timeDelta > 0 // isNewer
		throw new LocationNotSoNewerException("newLocation is newer than currentLocation, but both "
				+ "are retrieved in last "+timeMaximumThresholdInSeconds/1000+" seconds");
	}
	
	
	/**
	 * Compare currentLocation with newLocation by accuracy (distance error)
	 * @param newLocation
	 * @param currentLocation
	 * @param accuracyDistanceMaximumThreshold - in meters
	 * @return true is newLocation has higher accuracy than currentLocation, false otherwise
	 * @throws LocationNotSoCarefulException if newLocation is retrieved in same admitted area, 
	 * according to accuracyDistanceMaximumThreshold parameter
	 */
	public static boolean isLocationCareful(Location newLocation, Location currentLocation, 
			float accuracyDistanceMaximumThreshold) throws LocationNotSoCarefulException{
		
//		old way
//		if (actualLocation.distanceTo(oldLocation) > distanceMinimumThreshold) return true;
		
		// lower value from getAccuraty => better accuracy  
		final float accuracyDelta = newLocation.getAccuracy() - currentLocation.getAccuracy();
		Log.d("LocationStrategiesUtils","accuracyDelta: "+accuracyDelta);
		if (accuracyDelta < 0.0) 	// ok, delta < 0, so is careful: isMoreAccurate
			return true;
		if (accuracyDelta > accuracyDistanceMaximumThreshold) { 
			// significantly lower accuracy: isSignificantlyLessAccurate !
			return false;
		}
		// accuracyDelta > 0.0 // newLocation has higher accuracy, but within threshold: isLessAccurate
		// delta is low, and under threshold <=> 0 < accuracyDelta < accuracyDistanceMaximumThreshold
		throw new LocationNotSoCarefulException("newLocation has higher accuracy than currentLocation, "
				+ "but both are retrieved in admitted area, according to declared "
				+ "accuracyDistanceMaximumThreshold="+accuracyDistanceMaximumThreshold +"m");
	}
	
	
	/** Determines whether one Location reading is better than the current Location fix (better = 
	  * 	combination of "newer" and "higher accuracy" and etc)
	  * @param newLocation  The new Location that you want to evaluate
	  * @param currentLocation  The current Location fix, to which you want to compare the new one
	  * @param timeMaximumThresholdInSeconds threshold for evaluating if newLocation is newer (and how) 
	  * 	than currentLocation  
	  * @param accuracyDistanceMaximumThreshold threshold for evaluating if newLocation has more accuracy 
	  * 	(and how) than currentLocation
	  * @see <a href="http://developer.android.com/guide/topics/location/strategies.html">tips used 
	  * 	for implementation strategy</a>
	  */
	public static boolean isLocationBetter(Location newLocation, Location currentLocation, 
			int timeMaximumThresholdInSeconds, int accuracyDistanceMaximumThreshold) {
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
	    
//	    boolean isSignificantlyNewer = false; 
//	    boolean isNewer = false;
	    
    	try {
    		
    	    // return true If it's been more than "timeMinimumThreshold" (2?) minutes since the current location, 
    		// so use the new location because the user has likely moved
    		// else return false - really "false" means newLocation time is older than currentLocation ones 
    		// => it must be worse, so return false 
    		return isLocationNewer(newLocation, currentLocation, timeMaximumThresholdInSeconds);
    		    	    
		} catch (LocationNotSoNewerException e) { 
			// we arrive if true nor false are returned, however the catch do nothing and go straitght
			
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
    	
    	// if we are here, newLocation is newer than currentLocation, but retrieved within threshold, 
    	// so we check for any accuracy
	    boolean isLessAccurate = false;
//	    boolean isSignificantlyLessAccurate = false;
//	    boolean isMoreAccurate = false;
	    
	    // Determine location quality using a combination of timeliness and accuracy
	    try {
//	    	isMoreAccurate = 
			if (isLocationCareful(newLocation, currentLocation, accuracyDistanceMaximumThreshold));
//	    	if (isMoreAccurate) {
		        return true;
//	    	}
	    	// if we are here, isLessAccurate is false;
//	    	isSignificantlyLessAccurate = true; // = !isMoreAccurate
		} catch (LocationNotSoCarefulException e) {
			// if we are here, we have higher accuracy, but under threshold that is 
			// 0 < accuracyDelta < accuracyThreshold
			// we try check by providers
			
//			boolean isFromSameProvider = 
			if (isSameProvider(newLocation.getProvider(), currentLocation.getProvider()))
			// isSignificantlyLessAccurate is false
//		    if (isFromSameProvider) { // really is: (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
		        return true;
//		    }
			
			isLessAccurate = true;
		}
	    
	    // isMoreAccurate (= isLocationCareful=true) is still false (but not returned) AND isLessAccurate 
	    // is still false (not throwed LocationNotSoCarefulException)
	    if (!isLessAccurate) { 
	    	// really is (isNewer=[catched LocationNotSoNewer from isLocationNewer] && !isLessAccurate) 
	    	return true;
	    }
	    
	    return false;
	}
	
	/*public static boolean is() {
		return false;	
	}*/
	
//	public static boolean is
	/*
	public static boolean isNewerAndAccurate(boolean isNewer, boolean isLessAccurate) {
		if (isNewer && !isLessAccurate)
	        return true;
		return false;
	}
	public static boolean isNewerAndAccurateAndSameProvider(boolean isNewer, 
		boolean isSignificantlyLessAccurate, boolean isFromSameProvider) {
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
	private static long getTimeDeltaInMilliseconds(Location newLocation, Location currentLocation) {
		if ( Build.VERSION.SDK_INT >= 17 ) {
			long elapsedRealtimeNanosNewLocation = newLocation.getElapsedRealtimeNanos();
			long elapsedRealtimeNanosCurrentLocation = currentLocation.getElapsedRealtimeNanos();
			long timeDelta = TimeUnit.MILLISECONDS.convert(elapsedRealtimeNanosNewLocation - 
					elapsedRealtimeNanosCurrentLocation, TimeUnit.NANOSECONDS);

//			Log.d("LocationStrategiesUtils","elapsedRealtimeNanosNewLocation: "+elapsedRealtimeNanosNewLocation+"ns"
//					+", elapsedRealtimeNanosCurrentLocation: "+elapsedRealtimeNanosCurrentLocation+"ns"
//					+", timeDelta: "+timeDelta+"ms");
			
			return timeDelta;
		}
		return newLocation.getTime() - currentLocation.getTime();
	}
	
	/*public static boolean isBetter(Location newLocation, Location currentLocation, 
	 * long timeMinimumThreshold, int accuracyDistanceMaximumThreshold) {
		return false;
	}*/
	
//	public static boolean isLocationHighAccuracy(LocationManager locationManager, String provider, 
//		float accuracy, int accuracyMaxError) {
//		final String gpsProvider = LocationManager.GPS_PROVIDER;
//		final boolean isLowAccuracy = accuracy > accuracyMaxError;
//		final boolean isProviderGPS = provider.equals(gpsProvider);
//		final boolean isGPSEnabled = locationManager.isProviderEnabled(gpsProvider);
//		if ( isLowAccuracy && !isProviderGPS && !isGPSEnabled)
//			return true;
//		else
//			return false;
//	}
	
	public static boolean isHighAccuracy(LocationManager locationManager, String provider, 
			float accuracy, int accuracyMaxError) {
		final String gpsProvider = LocationManager.GPS_PROVIDER;
		final boolean isLowAccuracy = accuracy > accuracyMaxError;
		final boolean isProviderGPS = provider.equals(gpsProvider);
		final boolean isGPSEnabled = locationManager.isProviderEnabled(gpsProvider);
		if ( isLowAccuracy && !isProviderGPS && !isGPSEnabled)
			return true;
		else
			return false;
	}
}
