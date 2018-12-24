package com.fdev2.study;


import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * write description
 *
 * @author Kim Seok Yoon <seokyoon.kim@linecorp.com>
 * @since 2018. 12. 24.
 */
@SpringBootApplication
@EnableHystrix
@EnableCircuitBreaker
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



    @GetMapping("/")
    public String invoke() {
        return service.invoke();
    }
}

@Service
@AllArgsConstructor
class RibbonTestService {
    private final RestTemplate template;

    @HystrixCommand(
            fallbackMethod = "fallBack",
            commandProperties = {
//                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value = "10000"),
//                    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="1000")
                    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="10")
            }
    )
    public String invoke() {
        for (int i = 0; i < 10000; i++)
            System.out.println(template.getForObject("http://counter/get", String.class));

        return "OK";
    }


    public String fallBack() {
        return "server 1: fall backed";
    }

}


//@Configuration
@AllArgsConstructor
class CounterConfig {
    private IClientConfig ribbonClientConfig;

//    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }

//    @Bean
    public IRule ribbonRule() {
        return new AvailabilityFilteringRule();
    }

}
