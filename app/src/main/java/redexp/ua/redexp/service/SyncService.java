package redexp.ua.redexp.service;

import android.content.Context;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;

import redexp.ua.redexp.Config;
import redexp.ua.redexp.fitness.FitnessManager;
import redexp.ua.redexp.util.PrefUtils;
import redexp.ua.redexp.util.logging.Logger;
import redexp.ua.redexp.util.logging.LoggerImpl;

/**
 * Created on 26.03.2016.
 */
public class SyncService extends GcmTaskService {
    public static final String TAG = "SyncService";
    private final Logger logger = new LoggerImpl();

    public static void schedule(Context context) {
        context = context.getApplicationContext();
        final boolean isAutosyncEnabled = PrefUtils.getPref(context,
                PrefUtils.PREF_AUTOSYNC_ENABLED, Config.AUTOSYNC_ENABLED_DEFAULT, Boolean.class);

        if (!isAutosyncEnabled) return;

        final long period = PrefUtils.getPref(context,
                PrefUtils.PREF_SYNC_INTERVAL_IN_SEC, Config.SYNC_INTERVAL_DEFAULT, Long.class);
        boolean isWifiOnly = PrefUtils.getPref(context,
                PrefUtils.PREF_WIFI_ONLY, Config.WIFI_ONLY_DEFAULT, Boolean.class);

        PeriodicTask syncTask = new PeriodicTask.Builder()
                .setService(SyncService.class)
                .setTag(SyncService.TAG)
                .setUpdateCurrent(true)
                .setPersisted(true)
                .setPeriod(period)
                .setFlex(period > 600 ? 600 : period) // 10 min window or period if lower
                .setRequiredNetwork(isWifiOnly ? PeriodicTask.NETWORK_STATE_UNMETERED : PeriodicTask.NETWORK_STATE_CONNECTED)
                .build();
        GcmNetworkManager.getInstance(context).schedule(syncTask);
    }


    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        logger.trace(TAG, "Syncing --------------------------------------------------------------");
        FitnessManager.pullTrainings(getApplicationContext());
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
