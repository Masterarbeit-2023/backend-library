package com.example.library.test;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.annotation.parameter.HttpTrigger;
import com.example.library.util.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;


public class Test2 {

    Test test;
    @ApiFunction
    @HttpTrigger(httpMethod = HttpMethod.PUT)
    @RequestMapping
    public void test(Object object){
        System.out.println("Test test 123");
        int i = 1+1;
        for (; i<10; i++){
            System.out.println(i);
        }
    }

    @ApiFunction
    @HttpTrigger
    public void test2(Object object){
        System.out.println("Test test 123");
    }
}
