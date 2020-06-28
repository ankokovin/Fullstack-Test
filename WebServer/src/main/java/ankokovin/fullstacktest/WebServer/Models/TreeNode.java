package ankokovin.fullstacktest.WebServer.Models;

import java.util.List;

public class TreeNode<T> {
    public List<TreeNode<T>> children;
    public T item;
    public TreeNode(T item) {
        this.item = item;
    }
    public TreeNode(T item, List<TreeNode<T>> children) {
        this(item);
        this.children = children;
    }
}
