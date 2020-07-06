package ankokovin.fullstacktest.WebServer.Models.ErrorResponse;

import java.util.Objects;


public class NoSuchRecordResponse extends ErrorResponse {
    public int id;
    @SuppressWarnings("unused")
    public NoSuchRecordResponse() {
    }

    public NoSuchRecordResponse(int id) {
        super("Запись не найдена");
        this.id = id;
    }
}
