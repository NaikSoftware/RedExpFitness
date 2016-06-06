package redexp.ua.redexp.viewmodel;

import android.content.Context;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import redexp.ua.redexp.data.DataManager;
import redexp.ua.redexp.data.DataProvider;
import redexp.ua.redexp.data.Recommendation;
import redexp.ua.redexp.model.Training;
import redexp.ua.redexp.model.ProgressModel;
import redexp.ua.redexp.model.TrainingType;
import rx.Observable;

/**
 * Created on 27.03.16.
 */
public class StatsViewModel extends BaseViewModel {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.shortTime();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.mediumDate();

    private Callback mCallback;
    private DataProvider mDataProvider;

    public StatsViewModel(Context context, TrainingType trainingType, Callback callback) {
        super(context);
        mCallback = callback;
        mDataProvider = DataManager.getDataProvider(trainingType);
        subscribeProgress();
        subscribeRecommendation();
    }

    private void subscribeProgress() {
        mCallback.showProgressBar();
        mDataProvider.getTrainings().subscribe(trainings -> {
            if (mCallback == null) return;

            long maxDistance = 0, distance = 0;

            for (int i = 0; i < trainings.size(); i++) {
                distance = trainings.get(i).getDistance();
                if (distance > maxDistance) maxDistance = distance;
            }

            long startTime = 0, endTime = 0;
            long progress = 0;
            Date currentDate = null;
            final List<Float> values = new ArrayList<>();
            final List<String> titles = new ArrayList<>();
            long dayDistance = 0;
            long maxDayDistance = 0;

            for (int i = 0; i < trainings.size(); i++) {
                final Training training = trainings.get(i);
                startTime = training.getStartTime();
                endTime = training.getEndTime();
                distance = training.getDistance();
                progress = distance * 100 / maxDistance;

                Date date = new LocalDate(startTime).toDate();
                if (!date.equals(currentDate)) {
                    mCallback.addProgressHeader(DATE_FORMATTER.print(startTime));
                    if (currentDate != null) {
                        titles.add(DateTimeFormat.shortDate().print(currentDate.getTime()));
                        values.add((float) dayDistance);
                        if (dayDistance > maxDayDistance) maxDayDistance = dayDistance;
                    }
                    currentDate = date;
                    dayDistance = distance;
                } else {
                    dayDistance += distance;
                }

                mCallback.addProgressLine(new ProgressModel(
                        TIME_FORMATTER.print(startTime) + " - " + TIME_FORMATTER.print(endTime),
                        formatDistance(distance),
                        (int) progress));
            }
            if (currentDate != null) {
                titles.add(DateTimeFormat.shortDate().print(startTime));
                values.add((float) dayDistance);
            }
            mCallback.addGraphics(values, titles, (int) maxDayDistance);
            mCallback.hideProgressBar();

        }, throwable -> {
            throwable.printStackTrace();
            if (mCallback != null) mCallback.hideProgressBar();
        });
    }

    private String formatDistance(long distance) {
        if (distance < 100) return distance + " m";
        else return (distance / 1000) + "." + ((distance % 1000) / 100) + " km";
    }

    private void subscribeRecommendation() {
        mDataProvider.getRecommendations()
                .filter(r -> mCallback != null)
                .flatMap(Observable::from)
                .map(Recommendation::getRecommendation)
                .subscribe(mCallback::addRecommendation);
    }

    @Override
    public void onDestroy() {
        mDataProvider.destroy();
        mCallback = null;
        super.onDestroy();
    }

    public interface Callback {

        void addProgressLine(ProgressModel history);

        void addRecommendation(String recommendation);

        void addGraphics(List<Float> values, List<String> strings, int maxValue);

        void addProgressHeader(String header);

        void showProgressBar();

        void hideProgressBar();
    }
}
