package ankokovin.fullstacktest.WebServer.Models;

import java.util.List;

@SuppressWarnings("unused")
public class TreeNode<T> {
    public final T item;
    @SuppressWarnings("unused")
    public List<TreeNode<T>> children;

    public TreeNode(T item) {
        this.item = item;
    }

    public TreeNode(T item, List<TreeNode<T>> children) {
        this(item);
        this.children = children;
    }
}
