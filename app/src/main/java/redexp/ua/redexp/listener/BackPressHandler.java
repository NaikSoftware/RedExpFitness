package redexp.ua.redexp.listener;

public interface BackPressHandler {
    /**
     * @return Return false to allow normal back processing to proceed, true to consume it here.
     */
    boolean onBackPressed();
}
