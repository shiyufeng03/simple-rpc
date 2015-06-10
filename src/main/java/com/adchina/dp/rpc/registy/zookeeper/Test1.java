package com.adchina.dp.rpc.registy.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Test1 implements Watcher{
    ZooKeeper zk;
    String hostPort;

    Test1(String hostPort) {
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }
    
    void stopZK() throws InterruptedException{
        this.zk.close();
    }

    public void process(WatchedEvent e) {
        System.out.println(e);
    }

    public static void main(String args[]) throws Exception {
        
        String connString = "localhost:2181";
        Test1 m = new Test1(connString);
        m.startZK();
        // wait for a bit
        Thread.sleep(60000);
        
        m.stopZK();
    }
}
