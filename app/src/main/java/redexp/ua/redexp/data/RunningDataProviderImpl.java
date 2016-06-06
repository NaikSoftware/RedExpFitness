package redexp.ua.redexp.data;

import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;
import redexp.ua.redexp.model.RunningRecommendation;
import redexp.ua.redexp.model.RunningTraining;
import redexp.ua.redexp.model.Training;
import rx.Observable;

/**
 * Created on 3/26/2016.
 */
class RunningDataProviderImpl implements DataProvider {

    private final Realm mRealm;

    public RunningDataProviderImpl() {
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

        return mRealm.where(RunningTraining.class)
                .greaterThan("startTime", from)
                .findAllAsync().asObservable()
                .filter(r -> !r.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    @Override
    public void destroy() {
        mRealm.close();
    }
}
