package redexp.ua.redexp.view.stats;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;

import java.util.Collections;
import java.util.List;

import redexp.ua.redexp.R;
import redexp.ua.redexp.databinding.ItemProgressBinding;
import redexp.ua.redexp.databinding.ItemProgressCategoryBinding;
import redexp.ua.redexp.databinding.ViewStatisticsBinding;
import redexp.ua.redexp.model.ProgressModel;
import redexp.ua.redexp.model.TrainingType;
import redexp.ua.redexp.util.ProgressBarController;
import redexp.ua.redexp.view.ChildActivity;
import redexp.ua.redexp.view.session.SessionFragment;
import redexp.ua.redexp.viewmodel.StatsViewModel;

/**
 * Created on 3/26/2016.
 */
public class StatsFragment extends Fragment implements StatsViewModel.Callback {

    public static final String ARG_TYPE = "arg_training_type";

    private ViewStatisticsBinding mBinding;
    private StatsViewModel mViewModel;
    private LineGraphCard mLineGraphCard;
    private LayoutInflater mLayoutInflater;
    private TrainingType mTrainingType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getContext());
        mTrainingType = TrainingType.valueOf(getArguments().getString(ARG_TYPE));
        mViewModel = new StatsViewModel(getContext(), mTrainingType, this);
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle state) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.view_statistics, container, false);
        mBinding.fab.setOnClickListener(this::openNewSession);
        mBinding.setTrainingType(mTrainingType);

        Toolbar toolbar = mBinding.toolbar;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(getString(mTrainingType.getName()) + " "
                + getString(R.string.statistic));

        return mBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        mViewModel.onDestroy();
        super.onDestroy();
    }

    private void openNewSession(final View view) {
        ChildActivity.start(getContext(), SessionFragment.class, null);
    }

    @Override
    public void addProgressLine(ProgressModel history) {
        ItemProgressBinding progress = ItemProgressBinding.inflate(mLayoutInflater);
        progress.setModel(history);
        mBinding.historyLayout.addView(progress.getRoot());
    }

    @Override
    public void addProgressHeader(String headerText) {
        ItemProgressCategoryBinding header = ItemProgressCategoryBinding.inflate(mLayoutInflater);
        header.setTitle(headerText);
        mBinding.historyLayout.addView(header.getRoot());
    }

    @Override
    public void showProgressBar() {
        ProgressBarController.showProgressDialog(getContext());
    }

    @Override
    public void hideProgressBar() {
        ProgressBarController.hideProgressDialog();
    }

    @Override
    public void addRecommendation(final String recommendation) {
        TextView text = new TextView(getContext());
        int padding16dp = (int) Tools.fromDpToPx(16);
        text.setPadding(padding16dp, padding16dp, padding16dp, 0);
        text.setText(recommendation);
        mBinding.layoutRecommendation.addView(text);
    }

    @Override
    public void addGraphics(final List<Float> values, final List<String> strings, int maxValue) {
        if (values.isEmpty() || strings.isEmpty()) return;
        Collections.reverse(values);
        Collections.reverse(strings);
        float[][] points = new float[2][values.size()];
        for (int i = 0; i < values.size(); i++) {
            points[0][i] = values.get(i);
            points[1][i] = i;
        }

        new LineGraphCard(mBinding.chart1,
                getContext(), points, strings.toArray(new String[]{}), maxValue).show();
    }
}
