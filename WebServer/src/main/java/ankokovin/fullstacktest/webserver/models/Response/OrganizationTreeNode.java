package ankokovin.fullstacktest.webserver.models.Response;

import ankokovin.fullstacktest.webserver.generated.tables.pojos.Organization;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Дерево организаций
 */
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
