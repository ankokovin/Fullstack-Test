package ankokovin.fullstacktest.webserver.models.ErrorResponse;

public class SameNameResponse extends ErrorResponse {
    public String name;

    public SameNameResponse() {
    }

    public SameNameResponse(String name) {
        super("Запись с таким именем уже сущесвует");
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SameNameResponse that = (SameNameResponse) o;
        return name.equals(that.name);
    }


    @Override
    public String toString() {
        return "SameNameResponse{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
