package com.atguigu.zk.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zz
 * @create 2022-06-28 10:43
 */
public class DistributeClient {

    private String connectString = "hadoop102:2181,hadoop103:2182,hadoop103:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws Exception {

        //1获取zk链接
        DistributeClient client = new DistributeClient();
        client.getConnect();

        //2监听/server 下面子节点的增加和删除
        client.getServerList();

        //3 业务逻辑（睡觉）
        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws InterruptedException, KeeperException {

        List<String> children = zk.getChildren("/servers", true);

        ArrayList<String> serves = new ArrayList<>();

        for (String child : children) {

            byte[] data = zk.getData("/servers/" + child, false, null);

            serves.add(new String(data));
        }

        System.out.println(serves);
    }

    private void getConnect() throws IOException {


        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

                try {
                    getServerList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
