package ankokovin.fullstacktest.WebServer.Exceptions;

import ankokovin.fullstacktest.WebServer.Models.Table;

public class WrongHeadIdException extends BaseException {

    public Integer id;
    public Table to;

    public WrongHeadIdException(Integer id,Table to, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
        this.to = to;
    }
    public WrongHeadIdException(Integer id,Table to, String message) { this(id,to,message,null); }
    public WrongHeadIdException(Integer id,Table to) { this(id,to,null,null); }


}
