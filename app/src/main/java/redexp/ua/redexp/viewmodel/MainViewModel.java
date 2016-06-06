package redexp.ua.redexp.viewmodel;

import android.content.Context;
import android.view.View;

import org.joda.time.DateTime;

import redexp.ua.redexp.fitness.FitnessManager;
import redexp.ua.redexp.util.logging.LogManager;
import redexp.ua.redexp.util.logging.Logger;

/**
 * Created on 20.03.16.
 */
public class MainViewModel extends BaseViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger();

    public MainViewModel(Context context) {
        super(context);
        FitnessManager.pullTrainings(getContext());
    }

}
