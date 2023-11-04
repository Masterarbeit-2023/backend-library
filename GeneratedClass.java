

import org.springframework.web.bind.annotation.RequestMapping;


public class Test2 {

    Test test;


    
@RequestMapping
@RequestMapping(value = test, method = RequestMethod.PUT)
public void test(Object object) {
    System.out.println("Test test 123");
    int i = 1 + 1;
    for (; i < 10; i++) {
        System.out.println(i);
    }
}

@RequestMapping(value = test2, method = RequestMethod.GET)
public void test2(Object object) {
    System.out.println("Test test 123");
}


}
