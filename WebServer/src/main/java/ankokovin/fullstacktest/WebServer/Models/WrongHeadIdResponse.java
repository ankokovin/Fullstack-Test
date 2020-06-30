package ankokovin.fullstacktest.WebServer.Models;

import java.util.HashMap;
import java.util.Map;

public class WrongHeadIdResponse extends ErrorResponse {
    public int id;
    public WrongHeadIdResponse(){}
    public WrongHeadIdResponse(int id) {
        super("Невозможно указать элемент как родительский");
        this.id = id;
    }
}
