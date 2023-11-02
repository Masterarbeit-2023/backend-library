package com.example.library.test;

public class Test {
    public boolean s = true;

    public Test(){
        s = false;
    }

    public void print(String s) {
        System.out.println("Test");
        if(this.s){
            this.s = false;
        }
    }

    public void print2(String s, boolean s1) {
        System.out.println("Test2");
        if(s1){
            s1 = false;
        }
    }
}
