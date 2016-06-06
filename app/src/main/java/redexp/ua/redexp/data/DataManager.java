package redexp.ua.redexp.data;

import android.content.Context;

import redexp.ua.redexp.model.TrainingType;

/**
 * Created on 26.03.2016.
 */
public final class DataManager {

    public static DataProvider getDataProvider(TrainingType type) {
        switch (type) {
            case RUNNING:
                return new RunningDataProviderImpl();
            case WALKING:
                return new WalkingDataProviderImpl();
            case CYCLING:
                return new CyclingDataProviderImpl();
        }
        return new FakeDataProvider();
    }

    private DataManager() {}
}
