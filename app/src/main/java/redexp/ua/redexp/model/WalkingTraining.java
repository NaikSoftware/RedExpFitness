package redexp.ua.redexp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created on 26.03.2016.
 */
public class WalkingTraining extends RealmObject implements Training {

    @PrimaryKey
    private long startTime;
    private long distance;
    private long stepCount;
    private long endTime;

    public WalkingTraining() {}

    public WalkingTraining(long startTime, long distance, long stepCount, long trainingTime) {
        this.startTime = startTime;
        this.distance = distance;
        this.stepCount = stepCount;
        this.endTime = trainingTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getDistance() {
        return distance;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    public long getStepCount() {
        return stepCount;
    }
}
