package ankokovin.fullstacktest.WebServer.Models;


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

}
