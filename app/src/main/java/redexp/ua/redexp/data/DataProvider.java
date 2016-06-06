package redexp.ua.redexp.data;

import java.util.List;

import redexp.ua.redexp.listener.Destroyable;
import redexp.ua.redexp.model.Training;
import rx.Observable;

public interface DataProvider extends Destroyable {

    Observable<List<? extends Recommendation>> getRecommendations();

    Observable<List<? extends Training>> getTrainings();

}
