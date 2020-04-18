package com.dron.lightconfig.suppliers;

import com.dron.lightconfig.suppliers.exceptions.CircularDependencyAdditionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Property references tree
 *
 * @author Vasiliy Dronov
 */
public class ReferenceTree {

    /**
     * Root of the tree
     */
    private ReferenceTreeNode root;

    /**
     * Index for easy addition of child nodes
     */
    private Map<String, ReferenceTreeNode> index;

    /**
     * Map: node key -> set of paths to this node from root
     */
    private Map<String, Set<TreePath>> paths;

    /**
     * @param rootPropertyKey   root node property key
     * @param rootPropertyValue root node property value
     */
    public ReferenceTree(String rootPropertyKey, String rootPropertyValue) {
        this.root = new ReferenceTreeNode(rootPropertyKey, rootPropertyValue, new ArrayList<>());
        index = new HashMap<>();
        index.put(rootPropertyKey, root);
        paths = new HashMap<>();
        paths.put(root.getKey(), Collections.singleton(new TreePath().append(root.getKey())));
    }

    /**
     * Adds set of keys referenced by parentKey
     *
     * @param parentKey      parent key
     * @param referencedKeys referenced child keys
     */
    public void addReferencedKeys(String parentKey, Collection<String> referencedKeys) {
        ReferenceTreeNode referenceTreeNode = getNode(parentKey);

        List<ReferenceTreeNode> children = referenceTreeNode.getChildren();

        Set<TreePath> parentPaths = paths.get(parentKey);

        referencedKeys.forEach(key -> {
            ReferenceTreeNode referenceNode = index.get(key);
            if (referenceNode == null) {
                referenceNode = new ReferenceTreeNode(key, null);
            }
            Optional<TreePath> suspiciousPath = parentPaths.stream().filter(path -> path.contains(key)).findFirst();
            if (suspiciousPath.isPresent()) {
                throw new CircularDependencyAdditionException(parentKey, key, suspiciousPath.get());
            }

            Set<TreePath> parentExtendedPaths = parentPaths.stream().map(path -> path.append(parentKey)).collect(Collectors.toSet());

            Set<TreePath> newNodePaths = paths.get(key);
            if (newNodePaths == null) {
                newNodePaths = parentExtendedPaths;
            } else {
                newNodePaths.addAll(parentExtendedPaths);
            }

            paths.put(key, newNodePaths);
            index.put(key, referenceNode);
            children.add(referenceNode);
        });
    }

    /**
     * Gets node from index with validation
     */
    private ReferenceTreeNode getNode(String parentKey) {
        ReferenceTreeNode referenceTreeNode = index.get(parentKey);
        if (referenceTreeNode == null) {
            throw new IllegalStateException("Node for key:" + parentKey + " is absent");
        }
        return referenceTreeNode;
    }

    /**
     * Iterates through the tree in the order: parent , child1, child2 ...
     *
     * @param visitor visitor to process each node
     */
    private void iterateParentFirst(Consumer<ReferenceTreeNode> visitor) {
        Stack<ReferenceTreeNode> stack = new Stack<>();
        stack.push(root);
        Set<String> visited = new HashSet<>();
        while (!stack.isEmpty()) {
            ReferenceTreeNode currentNode = stack.pop();
            if (visited.contains(currentNode.getKey())) {
                continue;
            }
            visitor.accept(currentNode);
            visited.add(currentNode.getKey());
            currentNode.getChildren().forEach(child -> {
                if (!visited.contains(child.getKey())) {
                    stack.push(child);
                }
            });
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Tree:\n");
        iterateParentFirst(node -> {
            stringBuilder.append(node.getKey()).append(" -> ");
            boolean first = true;
            for (ReferenceTreeNode child : node.getChildren()) {
                if (!first) {
                    stringBuilder.append(',');
                } else {
                    first = false;
                }
                stringBuilder.append(child.getKey());
            }
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

    public void setNodeValue(String key, String value) {
        ReferenceTreeNode node = getNode(key);
        node.setValue(value);
    }

    /**
     * Calculates the final node root node value using child nodes values
     *
     * @param prefix prefix for the placeholders to replace them with the calculated values
     * @param suffix suffix for the placeholders to replace them with the calculated values
     * @return root node value
     */
    public String calculateValue(String prefix, String suffix) {
        return calculateNodeValue(root, prefix, suffix);
    }

    /**
     * Handles the particular node - children are handled first
     *
     * @param node   particular tree node
     * @param prefix prefix for the placeholders to replace them with the calculated values
     * @param suffix suffix for the placeholders to replace them with the calculated values
     * @return calculated node value
     */
    private String calculateNodeValue(ReferenceTreeNode node, String prefix, String suffix) {
        List<ReferenceTreeNode> children = node.getChildren();
        String nodeValue = node.getValue();
        if (children.isEmpty()) {
            return nodeValue;
        }
        for (ReferenceTreeNode child : children) {
            String calculatedValue = calculateNodeValue(child, prefix, suffix);
            String childPlaceholder = prefix + child.getKey() + suffix;
            nodeValue = nodeValue.replace(childPlaceholder, calculatedValue);
        }
        return nodeValue;
    }
}
