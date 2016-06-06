package redexp.ua.redexp.util.logging;

public interface Logger {

    void error(String _category, final String _msg);
    void warn(String _category, final String _msg);
    void info(String _category, final String _msg);
    void debug(String _category, final String _msg);
    void trace(String _category, final String _msg);

}
