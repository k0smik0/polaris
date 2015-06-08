package net.iubris.polaris.locator;

import android.content.Context;

public class LocatorUtils {
	public static final String getActionLocationUpdatedString(Context context) {
		return context.getPackageName()+Locator.ACTION_LOCATION_UPDATED_SUFFIX;
	}
}
