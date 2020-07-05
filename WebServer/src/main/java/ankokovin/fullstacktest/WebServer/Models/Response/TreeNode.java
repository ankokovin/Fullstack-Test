package ankokovin.fullstacktest.WebServer.Models.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class TreeNode<T> {
    public final T item;
    @SuppressWarnings("unused")
    public List<TreeNode<T>> children;

    public TreeNode(T item) {
        this.item = item;
        this.children = new LinkedList<>();
    }

    public TreeNode(T item, List<TreeNode<T>> children) {
        this(item);
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeNode<?>)) return false;
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equals(item, treeNode.item) &&
                Objects.equals(children, treeNode.children);
    }
}

