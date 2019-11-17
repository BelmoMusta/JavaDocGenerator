package musta.belmo.javacodegenerator.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class MustaLogger {

    /**
     * The {@link #logger} attribute.
     */
    Logger logger;

    /**
     * The MustaLogger class constructor.
     *
     * @param cls{@link Class}
     */
    public MustaLogger(Class cls) {
        logger = LoggerFactory.getLogger(cls);
    }

    /**
     * @return Attribut {@link #name}
     */
    public String getName() {
        return logger.getName();
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void trace(String s, Throwable throwable) {
        logger.trace(s, throwable);
    }

    /**
     * @return Attribut {@link #warnEnabled}
     */
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     */
    public void info(String s) {
        logger.info(s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void error(Marker marker, String s, Object o) {
        logger.error(marker, s, o);
    }

    /**
     * @param marker {@link Marker}
     * @return Attribut {@link #traceEnabled}
     */
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void debug(Marker marker, String s, Object... objects) {
        logger.debug(marker, s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void info(String s, Object o, Object o1) {
        logger.info(s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     */
    public void info(Marker marker, String s) {
        logger.info(marker, s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void warn(Marker marker, String s, Object o) {
        logger.warn(marker, s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void trace(String s, Object... objects) {
        logger.trace(s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void error(String s, Object... objects) {
        logger.error(s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void warn(String s, Object o) {
        logger.warn(s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void debug(String s, Object o, Object o1) {
        logger.debug(s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void trace(Marker marker, String s, Object o) {
        logger.trace(marker, s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void trace(String s, Object o, Object o1) {
        logger.trace(s, o, o1);
    }

    /**
     * @return Attribut {@link #debugEnabled}
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     */
    public void debug(Marker marker, String s) {
        logger.debug(marker, s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void debug(Marker marker, String s, Throwable throwable) {
        logger.debug(marker, s, throwable);
    }

    /**
     * @return Attribut {@link #traceEnabled}
     */
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void trace(Marker marker, String s, Throwable throwable) {
        logger.trace(marker, s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void trace(Marker marker, String s, Object... objects) {
        logger.trace(marker, s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void warn(String s, Throwable throwable) {
        logger.warn(s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     */
    public void trace(Marker marker, String s) {
        logger.trace(marker, s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void error(Marker marker, String s, Object... objects) {
        logger.error(marker, s, objects);
    }

    /**
     * @param marker {@link Marker}
     * @return Attribut {@link #infoEnabled}
     */
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void warn(String s, Object o, Object o1) {
        logger.warn(s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void warn(Marker marker, String s, Throwable throwable) {
        logger.warn(marker, s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void error(Marker marker, String s, Throwable throwable) {
        logger.error(marker, s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void trace(Marker marker, String s, Object o, Object o1) {
        logger.trace(marker, s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void info(String s, Object... objects) {
        logger.info(s, objects);
    }

    /**
     * @return Attribut {@link #infoEnabled}
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void info(Marker marker, String s, Object o) {
        logger.info(marker, s, o);
    }

    /**
     * @return Attribut {@link #errorEnabled}
     */
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void debug(Marker marker, String s, Object o) {
        logger.debug(marker, s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void info(Marker marker, String s, Object o, Object o1) {
        logger.info(marker, s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     */
    public void warn(String s) {
        logger.warn(s);
    }

    /**
     * @param marker {@link Marker}
     * @return Attribut {@link #warnEnabled}
     */
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     */
    public void error(String s) {
        logger.error(s);
    }

    /**
     * @param marker {@link Marker}
     * @return Attribut {@link #errorEnabled}
     */
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void trace(String s, Object o) {
        logger.trace(s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     */
    public void error(Marker marker, String s) {
        logger.error(marker, s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void warn(Marker marker, String s, Object o, Object o1) {
        logger.warn(marker, s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void debug(String s, Object o) {
        logger.debug(s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void debug(String s, Throwable throwable) {
        logger.debug(s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void error(String s, Object o, Object o1) {
        logger.error(s, o, o1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     */
    public void debug(String s) {
        logger.debug(s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void warn(String s, Object... objects) {
        logger.warn(s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     */
    public void trace(String s) {
        logger.trace(s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void info(Marker marker, String s, Object... objects) {
        logger.info(marker, s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void warn(Marker marker, String s, Object... objects) {
        logger.warn(marker, s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void debug(Marker marker, String s, Object o, Object o1) {
        logger.debug(marker, s, o, o1);
    }

    /**
     * @param marker {@link Marker}
     * @return Attribut {@link #debugEnabled}
     */
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void info(Marker marker, String s, Throwable throwable) {
        logger.info(marker, s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     */
    public void warn(Marker marker, String s) {
        logger.warn(marker, s);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void info(String s, Throwable throwable) {
        logger.info(s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void error(String s, Object o) {
        logger.error(s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param objects {@link Object}
     */
    public void debug(String s, Object... objects) {
        logger.debug(s, objects);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param o {@link Object}
     */
    public void info(String s, Object o) {
        logger.info(s, o);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param s {@link String}
     * @param throwable {@link Throwable}
     */
    public void error(String s, Throwable throwable) {
        logger.error(s, throwable);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param marker {@link Marker}
     * @param s {@link String}
     * @param o {@link Object}
     * @param o1 {@link Object}
     */
    public void error(Marker marker, String s, Object o, Object o1) {
        logger.error(marker, s, o, o1);
    }

    /**
     * Log current method
     *
     * @param level {@link Level}
     * @param objects {@link Object}
     */
    public void logCurrentMethod(Level level, Object... objects) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length >= 3) {
            StackTraceElement stackTraceElement = stackTrace[2];
            List<String> strings = Collections.nCopies(objects.length, "{}");
            String str = "Method : %s";
            if (objects.length > 0) {
                str = str.concat(" with values ");
            }
            String join = String.join(",", strings);
            StringBuilder strBuilder = new StringBuilder().append(str).append(join);
            String methodName = String.format(strBuilder.toString(), stackTraceElement.getMethodName());
            if (logger == null) {
                logger = LoggerFactory.getLogger(stackTraceElement.getClassName());
            }
            switch(level) {
                case INFO:
                    info(methodName, objects);
                    break;
                case SEVERE:
                    error(methodName, objects);
                    break;
                case WARNING:
                    warn(methodName, objects);
                    break;
                case DEBUG:
                    debug(methodName, objects);
                    break;
                default:
                    break;
            }
        }
    }
}
