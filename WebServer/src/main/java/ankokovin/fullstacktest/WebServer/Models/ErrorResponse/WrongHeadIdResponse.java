package ankokovin.fullstacktest.WebServer.Models.ErrorResponse;

import ankokovin.fullstacktest.WebServer.Models.Table;

import java.util.Objects;

public class WrongHeadIdResponse extends ErrorResponse {
    public int id;
    public Table to;
    public WrongHeadIdResponse(){}
    public WrongHeadIdResponse(int id, Table to) {
        super("Невозможно указать элемент как родительский");
        this.id = id;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WrongHeadIdResponse that = (WrongHeadIdResponse) o;
        return id == that.id &&
                to == that.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, to);
    }

    @Override
    public String toString() {
        return "WrongHeadIdResponse{" +
                "id=" + id +
                ", to=" + to +
                "} " + super.toString();
    }
}