package ankokovin.fullstacktest.WebServer.Models;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;

public class OrganizationTreeNode extends TreeNode<Organization> {
    public OrganizationTreeNode(){this(null);}
    public OrganizationTreeNode(Organization item) {
        super(item);
    }
}
