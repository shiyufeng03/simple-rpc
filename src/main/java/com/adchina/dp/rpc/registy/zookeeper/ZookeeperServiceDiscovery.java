package com.adchina.dp.rpc.registy.zookeeper;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adchina.dp.rpc.registy.ServiceDiscovery;

public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);
    
    private String zkAddress;
    private String instanceName;

    private CuratorFramework client;

    public ZookeeperServiceDiscovery(String zkAddress, String instanceName) {
        this.zkAddress = zkAddress;
        this.instanceName = instanceName;

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(this.zkAddress, policy);
        this.client.start();
    }

    public String discover(String serviceName) {
        try {
            String servicePath = Constant.ZOOKEEPER_RPC_SERVICE_ROOT + "/" + instanceName + "/" + serviceName;
            if (this.client.checkExists().forPath(servicePath) == null) {
                return null;
            }

            List<String> list = this.client.getChildren().forPath(servicePath);
            String address;
            if (list == null || list.size() <= 0) {
                return null;
            }
            if (list.size() == 1) {
                address = list.get(0);
            } else {
                address = list.get(RandomUtils.nextInt(0, list.size() - 1));
            }

            String addressPath = servicePath + "/" + address;
            byte[] data = this.client.getData().forPath(addressPath);

            return new String(data);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        
        return null;
    }

}
