package redexp.ua.redexp.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

import redexp.ua.redexp.util.TimerWatcher;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import ua.naiksoftware.rxgoogle.RxGoogle;

/**
 * Created on 3/27/2016.
 */
public class SessionViewModel extends BaseViewModel {

    private static final String TAG = SessionViewModel.class.getSimpleName();

    public final ObservableField<String> timer = new ObservableField<>("00:00");
    public final ObservableField<String> speed = new ObservableField<>();
    public final ObservableField<String> distance = new ObservableField<>();
    public final ObservableField<String> calories = new ObservableField<>();
    public final ObservableField<String> steps = new ObservableField<>();
    public final ObservableBoolean isPaused = new ObservableBoolean(true);

    private Callback mCallback;

    private int stepsCounter;
    private float metersCounter;
    private int caloriesCounter;

    private TimerWatcher mTimerWatcher;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public SessionViewModel(Context context, Callback callback) {
        super(context);
        mCallback = callback;
        mTimerWatcher = new TimerWatcher.Builder()
                .watchField(timer)
                .up()
                .build();
        speed.set("0");
        distance.set("0");
        calories.set("0");
        steps.set("0");

        mSubscriptions.add(RxGoogle.Location.Fused.last()
                .subscribe(loc -> {
                    if (mCallback != null) {
                        mCallback.addPoint(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    }
                }));
    }

    private void receiveLocation() {
        mSubscriptions.add(RxGoogle.Location.Fused.requestLocation(
                new LocationRequest()
                        .setInterval(5000)
                        .setFastestInterval(5000)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                .subscribe(loc -> {
                    Log.d(TAG, "RECEIVED LOCATION: accuracy=" + loc.getAccuracy() + " speed=" + loc.getSpeed());
                    if (mCallback != null) {
                        mCallback.addPoint(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    }
                }));
    }

    private void subscribeSensors() {
        // Steps counter
        mSubscriptions.add(RxGoogle.Fit.Sensors.getDataPoints(
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build())
                .doOnError(error -> Log.e(TAG, "Query steps delta request error", error))
                .doOnUnsubscribe(() -> Log.d(TAG, "Steps listener removed"))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataPoint -> {
                    int stepsDelta = dataPoint.getValue(Field.FIELD_STEPS).asInt();
                    Log.d(TAG, "Receive data point steps delta: " + stepsDelta);
                    steps.set(String.valueOf(stepsCounter += stepsDelta));
                }));

        // Distance
        mSubscriptions.add(RxGoogle.Fit.Sensors.getDataPoints(
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_DISTANCE_DELTA)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build())
                .doOnError(error -> Log.e(TAG, "Query distance delta request error", error))
                .doOnUnsubscribe(() -> Log.d(TAG, "Distance listener removed"))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataPoint -> {
                    float distanceDelta = dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();
                    Log.d(TAG, "Receive data point distance delta: " + distanceDelta);
                    distance.set(String.valueOf(metersCounter += distanceDelta));
                }));

        // Calories
        mSubscriptions.add(RxGoogle.Fit.Sensors.getDataPoints(
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build())
                .doOnError(error -> Log.e(TAG, "Query distance delta request error", error))
                .doOnUnsubscribe(() -> Log.d(TAG, "Distance listener removed"))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataPoint -> {
                    int caloriesExpended = dataPoint.getValue(Field.FIELD_CALORIES).asInt();
                    Log.d(TAG, "Receive data point calories expended: " + caloriesExpended);
                    calories.set(String.valueOf(caloriesCounter += caloriesExpended));
                }));

        // Speed
        mSubscriptions.add(RxGoogle.Fit.Sensors.getDataPoints(
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_SPEED)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build())
                .doOnError(error -> Log.e(TAG, "Query distance delta request error", error))
                .doOnUnsubscribe(() -> Log.d(TAG, "Distance listener removed"))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataPoint -> {
                    float speedValue = dataPoint.getValue(Field.FIELD_SPEED).asFloat() / 1000 * 60;
                    Log.d(TAG, "Receive data point speed: " + speedValue);
                    speed.set(String.valueOf(speedValue));
                }));
    }

    public void onClickPlayPause(final View view) {
        if (mTimerWatcher.isPaused()) {
            mTimerWatcher.start();
            receiveLocation();
            subscribeSensors();
        } else {
            mTimerWatcher.pause();
            mSubscriptions.unsubscribe();
        }
        isPaused.set(mTimerWatcher.isPaused());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerWatcher.cancel();
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
        mCallback = null;
    }

    public interface Callback {

        void addPoint(LatLng point);
    }
}
