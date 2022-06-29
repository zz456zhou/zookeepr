package com.atguigu.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * @author Zz
 * @create 2022-06-28 15:49
 */
public class GetConnect {
    public static final String connectString = "hadoop102:2181,hadoop103:2182,hadoop103:2181";
    public static final int sessionTimeout = 2000;

    public static ZooKeeper getConnect() throws IOException {
        ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
        return zooKeeper;
    }


}
