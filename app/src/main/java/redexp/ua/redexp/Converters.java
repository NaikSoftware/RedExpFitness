package redexp.ua.redexp;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

/**
 * Created on 3/27/2016.
 */
public class Converters {

    @BindingAdapter({"animateToSpeed"})
    public static void setSpeedToProgressBarView(final CircularProgressBar progressBar, final String progress) {
        if (progressBar == null || progress == null || progress.isEmpty()) return;
        final float maxSpeed = 30F;
        final float speed = Float.parseFloat(progress);
        float result = speed / maxSpeed;
        result = result * 100;
        progressBar.setColor(generateColorForSpeedometer(speed, maxSpeed));
        progressBar.setProgressWithAnimation(result / 2);
    }

    public static int generateColorForSpeedometer(final float current, final float max) {
//        255 - max yellow
//          0 - max red
        final float percent = current / max;
        //do some magic with digits
        final int color = Color.argb(255, 255, (int) (200 - (255 * percent)), 0);
        return color;
    }
}
