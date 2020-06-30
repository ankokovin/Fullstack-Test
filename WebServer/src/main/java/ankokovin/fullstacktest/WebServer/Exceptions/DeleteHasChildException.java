package ankokovin.fullstacktest.WebServer.Exceptions;

public class DeleteHasChildException extends BaseException {
    public Object child;
    public DeleteHasChildException(Object child, String message, Throwable cause) {
        super(message, cause);
        this.child = child;
    }
    public DeleteHasChildException(Object child, Throwable cause) {
        this(child, null, cause);
    }
}
