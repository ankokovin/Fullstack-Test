package ankokovin.fullstacktest.WebServer.Models;


public class OrgListElement {
    public String name;
    public Integer count;
    public OrgListElement(String name, Integer count) {
        if (name == null) {
            // TODO: throw
        }
        if (count == null) {
            // TODO: throw
        }
        this.count = count;
        this.name = name;
    }

}
