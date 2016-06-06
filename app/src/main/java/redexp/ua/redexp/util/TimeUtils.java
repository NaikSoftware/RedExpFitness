package redexp.ua.redexp.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Created on 27.03.16.
 */
public class TimeUtils {

    public static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .appendDays().appendSuffix(" day ", " days ")
            .appendHours().appendSuffix(" hour ", " hours ")
            .appendMinutes().appendSuffix(" minute ", " minutes ")
            .appendSeconds().appendSuffix(" s ")
            .printZeroRarelyLast().toFormatter();

    public static String formatDurationBetween(DateTime from, DateTime to) {
        Period period = new Period(from.toInstant(), to.toInstant(), PeriodType.dayTime());
        return PERIOD_FORMATTER.print(period).trim();
    }

    public static String formatDurationBetween(long fromMillis, long toMillis) {
        Period period = new Period(fromMillis, toMillis, PeriodType.dayTime());
        return PERIOD_FORMATTER.print(period).trim();
    }
}
