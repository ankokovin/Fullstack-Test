package ankokovin.fullstacktest.WebServer.Models;

import java.util.List;

@SuppressWarnings("unused")
public class TreeNode<T> {
    @SuppressWarnings("unused")
    public List<TreeNode<T>> children;
    public final T item;
    public TreeNode(T item) {
        this.item = item;
    }
    public TreeNode(T item, List<TreeNode<T>> children) {
        this(item);
        this.children = children;
    }
}
