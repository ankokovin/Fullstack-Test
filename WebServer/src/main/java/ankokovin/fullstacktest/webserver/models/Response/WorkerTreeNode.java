package ankokovin.fullstacktest.webserver.models.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Дерево работников
 */
public class WorkerTreeNode extends TreeNode<WorkerTreeListElement> {
    public WorkerTreeNode(){super(null);}
    public WorkerTreeNode(WorkerTreeListElement item, List<WorkerTreeNode>children) {
        super(item, children.stream().map(WorkerTreeNode::new).collect(Collectors.toList()));
    }
    public WorkerTreeNode(TreeNode<WorkerTreeListElement> node){super(node.item, node.children);}

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
