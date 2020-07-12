package ankokovin.fullstacktest.webserver.models.errorresponse;

import ankokovin.fullstacktest.webserver.models.Table;

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
