package com.atguigu.zk.case3;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.security.Policy;

/**
 * @author Zz
 * @create 2022-06-28 20:27
 */
public class CuratorLockTest {

    public static void main(String[] args) {
        
        //创建分布式锁1
        InterProcessMutex lock1 = new InterProcessMutex(getCuratorFramework(),"/locks");
        //创建分布式锁2
        InterProcessMutex lock2 = new InterProcessMutex(getCuratorFramework(),"/locks");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程1获取到锁");

                    lock1.acquire();
                    System.out.println("线程1再次获取到锁");

                    Thread.sleep(5000);
                    lock1.release();
                    System.out.println("线程1释放锁");
                    lock1.release();
                    System.out.println("线程1再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.acquire();
                    System.out.println("线程2获取到锁");

                    lock2.acquire();
                    System.out.println("线程2再次获取到锁");

                    Thread.sleep(5000);
                    lock2.release();
                    System.out.println("线程2释放锁");
                    lock2.release();
                    System.out.println("线程2再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        
    }

    private static CuratorFramework getCuratorFramework() {

        ExponentialBackoffRetry Policy = new ExponentialBackoffRetry(3000,3);

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("hadoop102:2181,hadoop103:2182,hadoop104:2181")
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(2000)
                .retryPolicy(Policy)
                .build();

        //启动客户端
        client.start();
        System.out.println("zookeeper启动成功");
        return client;
    }
}
