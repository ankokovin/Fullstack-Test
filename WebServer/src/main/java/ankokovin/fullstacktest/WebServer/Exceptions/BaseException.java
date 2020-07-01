package ankokovin.fullstacktest.WebServer.Exceptions;

public abstract class BaseException extends Exception {
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
