package ankokovin.fullstacktest.WebServer.Exceptions;

public class SameNameException extends Exception {
    public String name;

    public SameNameException(String name, String message, Throwable cause) {
        super(message, cause);
        this.name = name;
    }
    public SameNameException(String name, String message) { this(name,message,null); }
    public SameNameException(String name) { this(name,null,null); }
}
