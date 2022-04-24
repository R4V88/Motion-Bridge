package com.motionbridge.motionbridge;

import com.motionbridge.motionbridge.product.entity.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication
public class MotionbridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotionbridgeApplication.class, args);
    }
}
