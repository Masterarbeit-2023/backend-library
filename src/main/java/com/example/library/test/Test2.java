package com.example.library.test;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.annotation.parameter.HttpTrigger;


public class Test2 {

    Test test;
    @ApiFunction
    public void test(@HttpTrigger() Object object){
        System.out.println("Test test 123");
        int i = 1+1;
        for (; i<10; i++){
            System.out.println(i);
        }
    }

    @ApiFunction
    public void test2(@HttpTrigger() Object object){
        System.out.println("Test test 123");
    }
}
