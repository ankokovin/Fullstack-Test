package ankokovin.fullstacktest.WebServer.Models;

public class CreateOrganizationInput {
    public String name;
    public Integer org_id;

    public CreateOrganizationInput() {

    }

    public CreateOrganizationInput(String name, Integer org_id) {
        this.name = name;
        this.org_id = org_id;
    }
}
