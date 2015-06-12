package com.adchina.dp.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("start server");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
