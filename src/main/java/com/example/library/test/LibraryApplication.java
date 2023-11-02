package com.example.library.test;
import com.example.library.annotation.parameter.HttpTrigger;


public class LibraryApplication {

    public static void main(String[] args) {
        test(new Object());
    }

    public static void test(@HttpTrigger() Object object){
        System.out.println("Test test 123");
    }

}
