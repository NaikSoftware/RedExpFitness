package redexp.ua.redexp.data;

import java.util.List;

import redexp.ua.redexp.model.Training;
import rx.Observable;

/**
 * Created on 3/26/2016.
 */
public class FakeDataProvider implements DataProvider {

    @Override
    public Observable<List<? extends Recommendation>> getRecommendations() {
        return Observable.empty();
    }

    @Override
    public Observable<List<? extends Training>> getTrainings() {
        return Observable.empty();
    }

    @Override
    public void destroy() {
    }
}
