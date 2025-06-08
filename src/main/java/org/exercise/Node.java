package org.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private final String name;
    private final List<Node> children;

    public Node(String name) {
        this.name = name;
        children = new ArrayList<>();
    }

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChildren(Node node) {
        children.add(node);
    }


    public String getName() {
        return name;
    }
}
