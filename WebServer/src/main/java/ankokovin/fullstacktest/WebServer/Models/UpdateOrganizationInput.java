package ankokovin.fullstacktest.WebServer.Models;

public class UpdateOrganizationInput extends CreateOrganizationInput {
    public Integer id;
    public UpdateOrganizationInput() {
        super();
    }
    public UpdateOrganizationInput(Integer id, String name, Integer head_org_id) {
        super(name, head_org_id);
        this.id = id;
    }
}
