package org.exercise;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class ZKManagerImpl implements ZKManager {
    private static ZooKeeper zkeeper;
    private final ProcessBuilder processBuilderOpen = new ProcessBuilder("open", "-a", "Safari", "https://www.informatyka.agh.edu.pl/pl/");
    private final ProcessBuilder processBuilderClose = new ProcessBuilder("osascript", "-e", "tell application \"Safari\" to quit");

    public ZKManagerImpl() throws IOException, InterruptedException {
        initialize();
    }


    private void initialize() throws IOException, InterruptedException {
        ZKConnection zkConnection = new ZKConnection();
        zkeeper = zkConnection.connect("localhost");
    }

//    public void closeConnection() throws InterruptedException {
//        zkConnection.close();
//    }

    private void findChildren(Node node) throws InterruptedException, KeeperException {
        try {
            List<String> children = zkeeper.getChildren(node.getName(), false);
            if (children.isEmpty()) {
                return;
            }
            children.forEach(s ->
            {
                Node childrenNode = new Node(node.getName() + "/" + s);
                node.addChildren(childrenNode);
                try {
                    findChildren(childrenNode);
                } catch (InterruptedException | KeeperException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (KeeperException | InterruptedException e) {
            System.out.println("Maybe " + node.getName() + " doesn't exist");
        }

    }

    public void printTreeLikeTreeCommand(Node node, String prefix, boolean issLastChildren) {
        System.out.println(prefix + (issLastChildren ? "└── " : "├── ") + node.getName());
        List<Node> children = node.getChildren();

        for (int i = 0; i < children.size(); i++) {
            boolean isLast = i == children.size() - 1;
            printTreeLikeTreeCommand(children.get(i), prefix + (issLastChildren ? "    " : "│   "), isLast);
        }
    }

    @Override
    public void printTree(String path) throws InterruptedException, KeeperException {
        Node root = new Node(path);
        findChildren(root);
        printTreeLikeTreeCommand(root, "", true);
    }

    @Override
    public void createWatcher(String path) throws InterruptedException, KeeperException {
        zkeeper.addWatch(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType().equals(Event.EventType.NodeCreated)) {
                    try {
                        System.out.println("----------------------------------------------");
                        System.out.println(MessageFormat.format("{0} was created", watchedEvent.getPath()));
                        System.out.println("----------------------------------------------");
                        if (watchedEvent.getPath().equals(path)) {
                            processBuilderOpen.start();
                        } else {
                            try {
                                int children = zkeeper.getAllChildrenNumber(path);
                                System.out.println("----------------------------------------------");
                                System.out.println(MessageFormat.format("{0} has {1} children", path, children));
                                System.out.println("----------------------------------------------");
                                System.out.println();
                            } catch (KeeperException | InterruptedException e) {
                                System.out.println("----------------------------------------------");
                                System.out.println("Node /a doesn't exist");
                                System.out.println("----------------------------------------------");
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (watchedEvent.getType().equals(Event.EventType.NodeDeleted)) {
                    try {
                        if (watchedEvent.getPath().equals(path)) {
                            processBuilderClose.start();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("----------------------------------------------");
                    System.out.println(MessageFormat.format("{0} was deleted", watchedEvent.getPath()));
                    System.out.println("----------------------------------------------");
                }
                System.out.println(watchedEvent);
            }
        }, AddWatchMode.PERSISTENT_RECURSIVE);
    }
}