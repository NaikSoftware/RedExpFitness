package redexp.ua.redexp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created on 3/26/2016.
 */
public class TrackPoint extends RealmObject {

    private String trainingType;
    @PrimaryKey
    private long timestamp;
    private float value;

    public TrackPoint() {
    }

    public TrackPoint(String trainingType, long timestamp, float value) {
        this.trainingType = trainingType;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(final float value) {
        this.value = value;
    }
}
