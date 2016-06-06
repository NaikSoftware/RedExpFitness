package redexp.ua.redexp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Helper class for working with preferences
 */
public final class PrefUtils {

    public static final String PREF_SYNC_INTERVAL_IN_SEC = "PREF_SYNC_INTERVAL_IN_SEC";
    public static final String PREF_AUTOSYNC_ENABLED = "PREF_AUTOSYNC_ENABLED";
    public static final String PREF_WIFI_ONLY = "PREF_SYNC_INTERVAL_IN_SEC";
    public static final String PREF_LAST_SYNC_DATE = "PREF_LAST_SYNC_DATE";
//    public static final String PREF_SYNC_ON_START = "PREF_SYNC_ON_START";

    private static SharedPreferences sPreferences;

    public static SharedPreferences getDefaultSharedPreferences(Context _context) {
        if (sPreferences == null) {
            sPreferences = PreferenceManager.getDefaultSharedPreferences(_context.getApplicationContext());
        }
        return sPreferences;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPref(Context _context, String prefKey, T defValue, Class<T> clazz) {
        final SharedPreferences pref = getDefaultSharedPreferences(_context);
        if (Long.class == clazz) {
            return (T) (Long.valueOf(pref.getLong(prefKey, (Long) defValue)));
        } else if (Integer.class == clazz) {
            return (T) (Integer.valueOf(pref.getInt(prefKey, (Integer) defValue)));
        } else if (defValue instanceof String) {
            return (T) (pref.getString(prefKey, String.valueOf(defValue)));
        } else if (defValue instanceof Boolean) {
            return (T) (Boolean.valueOf(pref.getBoolean(prefKey, (Boolean) defValue)));
        }
        throw new UnsupportedOperationException("Class " + clazz + " not supported");
    }

}
