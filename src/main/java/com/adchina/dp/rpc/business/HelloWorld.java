package com.adchina.dp.rpc.business;

import com.adchina.dp.rpc.business.model.Person;

public interface HelloWorld {

    public String hello(String message);
    
    public Person hello(Person person);
}
