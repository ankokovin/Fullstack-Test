package ankokovin.fullstacktest.webserver.exceptions;

/**
 * Исключение, полученное в рамках работы данного приложения
 */
public abstract class BaseException extends Exception {
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
