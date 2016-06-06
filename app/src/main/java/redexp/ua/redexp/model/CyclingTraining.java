package redexp.ua.redexp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created on 26.03.2016.
 */
public class CyclingTraining extends RealmObject implements Training {

    @PrimaryKey
    private long startTime;
    private long distance;
    private long endTime;

    public CyclingTraining() {}

    public CyclingTraining(long startTime, long distance, long trainingTime) {
        this.startTime = startTime;
        this.distance = distance;
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
}
