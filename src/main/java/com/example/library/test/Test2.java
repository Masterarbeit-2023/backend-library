package com.example.library.test;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.annotation.method.HttpTrigger;
import com.example.library.util.HttpMethod;
import org.springframework.http.ResponseEntity;


public class Test2 {

    //Test test;
    @ApiFunction
    @HttpTrigger(httpMethod = HttpMethod.PUT)
    public void test(Object object){
        System.out.println("Test test 123");
        int i = 1+1;
        for (; i<10; i++){
            System.out.println(i);
        }
    }

    @ApiFunction
    @HttpTrigger
    public ResponseEntity<String> test2(Object object){
        System.out.println("Test test 123");
        return ResponseEntity.ok("Test");
    }
}
