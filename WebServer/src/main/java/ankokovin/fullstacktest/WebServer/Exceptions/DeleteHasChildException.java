package ankokovin.fullstacktest.WebServer.Exceptions;

import ankokovin.fullstacktest.WebServer.Models.Table;

public class DeleteHasChildException extends BaseException {
    public final int id;
    public final Table table;
    public DeleteHasChildException(int id, Table table, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
        this.table = table;
    }
    public DeleteHasChildException(int id, Table table, Throwable cause) {
        this(id, table, null, cause);
    }
    public DeleteHasChildException(int id, Table table) {this(id, table, null);}
}
