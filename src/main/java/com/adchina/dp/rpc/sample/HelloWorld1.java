package com.adchina.dp.rpc.sample;

import com.adchina.dp.rpc.business.HelloWorld;
import com.adchina.dp.rpc.business.model.Person;
import com.adchina.dp.rpc.client.ClientProxy;

public class HelloWorld1 {

    public static void main(String[] args) {
        ClientProxy proxy = new ClientProxy("localhost:8000");
        HelloWorld hello = proxy.create(HelloWorld.class, "");
        
        System.out.println(hello.hello("steven"));
        
        Person person = new Person();
        person.setName("stevenshi");
        person.setSex("man");
        
        System.out.println(hello.hello(person).getMessage());
    }

}
