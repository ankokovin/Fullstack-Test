package ankokovin.fullstacktest.WebServer.Models.ErrorResponse;

import java.util.Objects;

@SuppressWarnings("unused")
public class NoSuchRecordResponse extends ErrorResponse {
    public int id;

    public NoSuchRecordResponse() {
    }

    public NoSuchRecordResponse(int id) {
        super("Запись не найдена");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NoSuchRecordResponse that = (NoSuchRecordResponse) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "NoSuchRecordResponse{" +
                "id=" + id +
                "} " + super.toString();
    }
}
