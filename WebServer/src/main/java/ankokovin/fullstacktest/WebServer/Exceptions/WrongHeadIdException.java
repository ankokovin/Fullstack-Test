package ankokovin.fullstacktest.WebServer.Exceptions;

public class WrongHeadIdException extends Exception {

    public Integer id;

    public WrongHeadIdException(Integer id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
    public WrongHeadIdException(Integer id, String message) { this(id,message,null); }
    public WrongHeadIdException(Integer id) { this(id,null,null); }
}
