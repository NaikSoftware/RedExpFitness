package redexp.ua.redexp.util.logging;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class LoggerImpl implements Logger {

    private static final String DATE_FORMAT = "MM/dd HH:mm:ss.SSS";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private static final int THREAD_WIDTH = 7;
    private static final int LEVEL_WIDTH = 6;
    private static final int CATEGORY_WIDTH = 30;
    private static final String FORMAT = "%s\t%-" + THREAD_WIDTH + "s\t%-" + LEVEL_WIDTH + "s\t%-" + CATEGORY_WIDTH + "s\t\t%s";

    private static final boolean SYSTEM = false;

    @Override
    public final void error(final String _category, String _msg) {
        if (_msg == null) _msg = "Some Error";
        Log.e(_category, _msg);
        if (SYSTEM) logToSystem("ERROR", _category, _msg);
    }

    @Override
    public final void warn(final String _category, String _msg) {
        if (_msg == null) _msg = "Some Error";
        Log.w(_category, _msg);
        if (SYSTEM) logToSystem("WARN", _category, _msg);
    }

    @Override
    public final void info(final String _category, String _msg) {
        if (_msg == null) _msg = "Some Error";
        Log.i(_category, _msg);
        if (SYSTEM) logToSystem("INFO", _category, _msg);
    }

    @Override
    public final void debug(final String _category, String _msg) {
        if (_msg == null) _msg = "Some Error";
        Log.d(_category, _msg);
        if (SYSTEM) logToSystem("DEBUG", _category, _msg);
    }

    @Override
    public final void trace(final String _category, String _msg) {
        if (_msg == null) _msg = "Some Error";
        Log.d(_category, _msg);
        if (SYSTEM) logToSystem("TRACE", _category, _msg);
    }

    private final void logToSystem(final String _level, final String _category, String _msg) {
        if (_msg == null) _msg = "Some Error";
        final String date = dateFormat.format(new Date(System.currentTimeMillis()));
        String thread = Thread.currentThread().getName();
        thread = thread.length() <= THREAD_WIDTH ? thread : thread.substring(0, THREAD_WIDTH);
        System.out.println(String.format(FORMAT, date, thread, _level, _category, addLeadingSpaces(_msg)));
    }

    private final String addLeadingSpaces(final String _str) {
        if (!_str.contains("\n")) return _str;
        return _str.replaceAll("\\n", "\n" + String.format("%" + (DATE_FORMAT.length() + THREAD_WIDTH + LEVEL_WIDTH + CATEGORY_WIDTH) + "s\t\t\t", ""));
    }

}
