package com.vdronov.lightconfig.suppliers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vasiliy Dronov
 */
public class ReferenceTreeNode {

    /**
     * Current property key
     */
    private String key;

    /**
     * Current property value
     */
    private String value;

    /**
     * References to the dependent keys
     */
    private final List<ReferenceTreeNode> children;

    public ReferenceTreeNode(String key, String value) {
        this.key = key;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public ReferenceTreeNode(String key, String value, List<ReferenceTreeNode> children) {
        this.key = key;
        this.value = value;
        this.children = children;
    }

    /**
     * Gets References to the dependent keys.
     *
     * @return Value of References to the dependent keys.
     */
    public List<ReferenceTreeNode> getChildren() {
        return children;
    }


    /**
     * Gets Current rroperty ket.
     *
     * @return Value of Current rroperty ket.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets Current property value.
     *
     * @return Value of Current property value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets new Current property value.
     *
     * @param value New value of Current property value.
     */
    public void setValue(String value) {
        this.value = value;
    }


}
