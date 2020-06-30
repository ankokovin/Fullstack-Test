package ankokovin.fullstacktest.WebServer.Models.ErrorResponse;

import java.util.Objects;

public class WrongHeadIdResponse extends ErrorResponse {
    public int id;
    public WrongHeadIdResponse(){}
    public WrongHeadIdResponse(int id) {
        super("Невозможно указать элемент как родительский");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WrongHeadIdResponse that = (WrongHeadIdResponse) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "WrongHeadIdResponse{" +
                "message='" + message + '\'' +
                "} " + super.toString();
    }
}
