package com.atguigu.zk.case2;

import com.atguigu.zk.GetConnect;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Zz
 * @create 2022-06-28 15:43
 */
public class DistributedLock {


    private  String waitPath = "";
    private final ZooKeeper zk;

    public  final String connectString = "hadoop102:2181,hadoop103:2182,hadoop104:2181";
    public  final int sessionTimeout = 2000;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);
    private String currentMode;

    public DistributedLock() throws Exception {

        //获取链接

        // zk = GetConnect.getConnect();

       zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
               @Override
               public void process(WatchedEvent watchedEvent) {
                   //connectLatch 如果连接上zk 可以释放
                   if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                       connectLatch.countDown();
                   }
                   //waitLatch 需要释放
                   if (watchedEvent.getType()==Event.EventType.NodeDeleted&&watchedEvent.getPath().equals(waitPath)){
                       waitLatch.countDown();
                   }
               }
        });
        //等待zk正常连接后，往下走程序
        connectLatch.await();

        //判断根节点/locks是否存在

        Stat stat = zk.exists("/locks", false);

        if (stat==null){
            zk.create("/locks","locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }


    }

    //加锁
    public void zkLock()   {
        //创建对应的临时带序号的节点
        try {
            currentMode = zk.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

            //判断创建的节点是否是最小的序号节点，如果是获取到锁，如果不是，监听序号前一个节点
            List<String> children = zk.getChildren("/locks", false);
            if (children.size()==1){
                return;
            }else {
                Collections.sort(children);

                //获取节点名称 seq-0000000
                String thisNode = currentMode.substring("/locks/".length());

                //获取 seq-0000000在children中的位置
                int index = children.indexOf(thisNode);

                if (index==-1){
                    System.out.println("数据异常");
                }else if (index ==0){
                    //就一个节点，直接获取锁
                    return;
                }else {
                    //需要监听 前面一个节点的变化
                    waitPath="/locks/" +children.get(index-1);
                    // zk.getData(waitPath, new Watcher() {
                    //     @Override
                    //     public void process(WatchedEvent watchedEvent) {
                    //         //connectLatch 如果连接上zk 可以释放
                    //         if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    //             connectLatch.countDown();
                    //         }
                    //         //waitLatch 需要释放
                    //         if (watchedEvent.getType()==Event.EventType.NodeDeleted&&watchedEvent.getPath().equals(waitPath)){
                    //             waitLatch.countDown();
                    //         }
                    //     }
                    // },null);
                    zk.getData(waitPath,true,null);
                    waitLatch.await();
                    return;
                }
            }



        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }
    //解锁
    public void zkUnLock(){

        //删除节点
        try {
            zk.delete(currentMode,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }
}
