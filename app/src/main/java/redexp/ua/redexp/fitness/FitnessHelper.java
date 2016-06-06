package redexp.ua.redexp.fitness;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import redexp.ua.redexp.model.Training;
import redexp.ua.redexp.model.CyclingTraining;
import redexp.ua.redexp.model.RunningTraining;
import redexp.ua.redexp.model.WalkingTraining;
import redexp.ua.redexp.util.logging.LogManager;
import redexp.ua.redexp.util.logging.Logger;
import rx.Observable;
import rx.Single;
import ua.naiksoftware.rxgoogle.RxGoogle;

/**
 * Created on 20.03.16.
 */
public class FitnessHelper {

    public static final String TAG = FitnessHelper.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger();

    private static DataReadRequest.Builder historyByActivitySegmentsRequest(DataType type, DataType agregate, long startTimeMillis, long endTimeMillis) {
        return new DataReadRequest.Builder()
                .aggregate(type, agregate)
                .bucketByActivitySegment(1, TimeUnit.MINUTES)
                .setTimeRange(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS);
    }

    public static Single<List<Training>> getActivityBySegments(long startTimeMillis, long endTimeMillis, DataType dataType, DataType aggregate) {
        DataReadRequest.Builder readRequest = historyByActivitySegmentsRequest(dataType, aggregate, startTimeMillis, endTimeMillis);
        return RxGoogle.Fit.History.read(readRequest.enableServerQueries().build())
                .compose(RxGoogle.OnExceptionResumeNext.with(RxGoogle.Fit.History.read(readRequest.build())))
                .flatMapObservable(dataReadResult -> Observable.just(dataReadResult.getBuckets()))
                .flatMap(buckets -> {
                    if (buckets.isEmpty())
                        logger.debug(TAG, "No activity segments data for type " + dataType.getName());
                    List<Training> list = new ArrayList<>(buckets.size());
                    for (Bucket bucket : buckets) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                Training training = convertToTraining(bucket.getActivity(), dp);
                                if (training != null) list.add(training);
                            }
                        }
                    }
                    return Observable.just(list);
                }).toSingle();
    }

    private static Training convertToTraining(String activity, DataPoint dp) {
        long startMillis = dp.getStartTime(TimeUnit.MILLISECONDS);
        long endMillis = dp.getEndTime(TimeUnit.MILLISECONDS);
        Training training = null;
        switch (activity) {
            case "running":
                if (dp.getDataType().getFields().contains(Field.FIELD_DISTANCE)) {
                    final float distance = dp.getValue(Field.FIELD_DISTANCE).asFloat();
                    training = new RunningTraining(startMillis, (long) distance, endMillis);
                }
                break;
            case "walking":
                if (dp.getDataType().getFields().contains(Field.FIELD_DISTANCE)) {
                    final int steps = dp.getDataType().getFields().contains(Field.FIELD_STEPS)
                            ? dp.getValue(Field.FIELD_STEPS).asInt() : 0;
                    final float distance = dp.getValue(Field.FIELD_DISTANCE).asFloat();
                    training = new WalkingTraining(startMillis, (long) distance, steps, endMillis);
                }
                break;
            case "biking":
                if (dp.getDataType().getFields().contains(Field.FIELD_DISTANCE)) {
                    final float distance = dp.getValue(Field.FIELD_DISTANCE).asFloat();
                    training = new CyclingTraining(startMillis, (long) distance, endMillis);
                }
                break;
        }
        return training;
    }
}
