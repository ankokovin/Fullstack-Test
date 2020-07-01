package ankokovin.fullstacktest.WebServer.Models;

public class WorkerTreeNode extends TreeNode<WorkerTreeListElement> {
    public WorkerTreeNode(){super(null);}
    public WorkerTreeNode(WorkerTreeListElement item){super(item);}
    public WorkerTreeNode(TreeNode<WorkerTreeListElement> node){super(node.item, node.children);}

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
