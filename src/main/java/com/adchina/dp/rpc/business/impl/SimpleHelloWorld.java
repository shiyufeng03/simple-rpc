package com.adchina.dp.rpc.business.impl;

import com.adchina.dp.rpc.business.HelloWorld;
import com.adchina.dp.rpc.server.Service;

@Service(value=SimpleHelloWorld.class)
public class SimpleHelloWorld implements HelloWorld{

    public String hello(String message) {
        System.out.println("hello:" + message);
        
        return message + "!";
    }

}
