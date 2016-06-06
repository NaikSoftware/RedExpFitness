package redexp.ua.redexp.util.logging;

public abstract class LogManager {

    private static final Logger logger = new LoggerImpl();

    public static Logger getLogger() {
        return logger;
    }
}
