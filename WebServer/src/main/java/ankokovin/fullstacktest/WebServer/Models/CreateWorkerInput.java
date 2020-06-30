package ankokovin.fullstacktest.WebServer.Models;

public class CreateWorkerInput {
    public final String name;
    public final Integer org_id;
    public final Integer head_id;
    public CreateWorkerInput(String name, Integer org_id, Integer head_id) {
        this.name = name;
        this.org_id = org_id;
        this.head_id = head_id;
    }
}
