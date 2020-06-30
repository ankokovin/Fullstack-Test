package ankokovin.fullstacktest.WebServer.Models;

import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;

import java.util.HashMap;
import java.util.Map;

public class NoSuchRecordResponse extends ErrorResponse {
    public int id;
    public NoSuchRecordResponse() {}
    public NoSuchRecordResponse(int id) {
        super("Запись не найдена");
        this.id = id;
    }
}
