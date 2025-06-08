package org.exercise;

import org.apache.zookeeper.KeeperException;

import java.io.UnsupportedEncodingException;

public interface ZKManager {
    void createWatcher(String path) throws InterruptedException, KeeperException;

    void printTree(String path) throws InterruptedException, KeeperException;

}