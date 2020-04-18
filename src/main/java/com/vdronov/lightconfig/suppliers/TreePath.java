package com.vdronov.lightconfig.suppliers;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Path to the property key node in a tree
 *
 * @author Vasiliy Dronov
 */
public class TreePath {

    /**
     * Nested nodes set in order of appearance from root to current node
     */
    private final LinkedHashSet<String> nodes;

    public TreePath() {
        this(new LinkedHashSet<>());
    }

    public TreePath(Set<String> path) {
        if (path instanceof LinkedHashSet) {
            nodes = (LinkedHashSet<String>) path;
        } else {
            nodes = new LinkedHashSet<>(path);
        }
    }

    /**
     * Whether this path contains propertyKey with property key
     *
     * @param propertyKey key of referenced property
     * @return true if contains, false otherwise
     */
    public boolean contains(String propertyKey) {
        return nodes.contains(propertyKey);
    }

    /**
     * Makes new path with the specified node
     *
     * @param propertyKey property key with the node
     * @return path which contains all nodes from this one plus the specified node with key propertyKey
     */
    public TreePath append(String propertyKey) {
        TreePath treePath = new TreePath();
        treePath.nodes.addAll(this.nodes);
        treePath.nodes.add(propertyKey);
        return treePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreePath)) return false;
        TreePath treePath = (TreePath) o;
        return nodes.equals(treePath.nodes);
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
