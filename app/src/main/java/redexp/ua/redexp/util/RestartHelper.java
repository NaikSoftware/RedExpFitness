package redexp.ua.redexp.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RestartHelper implements Application.ActivityLifecycleCallbacks {

    private final AtomicBoolean mIsAppRestarting = new AtomicBoolean(false);
    private Class<? extends Activity> mLastCreatedActivity;

    public RestartHelper(Application app) {
        app.registerActivityLifecycleCallbacks(this);
    }

    public synchronized void requestAppRestart() {
        if (mIsAppRestarting.get() || isStartScreen(mLastCreatedActivity)) return;
        mIsAppRestarting.set(true);
        launchStartActivity();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mLastCreatedActivity = activity.getClass();
        if (isStartScreen(mLastCreatedActivity)) {
            mIsAppRestarting.set(false);
        }
    }


    public abstract boolean isStartScreen(Class<? extends Activity> activityClass);
    public abstract void launchStartActivity();

    @Override public void onActivityStarted(Activity activity) {}

    @Override public void onActivityResumed(Activity activity) {}

    @Override public void onActivityPaused(Activity activity) {}

    @Override public void onActivityStopped(Activity activity) {}

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override public void onActivityDestroyed(Activity activity) {}
}
