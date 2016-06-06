package redexp.ua.redexp;

import java.util.concurrent.TimeUnit;

/**
 * Created on 24.03.2016.
 */
public class Config {
    /**
     * Specify your Google Developers Console project ID, your service account's email address, and
     * the name of the P12 file.
     */
    public static final String GOOGLE_PROJECT_ID = "redexp-1255";
    public static final String GOOGLE_SERVICE_ACCT_EMAIL = "prediction-p12@redexp-1255.iam.gserviceaccount.com";
    public static final String GOOGLE_SERVICE_ACCT_KEYFILE = "RedExp-6e51737907ee.p12";

    public static final String PREDICTION_APPLICATION_NAME = BuildConfig.APPLICATION_ID;

    public static final Long SYNC_INTERVAL_DEFAULT = TimeUnit.HOURS.toSeconds(1);
    public static final Boolean WIFI_ONLY_DEFAULT = true;
    public static final Boolean AUTOSYNC_ENABLED_DEFAULT = true;
}
