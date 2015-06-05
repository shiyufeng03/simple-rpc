package com.adchina.dp.rpc.registy.zookeeper;

import com.adchina.dp.rpc.registy.ServiceRegisty;

public class ZookeeperServiceRegisty  implements ServiceRegisty{
    
    private String zkService;
    
    public ZookeeperServiceRegisty(String zkService){
        this.zkService = zkService;
    }

    public void register(String serviceName, String serviceAddress) {
        // TODO Auto-generated method stub
        
    }

}
