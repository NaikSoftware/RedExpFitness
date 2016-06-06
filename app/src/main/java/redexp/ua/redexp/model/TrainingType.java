package redexp.ua.redexp.model;

import redexp.ua.redexp.R;

/**
 * Created on 26.03.2016.
 */
public enum TrainingType {

    RUNNING(R.string.running),
    FAKE(R.string.app_name),
    WALKING(R.string.walking),
    CYCLING(R.string.cycling);

    private int name;

    TrainingType(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }
}
