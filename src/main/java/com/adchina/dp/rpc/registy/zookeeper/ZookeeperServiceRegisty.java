package com.adchina.dp.rpc.registy.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adchina.dp.rpc.registy.ServiceRegisty;

public class ZookeeperServiceRegisty implements ServiceRegisty {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegisty.class);

    private String zkAddress;
    private String instanceName;

    private CuratorFramework client;

    public ZookeeperServiceRegisty(String zkAddress, String instanceName) throws Exception {
        this.zkAddress = zkAddress;
        this.instanceName = instanceName;

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(this.zkAddress, policy);
        this.client.start();

        if (this.client.checkExists().forPath(Constant.ZOOKEEPER_RPC_SERVICE_ROOT) == null) {
            this.client.create().withMode(CreateMode.PERSISTENT).forPath(Constant.ZOOKEEPER_RPC_SERVICE_ROOT);
            LOGGER.debug("create zookeeper path:" + Constant.ZOOKEEPER_RPC_SERVICE_ROOT);
        }

        String instancePath = Constant.ZOOKEEPER_RPC_SERVICE_ROOT + "/" + instanceName;
        if (this.client.checkExists().forPath(instancePath) == null) {
            this.client.create().withMode(CreateMode.PERSISTENT).forPath(instancePath);
            LOGGER.debug("create zookeeper path:" + instancePath);
        }
    }

    public void register(String serviceName, String serviceAddress) {
        try {
            String servicePath = Constant.ZOOKEEPER_RPC_SERVICE_ROOT + "/" + this.instanceName + "/" + serviceName;
            if (this.client.checkExists().forPath(servicePath) == null) {
                this.client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                LOGGER.debug("create zookeeper path:" + servicePath);
            }
            
            String AddressPath = servicePath + "/" + "address-";
            this.client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(AddressPath, serviceAddress.getBytes());
            LOGGER.debug("create zookeeper path:" + AddressPath + ", child data:" + serviceAddress);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

}
