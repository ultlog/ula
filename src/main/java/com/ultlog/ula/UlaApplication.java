package com.ultlog.ula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


/**
 * @program: ula
 * @link: github.com/ultlog/collector
 * @author: will
 * @create: 2020-05-02
 **/

@SpringBootApplication
@EntityScan(basePackageClasses = { UlaApplication.class})
public class UlaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UlaApplication.class, args);
    }

}
