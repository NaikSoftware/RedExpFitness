package redexp.ua.redexp;


import android.support.multidex.MultiDex;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import redexp.ua.redexp.service.SyncService;
import redexp.ua.redexp.util.RestartHelper;
import ua.naiksoftware.rxgoogle.RxGoogle;

public class Application extends android.app.Application {

    private static Application INSTANCE;
//    private RestartHelper mRestartHelper;
//
//    public void restartApplication() {
//        mRestartHelper.requestAppRestart();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        MultiDex.install(this);

        RxGoogle.init(
                this,
                new Api[]{
                        Fitness.SESSIONS_API,
                        Fitness.HISTORY_API,
                        Fitness.SENSORS_API,
                        Fitness.RECORDING_API,
                        LocationServices.API},
                new Scope[]{
                        new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE),
                        new Scope(Scopes.FITNESS_BODY_READ_WRITE),
                        new Scope(Scopes.FITNESS_LOCATION_READ_WRITE),
                        new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE)}
        );
        RxGoogle.setDefaultTimeout(15, TimeUnit.SECONDS);

        initRealm();
        SyncService.schedule(getApplicationContext());
    }

    private void initRealm() {

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }


//        mRestartHelper = new RestartHelper(this){
//            @Override
//            public boolean isStartScreen(Class<? extends Activity> activityClass) {
//                return LoginActivity.class.getName().equals(activityClass.getName());
//            }
//
//            @Override
//            public void launchStartActivity() {
//                LoginActivity.getIntent(getApplicationContext());
//            }
//        };
}
