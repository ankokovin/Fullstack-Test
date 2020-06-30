package ankokovin.fullstacktest.WebServer.Models;

public class UpdateWorkerInput extends CreateWorkerInput {
    public final Integer id;
    public UpdateWorkerInput(Integer id, String name, Integer org_id, Integer head_id) {
        super(name, org_id, head_id);
        this.id = id;
    }
}
