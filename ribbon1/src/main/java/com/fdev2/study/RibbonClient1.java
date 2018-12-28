package com.fdev2.study;


import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Null;
import java.util.concurrent.atomic.AtomicLong;

/**
 * write description
 *
 * @author Kim Seok Yoon <seokyoon.kim@linecorp.com>
 * @since 2018. 12. 24.
 */
@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
@EnableTurbine
public class RibbonClient1 {
    public static void main(String... args) {
        SpringApplication.run(RibbonClient1.class, args);
    }
}


@Configuration
@RibbonClient(name = "counter", configuration = CounterConfig.class)
class RibbonConfig {

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}


@RestController
@AllArgsConstructor
class RibbonTestController {
    private final RibbonTestService service;

    @GetMapping("/ok")
    public String ok() {
        return "OK";
    }

    @GetMapping("/badrequest")
    public String badRequest() {
        return service.badRequest();
    }

    @GetMapping("/timeout")
    public String timeout() {
        return service.timeout();
    }

    @GetMapping("/rejected")
    public String rejected() {
        return service.rejected();
    }

    @GetMapping("/excepted")
    public String excepted() {
        return service.excepted();
    }
}


@Service
class RibbonTestService {
    private final RestTemplate template;

    AtomicLong counter = new AtomicLong();

    @Autowired
    public RibbonTestService(RestTemplate template) {
        this.template = template;
    }

    @HystrixCommand(
            ignoreExceptions = RuntimeException.class,
            fallbackMethod = "fallBack",
            commandProperties = {@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10")}
    )
    public String badRequest() {
        throw new RuntimeException("badRequest");
    }


    @HystrixCommand(
            fallbackMethod = "fallBack",
            commandProperties = {@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10")}
    )
    public String timeout() {
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "timeout";
    }


    @HystrixCommand(
            fallbackMethod = "fallBack",
            commandProperties = {@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10")}
    )
    public String rejected() {
        long value = counter.incrementAndGet();

        if(value%3 == 0)
            throw new RuntimeException("3's mutiplied");


        return "rejected";
    }


    @HystrixCommand(
            fallbackMethod = "fallBack",
            commandProperties = {@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10")}
    )
    public String excepted() throws RuntimeException {
        throw new RuntimeException("excepted");
    }


    public String fallBack() {
        return "fallback";
    }

}


@AllArgsConstructor
class CounterConfig {
    private IClientConfig ribbonClientConfig;

    public IPing ribbonPing() {
        return new PingUrl();
    }

    public IRule ribbonRule() {
        return new AvailabilityFilteringRule();
    }
}
