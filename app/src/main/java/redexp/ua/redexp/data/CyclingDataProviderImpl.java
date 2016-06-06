package redexp.ua.redexp.data;

import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import redexp.ua.redexp.model.CyclingTraining;
import redexp.ua.redexp.model.RunningRecommendation;
import redexp.ua.redexp.model.Training;
import rx.Observable;

/**
 * Created on 27.03.16.
 */
public class CyclingDataProviderImpl implements DataProvider {

    private final Realm mRealm;

    public CyclingDataProviderImpl() {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public Observable<List<? extends Recommendation>> getRecommendations() {
        return mRealm.where(RunningRecommendation.class)
                .findAllAsync().asObservable()
                .filter(r -> !r.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    @Override
    public Observable<List<? extends Training>> getTrainings() {
        long from = DateTime.now().minusWeeks(4).getMillis();

        return mRealm.where(CyclingTraining.class)
                .greaterThan("startTime", from)
                .findAllSortedAsync("startTime", Sort.DESCENDING).asObservable()
                .filter(r -> !r.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    @Override
    public void destroy() {
        mRealm.close();
    }
}
