package com.fdev2.study.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * write description
 *
 * @author Kim Seok Yoon <seokyoon.kim@linecorp.com>
 * @since 2018. 12. 24.
 */
@SpringBootApplication
public class MainServer1 {
    public static void main(String... args) {
        SpringApplication.run(MainServer1.class, args);
    }
}


@RestController
class ControllerServer1 {
    private final AtomicLong counter;

    public ControllerServer1() {
        counter = new AtomicLong(0);
    }


    @GetMapping("/get")
    public String getCounter() throws InterruptedException {
        throw new RuntimeException("merong");
//        long count = counter.incrementAndGet();
//
//        if(count%100 == 0)
//            throw new RuntimeException("merong");
//
//        return "server 1: " + count;
    }
}
