package ankokovin.fullstacktest.WebServer.Exceptions;

public class UnexpectedException extends BaseException {
    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedException(Exception cause) {
        this(null, cause);
    }

    public UnexpectedException(String message) {
        this(message, null);
    }
}
