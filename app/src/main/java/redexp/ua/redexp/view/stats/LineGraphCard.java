package redexp.ua.redexp.view.stats;

import android.content.Context;
import android.graphics.Color;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;

import redexp.ua.redexp.R;


public class LineGraphCard {

    private final LineChartView mChart;
    private final Context mContext;

    private String[] mLabels;
    private float[][] mValues;
    private int mMaxValue;

    private Tooltip mTip;

    public LineGraphCard(LineChartView chart, Context context, float[][] floats, String[] labels, int maxValue) {
        mContext = context;
        mChart = chart;
        mValues = floats;
        mLabels = labels;
        mMaxValue = maxValue;
    }

    public void show() {
        // Tooltip
        mTip = new Tooltip(mContext, R.layout.linechart_three_tooltip, R.id.value);

        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(65), (int) Tools.fromDpToPx(25));

        mTip.setPivotX(Tools.fromDpToPx(65) / 2);
        mTip.setPivotY(Tools.fromDpToPx(25));

        mChart.setTooltips(mTip);

        // Data
        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
//                .setDashed(new float[]{10f, 10f})
                .beginAt(0);
        mChart.addData(dataset);

        dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#b3b5bb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#ffc755"));
        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(0, mMaxValue)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#33ffff"))
                .setXAxis(false)
                .setYAxis(false);

        Animation anim = new Animation()
                .setEasing(new BounceEase())
                .setEndAction(() -> {
                    mTip.prepare(mChart.getEntriesArea(0).get(0), mValues[0][0]);
                    mChart.showTooltip(mTip, true);
                });

        mChart.show(anim);
    }

}
