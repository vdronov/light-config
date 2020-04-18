package com.vdronov.lightconfig.suppliers.exceptions;

import com.vdronov.lightconfig.suppliers.TreePath;

/**
 * @author Vasiliy Dronov
 */
public class CircularDependencyAdditionException extends IllegalStateException {

    /**
     * Parent node key
     */
    private final String parentKey;

    /**
     * Child node key
     */
    private final String childKey;

    /**
     * Path in the tree where will be circle
     */
    private final TreePath treePath;

    public CircularDependencyAdditionException(String parentKey, String childKey, TreePath treePath) {
        super("Circular dependency found. New edge: '" + parentKey + "' -> '" + childKey + "' will lead to circle.Path:" + treePath);
        this.parentKey = parentKey;
        this.childKey = childKey;
        this.treePath = treePath;
    }


    /**
     * Gets Child node key.
     *
     * @return Value of Child node key.
     */
    public String getChildKey() {
        return childKey;
    }

    /**
     * Gets Path in the tree where will be circle.
     *
     * @return Value of Path in the tree where will be circle.
     */
    public TreePath getTreePath() {
        return treePath;
    }

    /**
     * Gets Parent node key.
     *
     * @return Value of Parent node key.
     */
    public String getParentKey() {
        return parentKey;
    }
}
