package org.exercise;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZKManagerImpl zkManager = new ZKManagerImpl();
        String path = "/a";
        zkManager.createWatcher(path);
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if (input.equals("t")) {
                    try {
                        zkManager.printTree(path);
                    } catch (InterruptedException | KeeperException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
        while (true) {
        }
    }
}