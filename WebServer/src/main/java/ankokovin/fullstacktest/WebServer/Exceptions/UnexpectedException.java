package ankokovin.fullstacktest.WebServer.Exceptions;

public class UnexpectedException extends BaseException {
    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedException(Exception cause) {
        super(null, cause);
    }

    public UnexpectedException(String message) {
        super(message, null);
    }
}
