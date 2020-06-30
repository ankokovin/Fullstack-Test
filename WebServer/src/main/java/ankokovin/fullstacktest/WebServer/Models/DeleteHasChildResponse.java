package ankokovin.fullstacktest.WebServer.Models;

import java.util.HashMap;
import java.util.Map;

public class DeleteHasChildResponse extends ErrorResponse {
    public Table from;
    public Integer id;
    public DeleteHasChildResponse(){}
    public DeleteHasChildResponse(Integer id, Table from){
        super("Запись имеет дочерние элементы.");
        this.from = from;
        this.id = id;
    }
}
