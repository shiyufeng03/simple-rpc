package com.adchina.dp.rpc.business.impl;

import com.adchina.dp.rpc.business.HelloWorld;
import com.adchina.dp.rpc.business.model.Person;
import com.adchina.dp.rpc.server.Service;

@Service(value=HelloWorld.class)
public class SimpleHelloWorld implements HelloWorld{

    public String hello(String message) {
        System.out.println("hello:" + message);
        
        return message + "!";
    }

    public Person hello(Person person) {
        System.out.println("hello" + person.getName());
        
        person.setMessage("hello world!");
        
        return person;
    }

}
