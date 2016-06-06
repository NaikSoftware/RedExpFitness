package redexp.ua.redexp.model;

import android.text.format.DateUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import redexp.ua.redexp.data.Recommendation;

/**
 * Created on 26.03.2016.
 */
public class RunningRecommendation extends RealmObject implements Recommendation {

    @PrimaryKey
    String distance;
    long time;

    public RunningRecommendation() {}

    public RunningRecommendation(String distance, long time) {
        this.distance = distance;
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return getRecommendation();
    }

    @Override
    public String getRecommendation() {
        return "Run " + distance + " m for " + DateUtils.formatElapsedTime(time);
    }
}
