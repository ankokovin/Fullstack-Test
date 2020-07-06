package ankokovin.fullstacktest.WebServer.Models.ErrorResponse;

import ankokovin.fullstacktest.WebServer.Models.Table;

import java.util.Objects;

public class DeleteHasChildResponse extends ErrorResponse {
    public Table from;
    public Integer id;

    @SuppressWarnings("unused")
    public DeleteHasChildResponse() {
    }

    public DeleteHasChildResponse(Integer id, Table from) {
        super("Запись имеет дочерние элементы.");
        this.from = from;
        this.id = id;
    }
}
