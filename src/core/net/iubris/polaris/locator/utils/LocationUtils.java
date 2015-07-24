package net.iubris.polaris.locator.utils;


import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.StringRes;
//import android.support.annotation.UiThread;

/**
 * from https://github.com/quentin7b/android-location-tracker/
 * @author Quentin Klein <klein.quentin@gmail.com>
 *         <p>
 *         Helper for providers and locations
 *         </p>
 */
public class LocationUtils {

	private static final long MAX_SHOWING_DIALOG_TIMEOUT = 35*1000; // 35 sec

	/**
     * Check if the gps provider is enabled or not
     *
     * @param context any context
     * @return true if gps provider is enabled, false otherwise
     */
    public static boolean isGpsProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.GPS_PROVIDER);
    }

    /**
     * Check if the network provider is enabled or not
     *
     * @param context any context
     * @return true if the network provider is enabled, false otherwise
     */
    public static boolean isNetworkProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Check if the passive provider is enabled or not
     *
     * @param context any context
     * @return true if the passive provider is enabled, false otherwise
     */
    public static boolean isPassiveProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.PASSIVE_PROVIDER);
    }

    /**
     * Build a dialog to ask the user to change his location settings
     *
     * @param context a UI context
     */
//    @UiThread
    /*public static void askEnableProviders(final Context context) {
        askEnableProviders(context, R.string.provider_settings_message);
    }*/

    /**
     * Build a dialog to ask the user to change his location settings
     *
     * @param context         a UI Context
     * @param messageResource the message to show to the user in the dialog
     */
//    @UiThread
    /*public static void askEnableProviders(final Context context, @StringRes int messageResource) {
        askEnableProviders(context, messageResource, R.string.provider_settings_yes, R.string.provider_settings_yes);
    }*/

    /**
     * Build a dialog to ask the user to change his location settings
     *
     * @param context               a UI Context
     * @param message       the message to show to the user in the dialog
     * @param positiveLabel the positive button text
     * @param negativeLabel the negative button text
     */
//    @UiThread
    public static void askEnableProviders(final Activity activity, /*@StringRes*/ String message, String positiveLabel, /*@StringRes*/ String negativeLabel) {
        askEnableProviders(activity, message, positiveLabel, negativeLabel, null);
    }
    /**
     * Build a dialog to ask the user to change his location settings,<br/>
     * using also a latch for "no" response,<br/>which releases any thread blocked externally from latch
     *
     * @param activity      an Activity
     * @param message       the message to show to the user in the dialog
     * @param positiveLabel the positive button text
     * @param negativeLabel the negative button text
     * @param latch 		the latch blocking an external thread; clicking "no",<br/>
     * 			the latch will be released, allowing the thread to continue its work
     */
    public static void askEnableProviders(final Activity activity, /*@StringRes*/ String message, String positiveLabel, /*@StringRes*/ String negativeLabel, final CountDownLatch latch) {
    	if (!activity.isFinishing()) {
	    	final AlertDialog alertDialog = new AlertDialog.Builder(activity)
		        .setMessage(message)
		        .setCancelable(false)
		        .setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		            }
		        })
		        .setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	if (latch!=null)
		            		latch.countDown();
		                dialog.dismiss();
		            }
		        })
		        .show();
	    	new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (alertDialog.isShowing())
						alertDialog.dismiss();					
				}
			}, MAX_SHOWING_DIALOG_TIMEOUT);
    	}
    }

    /**
     * Check if the provider is enabled of not
     *
     * @param context  any context
     * @param provider the provider to check
     * @return true if the provider is enabled, false otherwise
     */
    private static boolean isProviderEnabled(Context context, String provider) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(provider);
    }

}
