package redexp.ua.redexp.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import redexp.ua.redexp.R;
import redexp.ua.redexp.databinding.ActivityMainBinding;
import redexp.ua.redexp.model.TrainingType;
import redexp.ua.redexp.view.stats.StatsFragment;
import redexp.ua.redexp.viewmodel.MainViewModel;

/**
 * Created on 20.03.16.
 */
public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainViewModel getViewModel() {
        mViewModel = new MainViewModel(this);
        return mViewModel;
    }

    public void onClickWalking(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(StatsFragment.ARG_TYPE, TrainingType.WALKING.name());
        ChildActivity.start(this, StatsFragment.class, bundle);
    }

    public void onClickRunning(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(StatsFragment.ARG_TYPE, TrainingType.RUNNING.name());
        ChildActivity.start(this, StatsFragment.class, bundle);
    }

    public void onClickCycling(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(StatsFragment.ARG_TYPE, TrainingType.CYCLING.name());
        ChildActivity.start(this, StatsFragment.class, bundle);
    }
}
