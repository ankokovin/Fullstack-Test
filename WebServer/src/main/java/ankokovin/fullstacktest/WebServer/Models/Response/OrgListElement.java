package ankokovin.fullstacktest.WebServer.Models.Response;


@SuppressWarnings("unused")
public class OrgListElement {
    public final Integer id;
    public final String name;
    public final Integer count;

    public OrgListElement(int id, String name, Integer count) {
        this.id = id;
        this.count = count;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgListElement that = (OrgListElement) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                count.equals(that.count);
    }
}
