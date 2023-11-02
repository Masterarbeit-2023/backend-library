package com.example.library.test;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.annotation.parameter.HttpTrigger;


public class Test2 {
    @ApiFunction
    public void test(@HttpTrigger() Object object){
        System.out.println("Test test 123");
    }

    @ApiFunction
    public void test2(@HttpTrigger() Object object){
        System.out.println("Test test 123");
    }
}
