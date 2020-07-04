package ankokovin.fullstacktest.WebServer.Models;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;

import java.util.List;
import java.util.stream.Collectors;

public class OrganizationTreeNode extends TreeNode<Organization> {
    public OrganizationTreeNode(){super(null);}
    public OrganizationTreeNode(TreeNode<Organization> node){super(node.item, node.children);}
    public OrganizationTreeNode(Organization item, List<OrganizationTreeNode> children) {
        super(item, children.stream().map(OrganizationTreeNode::new).collect(Collectors.toList()));
    }
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
