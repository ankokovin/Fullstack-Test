package ankokovin.fullstacktest.webserver.models.response;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Дерево выходных данных
 *
 * @param <T> хранимый тип
 */
@SuppressWarnings("unused")
public class TreeNode<T> {
    /**
     * Хранимый элемент
     */
    public final T item;
    /**
     * Потомки данной вершины
     */
    @SuppressWarnings("unused")
    public List<TreeNode<T>> children;

    /**
     * Вершина выходных данных
     *
     * @param item хранимый элемент
     */
    public TreeNode(T item) {
        this.item = item;
        this.children = new LinkedList<>();
    }

    /**
     * Под-дерево выходных данных
     *
     * @param item     Хранимый элемент корневой вершины
     * @param children Потомки корневой вершины
     */
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

