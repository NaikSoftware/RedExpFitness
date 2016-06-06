package redexp.ua.redexp.model;

/**
 * Created by naik on 02.06.16.
 */
public class ProgressModel implements Model {

    private final String date;
    private final String distance;
    private final int value;

    public ProgressModel(String date, String distance, int value) {
        this.value = value;
        this.date = date;
        this.distance = distance;
    }

    public int getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }

    public String getDistance() {
        return distance;
    }
}
