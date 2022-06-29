package com.atguigu.zk.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author Zz
 * @create 2022-06-28 10:25
 */
public class DistriubuteServer {


    private String connectString = "hadoop102:2181,hadoop103:2181,hadoop103:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        //1获取zk链接

        DistriubuteServer server = new DistriubuteServer();
        server.getConnect();
        //2注册服务器到zk
        server.regist(args[0]);

        //3 启动业务逻辑（睡觉）
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void regist(String hostname) throws InterruptedException, KeeperException {
        zk.create("/servers/"+hostname,hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + "is noline");
    }

    private void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });

    }


}
