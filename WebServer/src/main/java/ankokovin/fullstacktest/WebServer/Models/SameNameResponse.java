package ankokovin.fullstacktest.WebServer.Models;

import java.util.Map;

public class SameNameResponse extends ErrorResponse {
    public String name;
    public SameNameResponse() {}
    public SameNameResponse(String name) {
        super("Запись с таким именем уже сущесвует");
        this.name = name;
    }
}
