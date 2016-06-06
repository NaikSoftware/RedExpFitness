package redexp.ua.redexp.fitness;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.fitness.data.DataType;
import com.google.api.services.prediction.model.Output;

import org.joda.time.DateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import redexp.ua.redexp.data.TrackPoint;
import redexp.ua.redexp.model.Training;
import redexp.ua.redexp.model.CyclingTraining;
import redexp.ua.redexp.model.RunningRecommendation;
import redexp.ua.redexp.model.RunningTraining;
import redexp.ua.redexp.model.TrainingType;
import redexp.ua.redexp.model.WalkingTraining;
import redexp.ua.redexp.prediction.PredictionManager;
import redexp.ua.redexp.util.PrefUtils;
import redexp.ua.redexp.util.logging.LogManager;
import redexp.ua.redexp.util.logging.Logger;
import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.schedulers.Schedulers;

public class FitnessManager {

    private static final String TAG = FitnessManager.class.getSimpleName();
    private static Logger logger = LogManager.getLogger();

    public static void pullTrainings(Context context) {
        logger.debug(TAG, "--------- Pull trainings -------");

        long startTimeMillis = PrefUtils.getPref(context,
                PrefUtils.PREF_LAST_SYNC_DATE,
                DateTime.now().minusMonths(1).getMillis(),
                Long.class);
        long endTimeMillis = DateTime.now().getMillis();

        FitnessHelper.getActivityBySegments(startTimeMillis, endTimeMillis,
                    DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<Training>>() {
                    @Override
                    public void onSuccess(List<Training> trainings) {
                        logger.debug(TAG, "Training has been taken successfully");
                        updateTrainings(trainings);
                        requestPrediction(context, trainings);
                    }

                    @Override
                    public void onError(Throwable error) {
                        logger.error(TAG, error.getMessage());
                    }
                });
    }

    private static void updateTrainings(List<Training> trainings) {
        logger.debug(TAG, "----- Save trainings [" + trainings.size() + "] -----");
        List<RunningTraining> runningTrainings = new ArrayList<>();
        List<WalkingTraining> walkingTrainings = new ArrayList<>();
        List<CyclingTraining> cyclingTrainings = new ArrayList<>();
        for (Training training : trainings) {
            if (training instanceof RunningTraining) {
                runningTrainings.add((RunningTraining) training);
            } else if (training instanceof WalkingTraining) {
                walkingTrainings.add((WalkingTraining) training);
            } else if (training instanceof CyclingTraining) {
                cyclingTrainings.add((CyclingTraining) training);
            }
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (!runningTrainings.isEmpty()) {
            realm.delete(RunningTraining.class);
            realm.copyToRealmOrUpdate(runningTrainings);
        }
        if (!walkingTrainings.isEmpty()) {
            realm.delete(WalkingTraining.class);
            realm.copyToRealmOrUpdate(walkingTrainings);
        }
        if (!cyclingTrainings.isEmpty()) {
            realm.delete(CyclingTraining.class);
            realm.copyToRealmOrUpdate(cyclingTrainings);
        }
        realm.commitTransaction();
        realm.close();
        logger.debug(TAG, "Training has been saved successfully");
    }


    private static void requestPrediction(Context context, List<Training> trainings) {
        logger.debug(TAG, "--------- request Prediction -------");
        final PredictionManager prediction;
        try {
            prediction = PredictionManager.getInstance(context);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            logger.error(TAG, e.getMessage());
            return;
        }
        Observable.from(trainings).filter(t -> t instanceof RunningTraining)
                .map(t -> (RunningTraining) t)
                .collect(HashMap::new, (Action2<Map<Long, RunningTraining>, RunningTraining>) (map, runningTraining) -> {

                    final long startTime = runningTraining.getStartTime();
                    final Long key = new DateTime(startTime).withTimeAtStartOfDay().getMillis();
                    RunningTraining t = map.get(key);
                    if (t == null || t.getDistance() < runningTraining.getDistance()) {
                        map.put(key, runningTraining);
                    }
                })
                .flatMapIterable(Map::values)
                .subscribe(new Subscriber<RunningTraining>() {
                    @Override
                    public void onCompleted() {
                        requestRecommendation(prediction);
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(RunningTraining training) {
                        requestPredictionVdot(prediction, training);
                    }
                });
    }

    private static void requestRecommendation(PredictionManager prediction) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TrackPoint> realmResults = realm.where(TrackPoint.class).equalTo("trainingType", TrainingType.RUNNING.name()).findAllSorted("timestamp", Sort.DESCENDING);
        if (realmResults.isEmpty()) return;
        final TrackPoint lastTrackPoint = realm.copyFromRealm(realmResults.get(0));
        final String vdot = String.valueOf(lastTrackPoint.getValue());
        realm.close();
        prediction.requestRecomendation(vdot)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Output>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Output output) {
                        logger.debug(TAG, "Recomendation taken");
                        final String outputValue = output.getOutputValue();
                        if (TextUtils.isEmpty(outputValue)) return;
                        final RunningRecommendation recommendation = new RunningRecommendation("1000", Float.valueOf(outputValue).longValue() / 1000);
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(recommendation);
                        realm.commitTransaction();
                        realm.close();
                        logger.debug(TAG, "Recomendation saved");
                    }
                });
    }

    private static void requestPredictionVdot(PredictionManager prediction, final RunningTraining training) {
        logger.debug(TAG, "- request VDOT for training...");
        final String distanceMeter = String.valueOf(training.getDistance());
        final String timeMillis = String.valueOf(training.getEndTime());
        prediction.predictVdot(distanceMeter, timeMillis)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Output>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Output output) {
                        logger.debug(TAG, "Prediction VDOT taken");
                        final String outputValue = output.getOutputValue();
                        if (TextUtils.isEmpty(outputValue)) return;
                        final long timestamp = new DateTime(training.getStartTime()).withTimeAtStartOfDay().getMillis();
                        final TrackPoint trackPoint = new TrackPoint(TrainingType.RUNNING.name(), timestamp, Float.valueOf(outputValue));
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(trackPoint);
                        realm.commitTransaction();
                        realm.close();
                        logger.debug(TAG, "Prediction VDOT saved");
                    }
                });
    }

}
