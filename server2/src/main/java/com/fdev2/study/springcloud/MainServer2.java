package com.fdev2.study.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
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
@EnableEurekaClient
public class MainServer2 {
    public static void main(String... args) {
        SpringApplication.run(MainServer2.class, args);
    }
}


@RestController
class ControllerServer2 {
    private final AtomicLong counter;

    public ControllerServer2() {
        counter = new AtomicLong(0);
    }


    @GetMapping("/get")
    public String getCounter() {
        return "server 2: " + counter.incrementAndGet();
    }
}
