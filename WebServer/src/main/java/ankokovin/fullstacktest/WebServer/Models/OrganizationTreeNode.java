package ankokovin.fullstacktest.WebServer.Models;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;

public class OrganizationTreeNode extends TreeNode<Organization> {
    public OrganizationTreeNode(){super(null);}
    public OrganizationTreeNode(Organization item) {
        super(item);
    }
    public OrganizationTreeNode(TreeNode<Organization> node){super(node.item, node.children);}
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
