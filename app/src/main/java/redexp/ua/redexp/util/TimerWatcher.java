package redexp.ua.redexp.util;

import android.databinding.ObservableField;
import android.os.CountDownTimer;

public class TimerWatcher {

    private final CountDownTimer countDownTimer;
    private final ObservableField<String> observableField;
    private final boolean isDownTimer;
    private final long startTime;
    private final long interval;

    private long timeElapsed = 0;
    private volatile boolean isPaused = true;

    private TimerWatcher(final long from,
                         final long interval,
                         final long startTime,
                         final boolean isDownTimer,
                         final ObservableField<String> observableField) {
        this.isDownTimer = isDownTimer;
        this.startTime = startTime;
        this.observableField = observableField;
        this.interval = interval;
        countDownTimer = new CountDownTimer(from, interval) {
            @Override
            public void onTick(final long millisUntilFinished) {
                handleTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                handleFinish();
            }
        };
        countDownTimer.start();
    }

    public synchronized void start() {
        if (isPaused) {
            isPaused = false;
        } else {
            countDownTimer.start();
        }
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void handleTick(final long millisUntilFinished) {
        if (isPaused) return;
        timeElapsed = timeElapsed + interval;
        if (isDownTimer) {
            String result = "";
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            result = String.format("%d:%02d:%02d", hours, minutes, seconds);
            if (observableField != null) observableField.set(result);
        } else {
            int seconds = (int) (timeElapsed / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            if (observableField != null) {
                if (hours < 0) observableField.set(String.format("%d:%02d:%02d", hours, minutes, seconds));
                else observableField.set(String.format("%02d:%02d", minutes, seconds));
            }
        }
    }

    public void handleFinish() {
        if (observableField != null) observableField.set("WAITING");
    }

    public synchronized void cancel() {
        countDownTimer.cancel();
    }

    public static class Builder {

        public static final long DEFAULTINTERVAL = 1000;

        private long mFrom;
        private long mInterval = DEFAULTINTERVAL;
        private boolean isDownTimer = true;
        private ObservableField<String> mObservableField;

        public Builder up() {
            isDownTimer = false;
            mFrom = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
            return this;
        }

        public Builder down(final long from) {
            isDownTimer = true;
            mFrom = from;
            return this;
        }

        public Builder interval(final long interval) {
            mInterval = interval;
            return this;
        }

        public Builder watchField(final ObservableField<String> field) {
            mObservableField = field;
            return this;
        }

        public TimerWatcher build() {
            return new TimerWatcher(mFrom, mInterval, mFrom, isDownTimer, mObservableField);
        }
    }
}
