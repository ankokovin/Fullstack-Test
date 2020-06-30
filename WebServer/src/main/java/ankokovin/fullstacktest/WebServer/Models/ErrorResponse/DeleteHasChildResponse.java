package ankokovin.fullstacktest.WebServer.Models.ErrorResponse;

import ankokovin.fullstacktest.WebServer.Models.Table;

import java.util.Objects;

public class DeleteHasChildResponse extends ErrorResponse {
    public Table from;
    public Integer id;
    public DeleteHasChildResponse(){}
    public DeleteHasChildResponse(Integer id, Table from){
        super("Запись имеет дочерние элементы.");
        this.from = from;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeleteHasChildResponse that = (DeleteHasChildResponse) o;
        return from == that.from &&
                id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, id);
    }

    @Override
    public String toString() {
        return "DeleteHasChildResponse{" +
                "from=" + from +
                ", id=" + id +
                "} " + super.toString();
    }
}
